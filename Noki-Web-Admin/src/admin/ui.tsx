import { useEffect, useId, useRef, useState, type ReactNode } from "react";
import { Check, X, Info, LoaderCircle, ExternalLink } from "lucide-react";
import { errorMessage, getPrivatePhoto, safeHttps } from "./api";
import type { Notice } from "./types";

export const labels: Record<string, string> = {
  draft: "En preparación",
  pending: "Pendiente",
  needs_changes: "Cambios solicitados",
  approved: "Aprobado",
  suspended: "Suspendido",
  withdrawn: "Retirado",
  received: "Recepción registrada",
  completed: "Completada",
  cancelled: "Cancelada",
  phone: "Celulares",
  computer: "Computadores",
  audio_video: "Audio y video",
  accessories: "Accesorios",
  appliance: "Electrodomésticos",
  other: "Otros",
  normal: "Sin señales de riesgo",
  attention: "Requiere atención",
  unknown: "Sin confirmar",
  undecided: "Sin decisión",
  keep: "Conservar",
  repair: "Reparar",
  reuse: "Reutilizar",
  deliver: "Entregar",
  kept: "Conservado",
  repaired: "Reparado",
  reused: "Reutilizado",
  delivered: "Entregado",
  "session.open": "Inicio de sesión",
  user_detail: "Consulta de usuario",
  receiver_detail: "Consulta de receptor",
  delivery_detail: "Consulta de entrega",
  "receiver.approved": "Receptor aprobado",
  "receiver.needs_changes": "Cambios solicitados",
  "receiver.suspended": "Receptor suspendido",
  "point.save": "Lugar actualizado",
  "photo.view": "Consulta de fotografía",
  "report.export": "Informe descargado",
};
export const label = (s: string | null | undefined) =>
  s ? labels[s] || s : "No disponible";
export const date = (s: string | null | undefined) =>
  s
    ? new Intl.DateTimeFormat("es-CO", {
        dateStyle: "medium",
        timeZone: "America/Bogota",
      }).format(new Date(s.length === 10 ? s + "T12:00:00Z" : s))
    : "No disponible";
export const dateTime = (s: string | null | undefined) =>
  s
    ? new Intl.DateTimeFormat("es-CO", {
        dateStyle: "medium",
        timeStyle: "short",
        timeZone: "America/Bogota",
      }).format(new Date(s))
    : "No disponible";

export function Logo() {
  return (
    <picture>
      <source
        srcSet="/brand/noki_wordmark_dark.png"
        media="(prefers-color-scheme: dark)"
      />
      <img className="brand-logo" src="/brand/noki_wordmark.png" alt="Noki" />
    </picture>
  );
}
export function Message({
  notice,
  onClose,
}: {
  notice: Notice;
  onClose?: () => void;
}) {
  const Icon =
    notice.kind === "error" ? X : notice.kind === "success" ? Check : Info;
  return (
    <div
      className={`message ${notice.kind}`}
      role={notice.kind === "error" ? "alert" : "status"}
    >
      <Icon aria-hidden="true" size={20} />
      <span>{notice.message}</span>
      {onClose && (
        <button
          className="icon-button"
          type="button"
          aria-label="Cerrar aviso"
          onClick={onClose}
        >
          <X size={18} />
        </button>
      )}
    </div>
  );
}
export function Loading({
  text = "Cargando datos de Noki…",
}: {
  text?: string;
}) {
  return (
    <div className="loading" role="status">
      <LoaderCircle className="spin" size={22} aria-hidden="true" />
      {text}
    </div>
  );
}
export function Badge({ value, text }: { value: string; text?: string }) {
  return (
    <span className={`badge status-${value}`}>{text || label(value)}</span>
  );
}
export function Detail({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <div className="detail-item">
      <dt>{title}</dt>
      <dd>{children || "No disponible"}</dd>
    </div>
  );
}
export function External({
  href,
  children,
}: {
  href?: string | null;
  children: ReactNode;
}) {
  const safe = safeHttps(href);
  return safe ? (
    <a
      className="text-link"
      href={safe}
      target="_blank"
      rel="noopener noreferrer"
    >
      {children}
      <ExternalLink size={14} aria-hidden="true" />
      <span className="sr-only"> (abre otra pestaña)</span>
    </a>
  ) : (
    <span>Enlace no disponible</span>
  );
}
export function Modal({
  title,
  onClose,
  children,
  busy = false,
}: {
  title: string;
  onClose: () => void;
  children: ReactNode;
  busy?: boolean;
}) {
  const ref = useRef<HTMLDialogElement>(null);
  const id = useId();
  useEffect(() => {
    const dialog = ref.current;
    dialog?.showModal();
    return () => dialog?.close();
  }, []);
  return (
    <dialog
      ref={ref}
      className="modal"
      aria-labelledby={id}
      onCancel={(e) => {
        e.preventDefault();
        if (!busy) onClose();
      }}
    >
      <div className="modal-header">
        <h2 id={id}>{title}</h2>
        <button
          className="icon-button"
          aria-label="Cerrar detalle"
          onClick={onClose}
          disabled={busy}
        >
          <X />
        </button>
      </div>
      <div className="modal-body">{children}</div>
    </dialog>
  );
}
export function PrivatePhotos({
  paths,
  bucket = "noki-receiver-places",
}: {
  paths: string[];
  bucket?: string;
}) {
  const [photos, setPhotos] = useState<string[]>([]);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    setPhotos([]);
    return () => setPhotos([]);
  }, [paths.join("|")]);
  async function load() {
    setBusy(true);
    setError("");
    try {
      setPhotos(
        await Promise.all(paths.map((path) => getPrivatePhoto(bucket, path))),
      );
    } catch (e) {
      setError(errorMessage(e));
    } finally {
      setBusy(false);
    }
  }
  if (!paths.length) return <p className="muted">No hay fotos enviadas.</p>;
  return (
    <section className="photo-section">
      <h3>
        {bucket === "noki-receiver-places"
          ? "Fotos del lugar"
          : "Foto de perfil"}
      </h3>
      <p className="muted">
        Contenido privado. Esta consulta queda registrada; no compartas las
        imágenes fuera de Noki.
      </p>
      <button className="button secondary" onClick={load} disabled={busy}>
        {busy
          ? "Cargando…"
          : photos.length
            ? "Volver a cargar fotos"
            : `Ver ${paths.length === 1 ? "foto" : `${paths.length} fotos`}`}
      </button>
      {error && <Message notice={{ kind: "error", message: error }} />}
      <div className="photo-grid">
        {photos.map((src, i) => (
          <figure key={src}>
            <img
              src={src}
              alt={
                bucket === "noki-receiver-places"
                  ? `Fotografía ${i + 1} del lugar, enviada por su responsable`
                  : "Foto de perfil del usuario"
              }
              referrerPolicy="no-referrer"
            />
            <figcaption>
              {bucket === "noki-receiver-places" ? `Foto ${i + 1}` : "Perfil"}
            </figcaption>
          </figure>
        ))}
      </div>
    </section>
  );
}
