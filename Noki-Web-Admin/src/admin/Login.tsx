import { useEffect, useState, type FormEvent } from "react";
import {
  ArrowRight,
  Mail,
  ShieldCheck,
  ArrowLeft,
  KeyRound,
} from "lucide-react";
import { ADMIN_EMAILS, errorMessage, supabase } from "./api";
import { Message } from "./ui";
import BrandCircuit from "./BrandCircuit";

export default function Login({
  onVerified,
  initialError,
}: {
  onVerified: () => Promise<void>;
  initialError: string;
}) {
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [sent, setSent] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(initialError);
  const [resendAt, setResendAt] = useState(0);
  const [now, setNow] = useState(Date.now());
  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(timer);
  }, []);
  async function send(e?: FormEvent) {
    e?.preventDefault();
    if (busy || Date.now() < resendAt) return;
    setError("");
    const address = email.trim().toLowerCase();
    if (!ADMIN_EMAILS.some((allowed) => allowed === address)) {
      setError(
        "Usa uno de los correos autorizados para administrar Noki. Las cuentas de la app no tienen acceso a este sitio.",
      );
      return;
    }
    setBusy(true);
    try {
      const { error: failure } = await supabase.auth.signInWithOtp({
        email: address,
        options: { shouldCreateUser: false },
      });
      if (failure) throw failure;
      setEmail(address);
      setCode("");
      setSent(true);
      setResendAt(Date.now() + 60000);
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }
  async function verify(e: FormEvent) {
    e.preventDefault();
    if (busy) return;
    setBusy(true);
    setError("");
    try {
      const { error: failure } = await supabase.auth.verifyOtp({
        email,
        token: code.replace(/\s/g, ""),
        type: "email",
      });
      if (failure) throw failure;
      await onVerified();
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }
  return (
    <main className="login-shell" id="main">
      <section className="login-story">
        <img
          src="/brand/noki_wordmark_dark.png"
          alt="Noki"
          className="login-logo"
        />
        <span className="eyebrow">NOKI · ADMINISTRACIÓN</span>
        <h1>
          La tecnología merece <em>otro comienzo.</em>
        </h1>
        <p>
          Conecta personas, acompaña entregas y abre nuevas oportunidades para
          los aparatos que todavía tienen mucho por dar.
        </p>
        <BrandCircuit />
        <div className="story-foot">
          <span className="story-line" aria-hidden="true" />
          <span>Un mismo propósito. Una nueva oportunidad.</span>
        </div>
      </section>
      <section className="login-panel">
        <div className="login-card">
          <span className="eyebrow login-kicker">TU ESPACIO DE GESTIÓN</span>
          <span className="round-icon">{sent ? <KeyRound /> : <Mail />}</span>
          <h2>{sent ? "Revisa tu correo" : "Bienvenido de nuevo"}</h2>
          <p className="muted">
            {sent
              ? `Enviamos un código a ${email}. Escríbelo aquí para entrar.`
              : "Ingresa con el correo autorizado de tu cuenta. Te enviaremos un código de un solo uso."}
          </p>
          <ol className="access-steps" aria-label="Etapas del acceso">
            <li aria-current={!sent ? "step" : undefined}>
              <span aria-hidden="true">01</span>Tu correo
            </li>
            <li aria-current={sent ? "step" : undefined}>
              <span aria-hidden="true">02</span>Tu código
            </li>
          </ol>
          {error && <Message notice={{ kind: "error", message: error }} />}
          <form onSubmit={sent ? verify : send} className="stack">
            {sent ? (
              <label>
                Código de acceso
                <input
                  key="otp"
                  autoFocus
                  inputMode="numeric"
                  autoComplete="one-time-code"
                  pattern="[0-9]{6,10}"
                  maxLength={10}
                  value={code}
                  onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))}
                  required
                  placeholder="Escribe el código"
                  disabled={busy}
                />
              </label>
            ) : (
              <label>
                Correo electrónico
                <input
                  key="email"
                  type="email"
                  autoComplete="email"
                  placeholder="tu.correo@ejemplo.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  disabled={busy}
                />
              </label>
            )}
            <button className="button primary full" disabled={busy}>
              {busy ? "Un momento…" : sent ? "Entrar a Noki" : "Recibir código"}
              <ArrowRight size={19} />
            </button>
          </form>
          {sent && (
            <div className="stack">
              <button
                className="button ghost full"
                disabled={busy || now < resendAt}
                onClick={() => void send()}
              >
                {now < resendAt
                  ? `Reenviar en ${Math.ceil((resendAt - now) / 1000)} s`
                  : "Enviar un código nuevo"}
              </button>
              <button
                className="text-button"
                disabled={busy}
                onClick={() => {
                  setSent(false);
                  setCode("");
                  setError("");
                }}
              >
                <ArrowLeft size={16} />
                Usar otro correo
              </button>
              <p className="small muted">
                Si no lo encuentras, revisa Spam. Usa el último código recibido
                y no lo compartas.
              </p>
            </div>
          )}
          <div className="login-note">
            <ShieldCheck size={18} />
            <p>
              Solo el equipo autorizado puede entrar. No necesitas cambiar la
              contraseña que usas en la app.
            </p>
          </div>
        </div>
        <p className="login-footer">
          Noki · Dale una nueva oportunidad a tus aparatos.
        </p>
      </section>
    </main>
  );
}
