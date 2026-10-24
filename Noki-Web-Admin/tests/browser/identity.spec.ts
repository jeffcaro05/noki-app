import { test, expect } from "@playwright/test";
import axe from "axe-core";
import { mockBackend } from "./fixtures";

test("entrada tecnológica finita, decorativa y sin bloquear el formulario", async ({
  page,
  context,
}) => {
  await mockBackend(context, false);
  await page.setViewportSize({ width: 1366, height: 768 });
  await page.goto("/");
  const email = page.getByLabel("Correo electrónico");
  await expect(email).toBeVisible();
  await email.click();
  await expect(email).toBeFocused();
  await expect(page.locator(".brand-circuit")).toHaveAttribute(
    "aria-hidden",
    "true",
  );
  const animations = await page
    .locator(".brand-circuit")
    .evaluate((el) =>
      el
        .getAnimations({ subtree: true })
        .map((animation) => animation.effect!.getTiming()),
    );
  expect(animations.length).toBeGreaterThan(0);
  for (const animation of animations) {
    expect(animation.iterations).toBe(1);
    expect(Number(animation.duration) + animation.delay).toBeLessThanOrEqual(
      4000,
    );
  }
  await expect
    .poll(
      () =>
        page
          .locator(".brand-circuit")
          .evaluate(
            (el) =>
              el
                .getAnimations({ subtree: true })
                .filter((animation) => animation.playState === "running")
                .length,
          ),
      { timeout: 6000 },
    )
    .toBe(0);
  await page.screenshot({
    path: "test-results/identity-login-desktop.png",
    fullPage: true,
  });
  expect(
    await page
      .getByRole("button", { name: "Recibir código" })
      .evaluate((el) => el.getBoundingClientRect().bottom <= innerHeight),
  ).toBe(true);
});

for (const variant of [
  {
    name: "móvil claro",
    width: 390,
    scale: "100",
    theme: "light",
    contrast: "normal",
  },
  {
    name: "móvil oscuro",
    width: 390,
    scale: "100",
    theme: "dark",
    contrast: "normal",
  },
  {
    name: "tablet oscuro 150",
    width: 820,
    scale: "150",
    theme: "dark",
    contrast: "normal",
  },
  {
    name: "estrecho 200 alto contraste",
    width: 320,
    scale: "200",
    theme: "dark",
    contrast: "high",
  },
]) {
  test(`identidad accesible: ${variant.name}`, async ({ page, context }) => {
    await mockBackend(context, false);
    await context.addInitScript((settings) => {
      localStorage.setItem("noki-admin-theme", settings.theme);
      localStorage.setItem("noki-admin-scale", settings.scale);
      localStorage.setItem("noki-admin-contrast", settings.contrast);
      localStorage.setItem("noki-admin-motion", "reduce");
    }, variant);
    await page.setViewportSize({ width: variant.width, height: 844 });
    await page.goto("/");
    await expect(
      page.getByRole("heading", { name: "Bienvenido de nuevo" }),
    ).toBeVisible();
    expect(
      await page.evaluate(
        () => document.documentElement.scrollWidth <= innerWidth,
      ),
    ).toBe(true);
    expect(
      await page
        .locator(".circuit-ring")
        .evaluate((el) => getComputedStyle(el).animationName),
    ).toBe("none");
    await page.addScriptTag({ content: axe.source });
    expect((await page.evaluate(() => window.axe.run())).violations).toEqual(
      [],
    );
    const email = page.getByLabel("Correo electrónico");
    await email.click();
    await page.keyboard.press("Tab");
    await expect(
      page.getByRole("button", { name: "Recibir código" }),
    ).toBeFocused();
    await page.screenshot({
      path: `test-results/identity-${variant.width}-${variant.theme}-${variant.scale}.png`,
      fullPage: true,
    });
    await page.getByLabel("Opciones de accesibilidad").click();
    await expect(page.getByLabel("Apariencia")).toBeVisible();
    expect(
      await page.evaluate(
        () => document.documentElement.scrollWidth <= innerWidth,
      ),
    ).toBe(true);
  });
}

test("reducción de movimiento del sistema sin preferencia guardada", async ({
  page,
  context,
}) => {
  await mockBackend(context, false);
  await page.emulateMedia({ reducedMotion: "reduce" });
  await page.goto("/");
  await expect(page.getByLabel("Correo electrónico")).toBeVisible();
  expect(
    await page
      .locator(".brand-circuit")
      .evaluate((el) => el.getAnimations({ subtree: true }).length),
  ).toBe(0);
  expect(
    await page
      .locator(".circuit-core")
      .evaluate((el) => getComputedStyle(el).opacity),
  ).toBe("1");
});

test("indicador de correo y código conserva el flujo OTP y el foco", async ({
  page,
  context,
}) => {
  await mockBackend(context, false);
  await page.setViewportSize({ width: 390, height: 844 });
  await page.goto("/");
  await expect(
    page.locator('.access-steps [aria-current="step"]'),
  ).toContainText("Tu correo");
  await page.getByLabel("Correo electrónico").fill("caroajeffry@gmail.com");
  await page.getByRole("button", { name: "Recibir código" }).click();
  await expect(
    page.locator('.access-steps [aria-current="step"]'),
  ).toContainText("Tu código");
  await expect(page.getByLabel("Código de acceso")).toBeFocused();
  await expect(
    page.getByRole("button", { name: /Reenviar en/ }),
  ).toBeDisabled();
  await page.addScriptTag({ content: axe.source });
  expect((await page.evaluate(() => window.axe.run())).violations).toEqual([]);
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= innerWidth,
    ),
  ).toBe(true);
  await page.screenshot({
    path: "test-results/identity-login-code.png",
    fullPage: true,
  });
  await page.getByRole("button", { name: "Usar otro correo" }).click();
  await expect(
    page.locator('.access-steps [aria-current="step"]'),
  ).toContainText("Tu correo");
});
