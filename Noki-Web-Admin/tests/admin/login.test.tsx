import {
  fireEvent,
  render,
  screen,
  waitFor,
  cleanup,
} from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import Login from "../../src/admin/Login";
const mocks = vi.hoisted(() => ({ send: vi.fn(), verify: vi.fn() }));
vi.mock("../../src/admin/api", async (importOriginal) => {
  const original = await importOriginal<typeof import("../../src/admin/api")>();
  return {
    ...original,
    supabase: { auth: { signInWithOtp: mocks.send, verifyOtp: mocks.verify } },
  };
});
beforeEach(() => {
  vi.clearAllMocks();
  mocks.send.mockResolvedValue({ error: null });
  mocks.verify.mockResolvedValue({ error: null });
});
afterEach(cleanup);
describe("Acceso real por código, sin registro administrativo público", () => {
  it("rechaza otros correos antes de pedir el envío", async () => {
    render(<Login initialError="" onVerified={vi.fn()} />);
    fireEvent.change(screen.getByLabelText("Correo electrónico"), {
      target: { value: "persona@example.com" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Recibir código" }));
    expect(await screen.findByRole("alert")).toHaveTextContent(
      "correos autorizados",
    );
    expect(mocks.send).not.toHaveBeenCalled();
  });
  it.each(["caroajeffry@gmail.com", "oykmelissa@gmail.com"])(
    "permite %s sin crear una cuenta duplicada",
    async (email) => {
      render(<Login initialError="" onVerified={vi.fn()} />);
      fireEvent.change(screen.getByLabelText("Correo electrónico"), {
        target: { value: email.toUpperCase() },
      });
      fireEvent.click(screen.getByRole("button", { name: "Recibir código" }));
      await screen.findByLabelText("Código de acceso");
      expect(mocks.send).toHaveBeenCalledExactlyOnceWith({
        email,
        options: { shouldCreateUser: false },
      });
      expect(
        screen.getByRole("button", { name: /Reenviar en/ }),
      ).toBeDisabled();
    },
  );
  it("verifica el código con Auth y después valida el permiso en el servidor", async () => {
    const open = vi.fn().mockResolvedValue(undefined);
    render(<Login initialError="" onVerified={open} />);
    fireEvent.change(screen.getByLabelText("Correo electrónico"), {
      target: { value: "caroajeffry@gmail.com" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Recibir código" }));
    fireEvent.change(await screen.findByLabelText("Código de acceso"), {
      target: { value: "12345678" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Entrar a Noki" }));
    await waitFor(() => expect(open).toHaveBeenCalledOnce());
    expect(mocks.verify).toHaveBeenCalledWith({
      email: "caroajeffry@gmail.com",
      token: "12345678",
      type: "email",
    });
  });
  it("no entra cuando el código es inválido", async () => {
    mocks.verify.mockResolvedValue({
      error: { message: "Token has expired or is invalid" },
    });
    const open = vi.fn();
    render(<Login initialError="" onVerified={open} />);
    fireEvent.change(screen.getByLabelText("Correo electrónico"), {
      target: { value: "caroajeffry@gmail.com" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Recibir código" }));
    fireEvent.change(await screen.findByLabelText("Código de acceso"), {
      target: { value: "000000" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Entrar a Noki" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("no es válido");
    expect(open).not.toHaveBeenCalled();
  });
  it("muestra errores de envío sin fingir que se envió el correo", async () => {
    mocks.send.mockResolvedValue({
      error: { message: "Network request failed" },
    });
    render(<Login initialError="" onVerified={vi.fn()} />);
    fireEvent.change(screen.getByLabelText("Correo electrónico"), {
      target: { value: "caroajeffry@gmail.com" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Recibir código" }));
    expect(await screen.findByRole("alert")).toHaveTextContent("conexión");
    expect(screen.queryByLabelText("Código de acceso")).not.toBeInTheDocument();
  });
});
