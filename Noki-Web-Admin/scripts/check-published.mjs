// Read-only production smoke test. Never logs in, sends mail, or changes records.
import assert from 'node:assert/strict';
import { chromium } from '@playwright/test';
import { mkdir } from 'node:fs/promises';

const site = 'https://noki-89c7b.web.app';
const response = await fetch(site);
assert.equal(response.status, 200);
assert.match(response.headers.get('content-security-policy') || '', /frame-ancestors 'none'/);
assert.match(response.headers.get('cache-control') || '', /no-store/);
assert.equal(response.headers.get('x-content-type-options'), 'nosniff');
assert.equal(response.headers.get('x-frame-options'), 'DENY');
assert.equal(response.headers.get('referrer-policy'), 'no-referrer');
const denied = await fetch('https://cwsxjsmelrcfhbtbkssd.supabase.co/rest/v1/rpc/noki_admin_action', {
  method: 'POST',
  headers: {
    apikey: 'sb_publishable_D6FWErgyxMpTN4cZfPLoSw_PkrH-cpa',
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({ op: 'overview', args: {} }),
});
assert.ok([401, 403].includes(denied.status), 'La API administrativa debe rechazar consultas sin sesión.');

const browser = await chromium.launch({ channel: 'msedge', headless: true });
try {
  const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } });
  const page = await context.newPage();
  const failures = [];
  page.on('pageerror', error => failures.push(error.message));
  page.on('console', message => { if (message.type() === 'error') failures.push(message.text()); });
  await page.goto(site, { waitUntil: 'networkidle' });
  await page.getByRole('heading', { name: 'Bienvenido de nuevo' }).waitFor();
  await page.getByRole('heading', { name: 'La tecnología merece otro comienzo.' }).waitFor();
  assert.ok(await page.locator('.login-logo').evaluate(img => img.complete && img.naturalWidth > 0));
  assert.equal(await page.locator('.brand-circuit').getAttribute('aria-hidden'), 'true');
  assert.ok(await page.locator('.circuit-core img').evaluate(img => img.complete && img.naturalWidth > 0));
  assert.match(await page.locator('.access-steps [aria-current="step"]').innerText(), /Tu correo/);
  await mkdir('test-results/published', { recursive: true });
  await page.screenshot({ path: 'test-results/published/login-desktop.png', fullPage: true });
  await page.emulateMedia({ reducedMotion: 'reduce' });
  assert.equal(await page.locator('.brand-circuit').evaluate(el => el.getAnimations({ subtree: true }).length), 0);
  await page.setViewportSize({ width: 390, height: 844 });
  assert.ok(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth));
  await page.getByLabel('Correo electrónico').fill('persona@example.com');
  let authRequests = 0;
  page.on('request', req => { if (req.url().includes('/auth/v1/')) authRequests++; });
  await page.getByRole('button', { name: 'Recibir código' }).click();
  await page.getByRole('alert').filter({ hasText: 'correos autorizados' }).waitFor();
  assert.equal(authRequests, 0, 'Un correo no autorizado no debe solicitar ningún OTP.');
  await page.screenshot({ path: 'test-results/published/login-mobile.png', fullPage: true });
  assert.deepEqual(failures, [], 'No debe haber errores JavaScript, recursos bloqueados o fallos de CSP.');
  console.log('PASS: identidad visual actualizada, movimiento reducido, HTTPS, cabeceras, recursos, móvil, rechazo local de correo no autorizado y API privada sin sesión.');
} finally {
  await browser.close();
}
