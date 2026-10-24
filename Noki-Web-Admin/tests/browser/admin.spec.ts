import { test, expect } from "@playwright/test";
import axe from "axe-core";
import { mockBackend } from "./fixtures";

test("entrada, errores y accesibilidad sin sesión", async ({
  page,
  context,
}) => {
  await mockBackend(context, false);
  await page.goto("/");
  await expect(
    page.getByRole("heading", { name: "Bienvenido de nuevo" }),
  ).toBeVisible();
  await page.getByLabel("Correo electrónico").fill("persona@example.com");
  await page.getByRole("button", { name: "Recibir código" }).click();
  await expect(page.getByRole("alert")).toContainText("correos autorizados");
  await page.addScriptTag({ content: axe.source });
  const result = await page.evaluate(() => window.axe.run());
  expect(result.violations).toEqual([]);
  await page.screenshot({
    path: "test-results/login-desktop.png",
    fullPage: true,
  });
});

test("navegación, revisión, informes y todos los módulos", async ({
  page,
  context,
}) => {
  const calls = await mockBackend(context);
  await page.setViewportSize({ width: 1440, height: 1000 });
  await page.goto("/");
  await expect(
    page.getByRole("heading", { name: "Hola, Jeffry" }),
  ).toBeVisible();
  await page.addScriptTag({ content: axe.source });
  expect((await page.evaluate(() => window.axe.run())).violations).toEqual([]);
  await page.screenshot({
    path: "test-results/dashboard-desktop.png",
    fullPage: true,
  });
  await page.getByRole("link", { name: "Usuarios", exact: true }).click();
  await expect(
    page.getByText("Persona de prueba", { exact: true }),
  ).toBeVisible();
  await page.getByRole("button", { name: /Ver detalle/ }).click();
  await expect(page.getByRole("dialog")).toBeVisible();
  await expect(page.getByText("Aparatos asociados")).toBeVisible();
  await page.keyboard.press("Escape");
  await page.getByRole("link", { name: "Receptores", exact: true }).click();
  await page.getByRole("button", { name: /Ver detalle/ }).click();
  await expect(
    page.getByRole("button", { name: "Confirmar aprobación" }),
  ).toBeDisabled();
  await page
    .getByLabel("Nota para el responsable")
    .fill("Datos revisados en una prueba aislada.");
  await page.getByRole("checkbox").check();
  await page.getByRole("button", { name: "Confirmar aprobación" }).click();
  await expect(
    page.getByRole("status").filter({ hasText: "Receptor aprobado." }),
  ).toBeVisible();
  expect(calls.filter((c) => c.op === "review_receiver")).toHaveLength(1);
  await page
    .getByRole("link", { name: "Lugares de entrega", exact: true })
    .click();
  await page.getByRole("button", { name: /Ver detalle/ }).click();
  await page
    .getByRole("button", { name: "Ver solicitud del receptor" })
    .click();
  await expect(
    page.getByRole("dialog", { name: "Solicitud de receptor" }),
  ).toBeVisible();
  await page.keyboard.press("Escape");
  await page.getByRole("link", { name: "Entregas", exact: true }).click();
  await page.getByRole("button", { name: /Ver detalle/ }).click();
  await expect(page.getByText("Computador portátil de prueba")).toBeVisible();
  await expect(
    page.getByRole("button", { name: /confirmar recepción/i }),
  ).toHaveCount(0);
  await page.keyboard.press("Escape");
  await page.getByRole("link", { name: "Informes", exact: true }).click();
  const downloadPromise = page.waitForEvent("download");
  await page.getByRole("button", { name: "Descargar CSV" }).click();
  expect((await downloadPromise).suggestedFilename()).toMatch(
    /Noki-informe.*\.csv/,
  );
  await page
    .getByRole("link", { name: "Actividad del equipo", exact: true })
    .click();
  await expect(page.getByText("Revisión de prueba")).toBeVisible();
});

test("teléfono, texto grande, modo oscuro y teclado", async ({
  page,
  context,
}) => {
  await mockBackend(context);
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto("/");
  await expect(
    page.getByRole("heading", { name: "Hola, Jeffry" }),
  ).toBeVisible();
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= innerWidth,
    ),
  ).toBe(true);
  await page.getByLabel("Opciones de accesibilidad").click();
  await page.getByLabel("Apariencia").selectOption("dark");
  await page.getByLabel("Tamaño del texto").selectOption("200");
  await page.getByLabel("Reducir movimiento").check();
  await page.getByLabel("Opciones de accesibilidad").click();
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= innerWidth,
    ),
  ).toBe(true);
  await page.screenshot({
    path: "test-results/dashboard-mobile-large-dark.png",
    fullPage: true,
  });
  await page.getByRole("button", { name: "Abrir menú de navegación" }).click();
  await page.getByRole("link", { name: "Receptores", exact: true }).click();
  await expect(
    page.getByRole("heading", { name: "Receptores", exact: true }),
  ).toBeVisible();
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= innerWidth,
    ),
  ).toBe(true);
  await page.getByRole("button", { name: /Ver detalle/ }).click();
  await expect(page.getByRole("dialog")).toBeVisible();
  await page.addScriptTag({ content: axe.source });
  expect((await page.evaluate(() => window.axe.run())).violations).toEqual([]);
  await page.keyboard.press("Escape");
  await expect(page.getByRole("dialog")).toHaveCount(0);
});
