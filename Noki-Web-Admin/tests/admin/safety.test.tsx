import { describe, expect, it, afterEach } from "vitest";
import { render, screen, cleanup } from "@testing-library/react";
import { errorMessage, safeHttps } from "../../src/admin/api";
import { csvCell } from "../../src/admin/Overview";
import { External, Message, label } from "../../src/admin/ui";
afterEach(cleanup);
describe("Seguridad de presentación", () => {
  it.each([
    "javascript:alert(1)",
    "data:text/html,<script>alert(1)</script>",
    "http://example.com",
    "//example.com",
  ])("no convierte %s en enlace", (url) =>
    expect(safeHttps(url)).toBeUndefined(),
  );
  it("permite fuentes HTTPS", () =>
    expect(safeHttps("https://example.com/map")).toBe(
      "https://example.com/map",
    ));
  it("no muestra enlaces ejecutables desde datos del receptor", () => {
    render(<External href="javascript:alert(1)">Ver fuente</External>);
    expect(screen.queryByRole("link")).not.toBeInTheDocument();
  });
  it.each(['=HYPERLINK("http://x")', "+cmd", "-cmd", "@SUM(A1)", "\tformula"])(
    "neutraliza fórmulas de CSV: %s",
    (value) => expect(csvCell(value)).toMatch(/^"'/),
  );
  it("escapa comillas CSV", () =>
    expect(csvCell('Lugar "A"')).toBe('"Lugar ""A"""'));
  it("los errores tienen semántica y símbolo, no solo color", () => {
    render(<Message notice={{ kind: "error", message: "Algo falló" }} />);
    expect(screen.getByRole("alert")).toHaveClass("error");
    expect(screen.getByRole("alert").querySelector("svg")).toBeTruthy();
  });
  it("los éxitos se anuncian sin interrumpir", () => {
    render(<Message notice={{ kind: "success", message: "Guardado" }} />);
    expect(screen.getByRole("status")).toHaveClass("success");
  });
  it("no muestra detalles internos de una excepción SQL", () =>
    expect(
      errorMessage({ message: "SQL internal credential private table" }),
    ).not.toContain("SQL"));
  it("explica conflictos de edición en lenguaje claro", () =>
    expect(errorMessage({ message: "NOKI_ADMIN_CONFLICT" })).toContain(
      "versión más reciente",
    ));
  it("distingue pendiente de completada", () => {
    expect(label("pending")).toBe("Pendiente");
    expect(label("completed")).toBe("Completada");
  });
});
