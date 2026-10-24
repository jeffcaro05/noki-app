/// <reference types="vite/client" />
import { createClient } from "@supabase/supabase-js";

export const ADMIN_EMAILS = [
  "caroajeffry@gmail.com",
  "oykmelissa@gmail.com",
] as const;
export const supabase = createClient(
  import.meta.env.VITE_SUPABASE_URL ||
    "https://cwsxjsmelrcfhbtbkssd.supabase.co",
  import.meta.env.VITE_SUPABASE_PUBLISHABLE_KEY ||
    "sb_publishable_D6FWErgyxMpTN4cZfPLoSw_PkrH-cpa",
  {
    auth: {
      storage: window.sessionStorage,
      storageKey: "noki-admin-auth-v2",
      persistSession: true,
      autoRefreshToken: true,
      detectSessionInUrl: false,
    },
  },
);

export function errorMessage(error: unknown): string {
  const code =
    typeof error === "object" && error !== null && "message" in error
      ? String(error.message)
      : String(error);
  const messages: Record<string, string> = {
    NOKI_ADMIN_EXPIRED:
      "Tu sesión administrativa terminó. Solicita un código nuevo para continuar.",
    NOKI_ADMIN_AUTH: "Vuelve a entrar con un código enviado a tu correo.",
    NOKI_ADMIN_DENIED:
      "Esta cuenta no tiene acceso a la administración de Noki.",
    NOKI_ADMIN_OTP_REQUIRED:
      "Para administrar Noki necesitas un código nuevo enviado a tu correo.",
    NOKI_ADMIN_SELF_REVIEW:
      "El otro administrador debe revisar tu solicitud. No puedes aprobar ni suspender tu propio perfil.",
    NOKI_ADMIN_CONFLICT:
      "Este registro cambió mientras lo revisabas. Cierra el detalle, actualiza y revisa la versión más reciente.",
    NOKI_ADMIN_REVIEW_INCOMPLETE:
      "Faltan las fotos del lugar, el contacto o un estado válido para aprobar. Solicita los cambios necesarios.",
    NOKI_ADMIN_VALIDATION:
      "Revisa los campos obligatorios y escribe una nota de al menos 10 caracteres.",
    NOKI_LOCATION:
      "Falta la ubicación exacta del lugar. Solicita al receptor que la complete.",
    NOKI_ADMIN_NOT_FOUND:
      "Este registro ya no está disponible. Actualiza la lista.",
    NOKI_ADMIN_LINKED_POINT:
      "Este lugar pertenece a un receptor. Sus datos se gestionan desde su solicitud.",
  };
  for (const [key, value] of Object.entries(messages))
    if (code.includes(key)) return value;
  if (/expired|invalid.*token|otp_expired/i.test(code))
    return "El código no es válido o ya venció. Revisa el último correo o solicita uno nuevo.";
  if (/rate.limit|too many|security purposes|over_email/i.test(code))
    return "Espera un momento antes de solicitar otro código.";
  if (/fetch|network|offline|timeout/i.test(code))
    return "No pudimos conectar. Revisa tu conexión e inténtalo de nuevo; no se confirmó ningún cambio.";
  return "No se pudo completar la solicitud. Inténtalo de nuevo. Si persiste, conserva esta pantalla y contacta al otro administrador.";
}

export async function api<T>(
  op: string,
  args: Record<string, unknown> = {},
): Promise<T> {
  const { data, error } = await supabase.rpc("noki_admin_action", { op, args });
  if (error) {
    if (
      op !== "open" &&
      /NOKI_ADMIN_(AUTH|EXPIRED|DENIED)|JWT expired/.test(error.message)
    )
      window.dispatchEvent(new CustomEvent("noki-admin-expired"));
    throw error;
  }
  return data as T;
}

export async function getPrivatePhoto(bucket: string, path: string) {
  await api("photo_access", { bucket, path });
  const { data, error } = await supabase.storage
    .from(bucket)
    .createSignedUrl(path, 60);
  if (error) throw error;
  return data.signedUrl;
}

export function safeHttps(url: string | null | undefined) {
  try {
    const parsed = new URL(url || "");
    return parsed.protocol === "https:" ? parsed.href : undefined;
  } catch {
    return undefined;
  }
}
