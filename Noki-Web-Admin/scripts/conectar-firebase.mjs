// User-operated Firebase authorization. Credentials are managed by Firebase CLI.
import { spawnSync } from 'node:child_process';
import { existsSync } from 'node:fs';
import { delimiter, dirname, resolve } from 'node:path';
import { createInterface } from 'node:readline';

const pnpm = resolve(dirname(process.execPath), '../node_modules/pnpm/bin/pnpm.cjs');
const childEnvironment = { ...process.env };
const pathKey = Object.keys(childEnvironment).find(key => key.toLowerCase() === 'path') || 'PATH';
// Process-only PATH; no changes to Windows settings or execution policy.
childEnvironment[pathKey] = dirname(process.execPath) + delimiter + (childEnvironment[pathKey] || '');

function firebase(args, capture = false) {
  return spawnSync(process.execPath, [pnpm, 'dlx', 'firebase-tools@15.34.0', ...args], {
    stdio: capture ? ['ignore', 'pipe', 'pipe'] : 'inherit',
    encoding: 'utf8', env: childEnvironment,
  });
}

function connected() {
  // This JSON includes tokens: inspect only in memory, never print the result.
  const accounts = firebase(['login:list', '--json', '--non-interactive'], true);
  try {
    const result = JSON.parse(accounts.stdout);
    return Array.isArray(result.result) && result.result.length > 0;
  } catch { return false; }
}

async function holdResult() {
  if (!process.stdin.isTTY) return;
  const prompt = createInterface({ input: process.stdin, output: process.stdout });
  await new Promise(done => {
    prompt.once('close', done);
    prompt.once('SIGINT', () => prompt.close());
    prompt.question('\nPulsa Enter para cerrar. No pegues códigos aquí. ', () => prompt.close());
  });
}

if (!existsSync(pnpm)) {
  console.error('Abre este asistente con el Node.js incluido en Codex.');
  await holdResult();
  process.exit(1);
}
if (process.argv.includes('--check')) {
  const checked = firebase(['--version']);
  if (checked.error) console.error('No se pudo iniciar Firebase.');
  process.exit(checked.status ?? 1);
}

try {
  if (!connected()) {
    console.log('\nCONECTAR NOKI CON FIREBASE\n');
    console.log('1. Se abrirá el navegador. Autoriza la cuenta de Google con acceso a noki-89c7b.');
    console.log('2. Mantén esta ventana abierta mientras completas el acceso.');
    console.log('3. Espera el resultado aquí. No envíes contraseñas ni códigos al chat.\n');
    // Official interactive override for a human-operated visible terminal.
    // Standard local callback: this helper never collects authorization codes.
    // Reauth skips repetitive optional telemetry consent prompts.
    const attempt = firebase(['login', '--interactive', '--reauth']);
    if (attempt.error) console.error('No se pudo iniciar el asistente de Firebase.');
  }
  // Check saved state even if a Windows child exits abnormally after saving.
  if (connected()) {
    console.log('\nCUENTA CONECTADA. Vuelve al chat y avisa: «Firebase conectado».');
    console.log('Después se comprobará el permiso de publicación del proyecto.\n');
  } else {
    process.exitCode = 1;
    console.error('\nNO SE COMPLETÓ LA CONEXIÓN. No se publicó ningún cambio.');
    console.error('Indica en el chat el error anterior, sin códigos ni datos de acceso.\n');
  }
} catch {
  process.exitCode = 1;
  console.error('\nNo se pudo comprobar la conexión. No se publicó ningún cambio.\n');
}
await holdResult();
