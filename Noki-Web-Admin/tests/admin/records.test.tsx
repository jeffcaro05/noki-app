import {
  fireEvent,
  render,
  screen,
  waitFor,
  cleanup,
} from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import Records from "../../src/admin/Records";
const mocks = vi.hoisted(() => ({ api: vi.fn() }));
vi.mock("../../src/admin/api", async (importOriginal) => ({
  ...(await importOriginal<typeof import("../../src/admin/api")>()),
  api: mocks.api,
}));
const admin = { id: "admin-test", email: "admin@example.com", name: "Equipo" };
beforeEach(() => {
  vi.clearAllMocks();
  mocks.api.mockResolvedValue({
    items: [],
    total: 0,
    pageSize: 25,
    offset: 0,
    checkedAt: "2026-10-20T12:00:00Z",
  });
});
afterEach(cleanup);
describe("Registros reales, filtros y errores", () => {
  it("no rellena un estado vacío con registros de ejemplo", async () => {
    render(
      <Records
        section="users"
        revision={0}
        refresh={vi.fn()}
        admin={admin}
        notify={vi.fn()}
      />,
    );
    expect(
      await screen.findByText("Todavía no hay registros"),
    ).toBeInTheDocument();
    expect(mocks.api).toHaveBeenCalledWith("users", {
      q: "",
      status: "",
      offset: 0,
    });
  });
  it("explica un error de conexión y no lo convierte en cero usuarios", async () => {
    mocks.api.mockRejectedValue({ message: "Failed to fetch" });
    render(
      <Records
        section="users"
        revision={0}
        refresh={vi.fn()}
        admin={admin}
        notify={vi.fn()}
      />,
    );
    expect(await screen.findByRole("alert")).toHaveTextContent("conexión");
    expect(
      screen.queryByText("Todavía no hay registros"),
    ).not.toBeInTheDocument();
  });
  it("envía el filtro al servidor y vuelve a la primera página", async () => {
    render(
      <Records
        section="receivers"
        revision={0}
        refresh={vi.fn()}
        admin={admin}
        notify={vi.fn()}
      />,
    );
    await screen.findByText("Todavía no hay registros");
    fireEvent.change(screen.getByLabelText("Filtrar por estado"), {
      target: { value: "pending" },
    });
    await waitFor(() =>
      expect(mocks.api).toHaveBeenLastCalledWith("receivers", {
        q: "",
        status: "pending",
        offset: 0,
      }),
    );
  });
  it("consulta de nuevo cuando llega una notificación de cambio", async () => {
    const props = {
      section: "users" as const,
      refresh: vi.fn(),
      admin,
      notify: vi.fn(),
    };
    const { rerender } = render(<Records {...props} revision={0} />);
    await screen.findByText("Todavía no hay registros");
    rerender(<Records {...props} revision={1} />);
    await waitFor(() => expect(mocks.api).toHaveBeenCalledTimes(2));
  });
  it("la paginación no solicita páginas inexistentes", async () => {
    render(
      <Records
        section="users"
        revision={0}
        refresh={vi.fn()}
        admin={admin}
        notify={vi.fn()}
      />,
    );
    expect(
      await screen.findByRole("button", { name: "Página siguiente" }),
    ).toBeDisabled();
    expect(
      screen.getByRole("button", { name: "Página anterior" }),
    ).toBeDisabled();
  });
});
