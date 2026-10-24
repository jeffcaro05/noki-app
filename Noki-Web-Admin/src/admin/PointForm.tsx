import { useState, type FormEvent } from "react";
import { api, errorMessage } from "./api";
import type { Point } from "./types";
import { Badge, date, Detail, External, label, Message, Modal } from "./ui";

const categories = [
  "phone",
  "computer",
  "audio_video",
  "accessories",
  "appliance",
  "other",
];
export default function PointForm({
  point,
  onClose,
  onSaved,
  onReceiver,
}: {
  point?: Point;
  onClose: () => void;
  onSaved: () => void;
  onReceiver?: (id: string) => void;
}) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [confirmed, setConfirmed] = useState(false);
  const [chosen, setChosen] = useState(point?.accepted_categories || []);
  const linked = !!point?.receiver_request_id;
  async function save(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (busy || !confirmed) return;
    const fields = Object.fromEntries(new FormData(e.currentTarget).entries());
    setBusy(true);
    setError("");
    try {
      await api("save_point", {
        ...fields,
        id: point?.id || null,
        expected: point || null,
        latitude: Number(fields.latitude),
        longitude: Number(fields.longitude),
        active: fields.active === "true",
        accepted_categories: chosen,
      });
      onSaved();
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
    }
  }
  return (
    <Modal
      title={
        linked
          ? "Lugar de un receptor"
          : point
            ? "Editar lugar de referencia"
            : "Añadir lugar de referencia"
      }
      onClose={onClose}
      busy={busy}
    >
      {linked && point ? (
        <>
          <h3>{point.name}</h3>
          <Badge
            value={point.active ? "approved" : "suspended"}
            text={point.active ? "Visible en la app" : "Oculto"}
          />
          <dl className="detail-grid">
            <Detail title="Dirección">{point.address}</Detail>
            <Detail title="Horarios">{point.hours_text}</Detail>
            <Detail title="Aparatos">
              {point.accepted_categories.map(label).join(", ")}
            </Detail>
            <Detail title="Condiciones">{point.accepted_details}</Detail>
            <Detail title="Contacto">
              {point.whatsapp ? (
                <External href={`https://wa.me/${point.whatsapp}`}>
                  WhatsApp +{point.whatsapp}
                </External>
              ) : (
                point.phone
              )}
            </Detail>
            <Detail title="Ubicación">
              <External href={point.source_url}>Ver mapa</External>
            </Detail>
            <Detail title="Última revisión">{date(point.verified_at)}</Detail>
          </dl>
          <Message
            notice={{
              kind: "info",
              message:
                "Este lugar se publica desde la solicitud del receptor. Sus datos y su aprobación se gestionan desde esa solicitud, sin duplicar el lugar.",
            }}
          />
          {onReceiver && (
            <button
              className="button primary"
              onClick={() => onReceiver(point.receiver_request_id!)}
            >
              Ver solicitud del receptor
            </button>
          )}
          <button className="button secondary" onClick={onClose}>
            Volver a los lugares
          </button>
        </>
      ) : (
        <form onSubmit={save} className="stack">
          <Message
            notice={{
              kind: "info",
              message:
                "Un lugar de referencia aparece en el mapa de la app, pero no puede confirmar entregas con código. Para eso, su responsable debe solicitar y obtener un perfil de receptor.",
            }}
          />
          <div className="form-grid">
            <label>
              Nombre
              <input
                name="name"
                defaultValue={point?.name}
                minLength={3}
                maxLength={100}
                required
                disabled={busy}
              />
            </label>
            <label>
              Ciudad
              <input
                name="city"
                defaultValue={point?.city || "Medellín"}
                maxLength={100}
                required
                disabled={busy}
              />
            </label>
            <label className="span-two">
              Dirección completa
              <input
                name="address"
                defaultValue={point?.address}
                minLength={10}
                maxLength={240}
                required
                disabled={busy}
              />
            </label>
            <label>
              Latitud
              <input
                name="latitude"
                type="number"
                step="any"
                min={-90}
                max={90}
                defaultValue={point?.latitude}
                placeholder="6.2442"
                required
                disabled={busy}
              />
            </label>
            <label>
              Longitud
              <input
                name="longitude"
                type="number"
                step="any"
                min={-180}
                max={180}
                defaultValue={point?.longitude}
                placeholder="-75.5812"
                required
                disabled={busy}
              />
            </label>
            <label className="span-two">
              Horarios
              <input
                name="hours_text"
                defaultValue={point?.hours_text}
                minLength={5}
                maxLength={200}
                required
                disabled={busy}
              />
            </label>
          </div>
          <fieldset disabled={busy}>
            <legend>Aparatos que recibe</legend>
            <div className="checkbox-grid">
              {categories.map((c) => (
                <label key={c} className="check-label">
                  <input
                    type="checkbox"
                    checked={chosen.includes(c)}
                    onChange={(e) =>
                      setChosen((v) =>
                        e.target.checked ? [...v, c] : v.filter((s) => s !== c),
                      )
                    }
                  />
                  {label(c)}
                </label>
              ))}
            </div>
          </fieldset>
          <label>
            Condiciones de recepción
            <textarea
              name="accepted_details"
              defaultValue={point?.accepted_details}
              minLength={10}
              maxLength={500}
              required
              rows={3}
              disabled={busy}
            />
          </label>
          <div className="form-grid">
            <label>
              Teléfono (opcional)
              <input
                name="phone"
                defaultValue={point?.phone || ""}
                type="tel"
                maxLength={40}
                disabled={busy}
              />
            </label>
            <label>
              WhatsApp con indicativo (opcional)
              <input
                name="whatsapp"
                defaultValue={point?.whatsapp || ""}
                inputMode="tel"
                pattern="[1-9][0-9]{9,14}"
                placeholder="573001234567"
                disabled={busy}
              />
              <small>Solo números, sin + ni espacios.</small>
            </label>
            <label>
              Fuente de la información
              <input
                name="source_name"
                defaultValue={point?.source_name}
                minLength={3}
                maxLength={120}
                placeholder="Sitio oficial de la organización"
                required
                disabled={busy}
              />
            </label>
            <label>
              Fecha de comprobación
              <input
                name="verified_at"
                type="date"
                defaultValue={
                  point?.verified_at || new Date().toISOString().slice(0, 10)
                }
                max={new Date().toISOString().slice(0, 10)}
                required
                disabled={busy}
              />
            </label>
            <label className="span-two">
              Enlace de la fuente (HTTPS)
              <input
                name="source_url"
                type="url"
                pattern="https://.*"
                defaultValue={point?.source_url}
                required
                disabled={busy}
              />
            </label>
          </div>
          <label>
            Indicaciones para llegar (opcional)
            <textarea
              name="location_note"
              defaultValue={point?.location_note || ""}
              maxLength={500}
              rows={2}
              disabled={busy}
            />
          </label>
          <label>
            Visibilidad
            <select
              name="active"
              defaultValue={String(point?.active ?? true)}
              disabled={busy}
            >
              <option value="true">Visible en la app</option>
              <option value="false">Oculto en la app</option>
            </select>
          </label>
          <label>
            Motivo de la actualización
            <textarea
              name="note"
              minLength={10}
              maxLength={1000}
              required
              rows={2}
              disabled={busy}
            />
          </label>
          <label className="check-label">
            <input
              type="checkbox"
              checked={confirmed}
              onChange={(e) => setConfirmed(e.target.checked)}
              required
              disabled={busy}
            />
            Comprobé los datos y entiendo que se actualizarán en el directorio
            de la app.
          </label>
          {error && <Message notice={{ kind: "error", message: error }} />}
          <div className="form-actions">
            <button
              type="button"
              className="button secondary"
              onClick={onClose}
              disabled={busy}
            >
              Cancelar
            </button>
            <button
              className="button primary"
              disabled={busy || !confirmed || chosen.length === 0}
            >
              {busy ? "Guardando…" : "Guardar lugar"}
            </button>
          </div>
        </form>
      )}
    </Modal>
  );
}
