import { useEffect, useState, type FormEvent } from "react";
import {
  ArrowLeft,
  ArrowRight,
  ClipboardList,
  Eye,
  MapPin,
  Plus,
  Search,
  ShieldCheck,
} from "lucide-react";
import { api, errorMessage } from "./api";
import type {
  Admin,
  Audit,
  Delivery,
  ListSection,
  Notice,
  Page,
  Point,
  Receiver,
  Row,
  UserDetail,
  UserRow,
} from "./types";
import {
  Badge,
  date,
  dateTime,
  Detail,
  External,
  label,
  Loading,
  Message,
  Modal,
  PrivatePhotos,
} from "./ui";
import PointForm from "./PointForm";

const filters: Record<ListSection, [string, string][]> = {
  users: [],
  audit: [],
  points: [
    ["active", "Activos"],
    ["inactive", "Ocultos"],
  ],
  receivers: [
    ["pending", "Pendientes"],
    ["approved", "Aprobados"],
    ["needs_changes", "Cambios solicitados"],
    ["suspended", "Suspendidos"],
    ["draft", "En preparación"],
    ["withdrawn", "Retirados"],
  ],
  deliveries: [
    ["pending", "Pendientes"],
    ["received", "Recepción registrada"],
    ["completed", "Completadas"],
    ["cancelled", "Canceladas"],
  ],
};
const columns: Record<ListSection, string[]> = {
  users: [
    "Persona",
    "Correo",
    "Roles",
    "Verificación",
    "Aparatos",
    "Registro",
    "Detalle",
  ],
  receivers: ["Lugar", "Responsable", "Estado", "Actualización", "Detalle"],
  points: ["Lugar", "Recepción con Noki", "Publicación", "Acepta", "Detalle"],
  deliveries: [
    "Entrega",
    "Destino",
    "Estado",
    "Aparatos",
    "Creación",
    "Detalle",
  ],
  audit: ["Acción", "Administrador", "Fecha", "Observación"],
};
export default function Records({
  section,
  revision,
  refresh,
  admin,
  notify,
}: {
  section: ListSection;
  revision: number;
  refresh: () => void;
  admin: Admin;
  notify: (n: Notice) => void;
}) {
  const [page, setPage] = useState<Page | null>(null);
  const [draftQ, setDraftQ] = useState("");
  const [q, setQ] = useState("");
  const [status, setStatus] = useState("");
  const [offset, setOffset] = useState(0);
  const [busy, setBusy] = useState(true);
  const [error, setError] = useState("");
  const [selected, setSelected] = useState<Row | null>(null);
  const [adding, setAdding] = useState(false);
  const [linkedReceiver, setLinkedReceiver] = useState<string | null>(null);
  useEffect(() => {
    let active = true;
    setBusy(true);
    api<Page>(section, { q, status, offset })
      .then((d) => {
        if (active) {
          setPage(d);
          setError("");
        }
      })
      .catch((e) => {
        if (active) setError(errorMessage(e));
      })
      .finally(() => {
        if (active) setBusy(false);
      });
    return () => {
      active = false;
    };
  }, [section, revision, q, status, offset]);
  function search(e: FormEvent) {
    e.preventDefault();
    setOffset(0);
    setQ(draftQ.trim());
  }
  function saved(message: string) {
    setSelected(null);
    setAdding(false);
    setLinkedReceiver(null);
    notify({ kind: "success", message });
    refresh();
  }
  function cells(row: Row) {
    if (section === "users") {
      const u = row as UserRow;
      return (
        <>
          <td>
            <strong>{u.username || u.display_name || "Sin nombre aún"}</strong>
            {u.receiver_status && (
              <small>{label(u.receiver_status)} como receptor</small>
            )}
          </td>
          <td>{u.email}</td>
          <td>
            <Badge
              value={
                u.receiver_status === "approved" ? "approved" : "reference"
              }
              text={
                u.receiver_status === "approved"
                  ? "Usuario y receptor"
                  : "Usuario"
              }
            />
          </td>
          <td>
            <Badge
              value={u.confirmed ? "approved" : "pending"}
              text={u.confirmed ? "Correo confirmado" : "Sin confirmar"}
            />
          </td>
          <td>{u.devices}</td>
          <td>{date(u.created_at)}</td>
        </>
      );
    }
    if (section === "receivers") {
      const r = row as Receiver;
      return (
        <>
          <td>
            <strong>{r.place_name}</strong>
            <small>{r.address}</small>
          </td>
          <td>
            {r.full_name}
            <small>{r.email}</small>
          </td>
          <td>
            <Badge value={r.status} />
          </td>
          <td>{date(r.updated_at)}</td>
        </>
      );
    }
    if (section === "points") {
      const p = row as Point;
      return (
        <>
          <td>
            <strong>{p.name}</strong>
            <small>{p.address}</small>
          </td>
          <td>
            {p.receiver_request_id ? (
              <Badge value="approved" text="Receptor Noki" />
            ) : (
              <Badge value="reference" text="Lugar de referencia" />
            )}
          </td>
          <td>
            <Badge
              value={p.active ? "approved" : "suspended"}
              text={p.active ? "Visible en la app" : "Oculto"}
            />
          </td>
          <td>{p.accepted_categories.map(label).join(", ")}</td>
        </>
      );
    }
    if (section === "deliveries") {
      const d = row as Delivery;
      return (
        <>
          <td>
            <strong>Entrega {d.id.slice(0, 8)}</strong>
            <small>{d.sender_email}</small>
          </td>
          <td>{d.receiver_name}</td>
          <td>
            <Badge value={d.status} />
          </td>
          <td>{d.devices}</td>
          <td>{date(d.created_at)}</td>
        </>
      );
    }
    const a = row as Audit;
    return (
      <>
        <td>
          <strong>{label(a.action)}</strong>
        </td>
        <td>{a.actor_email}</td>
        <td>{dateTime(a.created_at)}</td>
        <td>
          {a.note || "Sin observaciones"}
          {a.target_id && (
            <small className="mono">Registro: {a.target_id}</small>
          )}
        </td>
      </>
    );
  }
  return (
    <section className="card records-card screen-enter">
      {section === "points" && (
        <div className="context-note">
          <MapPin size={22} />
          <p>
            Los receptores aprobados aparecen automáticamente. Los lugares de
            referencia también se muestran en el mapa, pero no reciben entregas
            con código dentro de Noki.
          </p>
        </div>
      )}
      {section === "deliveries" && (
        <div className="context-note">
          <ClipboardList size={22} />
          <p>
            La recepción se confirma desde la cuenta del receptor asignado.
            Desde aquí puedes consultar el recorrido, sin cambiar ni revelar el
            código de entrega.
          </p>
        </div>
      )}
      {section === "receivers" && (
        <>
          <div className="context-note">
            <ShieldCheck size={22} />
            <p>
              Las solicitudes enviadas desde la app llegan aquí. Abre cada una
              para revisar al responsable, la dirección, las fotos, los horarios
              y las condiciones antes de aprobar.
            </p>
          </div>
          <div
            className="quick-filters"
            role="group"
            aria-label="Solicitudes y receptores"
          >
            <button
              className={`button compact ${status === "" ? "primary" : "secondary"}`}
              aria-pressed={status === ""}
              onClick={() => {
                setStatus("");
                setOffset(0);
              }}
            >
              Todos
            </button>
            <button
              className={`button compact ${status === "pending" ? "primary" : "secondary"}`}
              aria-pressed={status === "pending"}
              onClick={() => {
                setStatus("pending");
                setOffset(0);
              }}
            >
              Solicitudes pendientes
            </button>
            <button
              className={`button compact ${status === "approved" ? "primary" : "secondary"}`}
              aria-pressed={status === "approved"}
              onClick={() => {
                setStatus("approved");
                setOffset(0);
              }}
            >
              Receptores aprobados
            </button>
          </div>
        </>
      )}
      <div className="list-toolbar">
        <form className="search-form" onSubmit={search}>
          <label className="search-input">
            <Search size={18} />
            <span className="sr-only">
              Buscar en{" "}
              {section === "users"
                ? "usuarios"
                : section === "receivers"
                  ? "receptores"
                  : section === "points"
                    ? "lugares"
                    : section === "deliveries"
                      ? "entregas"
                      : "actividad"}
            </span>
            <input
              type="search"
              placeholder={
                section === "points"
                  ? "Nombre, dirección o ciudad…"
                  : section === "receivers"
                    ? "Lugar, responsable o correo…"
                    : section === "users"
                      ? "Nombre o correo…"
                      : section === "deliveries"
                        ? "Destino, correo o identificador…"
                        : "Acción o administrador…"
              }
              maxLength={120}
              value={draftQ}
              onChange={(e) => setDraftQ(e.target.value)}
            />
          </label>
          <button className="button secondary" type="submit">
            Buscar
          </button>
        </form>
        {!!filters[section].length && (
          <label className="filter-label">
            <span className="sr-only">Filtrar por estado</span>
            <select
              value={status}
              onChange={(e) => {
                setStatus(e.target.value);
                setOffset(0);
              }}
            >
              <option value="">Todos los estados</option>
              {filters[section].map(([v, t]) => (
                <option key={v} value={v}>
                  {t}
                </option>
              ))}
            </select>
          </label>
        )}
        {section === "points" && (
          <button className="button primary" onClick={() => setAdding(true)}>
            <Plus size={18} />
            Añadir lugar
          </button>
        )}
      </div>
      {error && <Message notice={{ kind: "error", message: error }} />}
      {!page && busy ? (
        <Loading />
      ) : (
        page && (
          <>
            <div className="table-meta">
              <span>
                {page.total.toLocaleString("es-CO")} registros
                {q && ` · Búsqueda: “${q}”`}
              </span>
              <span className="small muted">
                {busy
                  ? "Actualizando…"
                  : `Consulta: ${dateTime(page.checkedAt)}`}
              </span>
            </div>
            {page.items.length ? (
              <div
                className="table-scroll"
                tabIndex={0}
                role="region"
                aria-label="Tabla de registros, desplázate horizontalmente si es necesario"
              >
                <table>
                  <caption className="sr-only">Registros de {section}</caption>
                  <thead>
                    <tr>
                      {columns[section].map((c) => (
                        <th key={c} scope="col">
                          {c}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {page.items.map((row) => (
                      <tr key={row.id}>
                        {cells(row)}
                        {section !== "audit" && (
                          <td>
                            <button
                              className="button compact secondary"
                              aria-label={`Ver detalle de ${"place_name" in row ? row.place_name : "name" in row ? row.name : "email" in row ? row.email : row.id}`}
                              onClick={() => setSelected(row)}
                            >
                              <Eye size={16} />
                              Ver
                            </button>
                          </td>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="empty-state">
                <Search size={28} />
                <h2>
                  {q || status
                    ? "No encontramos coincidencias"
                    : "Todavía no hay registros"}
                </h2>
                <p>
                  {q || status
                    ? "Prueba otro nombre o quita los filtros."
                    : "Los nuevos registros de la app aparecerán aquí automáticamente."}
                </p>
                {(q || status) && (
                  <button
                    className="button secondary"
                    onClick={() => {
                      setQ("");
                      setDraftQ("");
                      setStatus("");
                      setOffset(0);
                    }}
                  >
                    Limpiar filtros
                  </button>
                )}
              </div>
            )}
            <div className="pagination">
              <span>
                {page.total
                  ? `${offset + 1}–${Math.min(offset + page.pageSize, page.total)} de ${page.total}`
                  : "0 registros"}
              </span>
              <div>
                <button
                  className="button secondary compact"
                  aria-label="Página anterior"
                  disabled={offset === 0 || busy}
                  onClick={() =>
                    setOffset((v) => Math.max(0, v - page.pageSize))
                  }
                >
                  <ArrowLeft size={17} />
                  Anterior
                </button>
                <button
                  className="button secondary compact"
                  aria-label="Página siguiente"
                  disabled={offset + page.pageSize >= page.total || busy}
                  onClick={() => setOffset((v) => v + page.pageSize)}
                >
                  Siguiente
                  <ArrowRight size={17} />
                </button>
              </div>
            </div>
          </>
        )
      )}
      {adding && (
        <PointForm
          onClose={() => setAdding(false)}
          onSaved={() =>
            saved(
              "Lugar guardado. Ya está disponible para el directorio de la app.",
            )
          }
        />
      )}
      {selected &&
        (section === "points" ? (
          <PointForm
            point={selected as Point}
            onClose={() => setSelected(null)}
            onSaved={() =>
              saved("Cambios del lugar guardados y compartidos con la app.")
            }
            onReceiver={(id) => {
              setSelected(null);
              setLinkedReceiver(id);
            }}
          />
        ) : (
          <RecordDetail
            section={section}
            id={String(selected.id)}
            admin={admin}
            onClose={() => setSelected(null)}
            onSaved={saved}
          />
        ))}
      {linkedReceiver && (
        <RecordDetail
          section="receivers"
          id={linkedReceiver}
          admin={admin}
          onClose={() => setLinkedReceiver(null)}
          onSaved={saved}
        />
      )}
    </section>
  );
}

function RecordDetail({
  section,
  id,
  admin,
  onClose,
  onSaved,
}: {
  section: ListSection;
  id: string;
  admin: Admin;
  onClose: () => void;
  onSaved: (m: string) => void;
}) {
  const [data, setData] = useState<UserDetail | Receiver | Delivery | null>(
    null,
  );
  const [error, setError] = useState("");
  const [locked, setLocked] = useState(false);
  useEffect(() => {
    let active = true;
    const op =
      section === "users"
        ? "user_detail"
        : section === "receivers"
          ? "receiver_detail"
          : "delivery_detail";
    api<UserDetail | Receiver | Delivery>(op, { id })
      .then((d) => {
        if (active) setData(d);
      })
      .catch((e) => {
        if (active) setError(errorMessage(e));
      });
    return () => {
      active = false;
    };
  }, [id, section]);
  return (
    <Modal
      title={
        section === "users"
          ? "Perfil de usuario"
          : section === "receivers"
            ? "Solicitud de receptor"
            : "Detalle de la entrega"
      }
      onClose={onClose}
      busy={locked}
    >
      {error && <Message notice={{ kind: "error", message: error }} />}
      {!data && !error && <Loading />}
      {data && section === "receivers" && (
        <ReceiverDetail
          receiver={data as Receiver}
          admin={admin}
          onSaved={onSaved}
          onBusy={setLocked}
        />
      )}
      {data && section === "users" && <UserInfo user={data as UserDetail} />}
      {data && section === "deliveries" && (
        <DeliveryInfo delivery={data as Delivery} />
      )}
    </Modal>
  );
}

function UserInfo({ user: u }: { user: UserDetail }) {
  return (
    <>
      <div className="detail-intro">
        <span className="avatar large">
          {(u.username || u.display_name || u.email).slice(0, 1).toUpperCase()}
        </span>
        <div>
          <h3>{u.username || u.display_name || "Perfil sin nombre"}</h3>
          <p>{u.email}</p>
          <Badge
            value={u.confirmed ? "approved" : "pending"}
            text={
              u.confirmed
                ? "Correo confirmado"
                : "Correo pendiente de confirmar"
            }
          />
        </div>
      </div>
      <dl className="detail-grid">
        <Detail title="Nombre de usuario">{u.username}</Detail>
        <Detail title="Nombre del perfil">{u.display_name}</Detail>
        <Detail title="Registro">{dateTime(u.created_at)}</Detail>
        <Detail title="Último acceso">{dateTime(u.last_sign_in_at)}</Detail>
        <Detail title="Perfil de receptor">
          {u.receiver_status
            ? label(u.receiver_status)
            : "No ha solicitado un perfil"}
        </Detail>
      </dl>
      <PrivatePhotos
        bucket="noki-profile-photos"
        paths={[`${u.id}/avatar.jpg`]}
      />
      <h3>Aparatos asociados</h3>
      {u.devices.length ? (
        <ul className="item-list">
          {u.devices.map((d) => (
            <li key={d.id}>
              <strong>{d.description}</strong>
              <span>
                {label(d.category)} · {label(d.decision)} ·{" "}
                {d.outcome ? label(d.outcome) : "Sin resultado registrado"}
                {d.archived_at ? " · Archivado" : ""}
              </span>
            </li>
          ))}
        </ul>
      ) : (
        <p className="muted">Esta persona aún no tiene aparatos registrados.</p>
      )}
      <p className="small muted">
        Se muestran hasta 100 aparatos recientes. Las contraseñas y los códigos
        de acceso no son visibles para los administradores.
      </p>
    </>
  );
}

function DeliveryInfo({ delivery: d }: { delivery: Delivery }) {
  return (
    <>
      <div className="detail-intro">
        <span className="round-icon">
          <ClipboardList />
        </span>
        <div>
          <h3>{d.receiver_name}</h3>
          <Badge value={d.status} />
          <p className="small mono">ID: {d.id}</p>
        </div>
      </div>
      <dl className="detail-grid">
        <Detail title="Quien entrega">{d.sender_email}</Detail>
        <Detail title="Quien recibe">{d.receiver_email}</Detail>
        <Detail title="Destino">{d.address}</Detail>
        <Detail title="Creación">{dateTime(d.created_at)}</Detail>
        <Detail title="Recepción">{dateTime(d.received_at)}</Detail>
        <Detail title="Finalización">{dateTime(d.completed_at)}</Detail>
        {d.cancelled_at && (
          <Detail title="Cancelación">
            {dateTime(d.cancelled_at)} ·{" "}
            {d.cancel_reason || "Sin motivo registrado"}
          </Detail>
        )}
        <Detail title="Contacto previo declarado">
          {d.contact_confirmed_at
            ? dateTime(d.contact_confirmed_at)
            : "No registrado"}
        </Detail>
      </dl>
      <h3>Aparatos de esta entrega</h3>
      <ul className="item-list">
        {d.items.map((i) => (
          <li key={i.device_id}>
            <strong>{i.description}</strong>
            <span>
              {label(i.category)} · {label(i.safety_state)}
            </span>
          </li>
        ))}
      </ul>
      <div className="soft-note">
        <ShieldCheck />
        <p>
          El código permanece en la app de quien entrega. Solo el receptor
          asignado puede confirmar la recepción; la administración no sustituye
          ese paso.
        </p>
      </div>
    </>
  );
}

function ReceiverDetail({
  receiver: r,
  admin,
  onSaved,
  onBusy,
}: {
  receiver: Receiver;
  admin: Admin;
  onSaved: (m: string) => void;
  onBusy: (b: boolean) => void;
}) {
  const [decision, setDecision] = useState(
    r.status === "approved" ? "suspended" : "approved",
  );
  const [note, setNote] = useState("");
  const [checked, setChecked] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const reviewable = ["pending", "approved", "suspended"].includes(r.status);
  const self = r.user_id === admin.id;
  async function review(e: FormEvent) {
    e.preventDefault();
    if (!checked || busy) return;
    setBusy(true);
    onBusy(true);
    setError("");
    try {
      await api("review_receiver", {
        id: r.id,
        decision,
        note,
        expected_updated_at: r.updated_at,
      });
      onSaved(
        decision === "approved"
          ? "Receptor aprobado. Su lugar se publicó automáticamente en la app."
          : decision === "needs_changes"
            ? "La solicitud quedó con cambios solicitados. El responsable verá la nota en su app."
            : "Receptor suspendido. Su lugar dejó de aparecer como destino activo.",
      );
    } catch (failure) {
      setError(errorMessage(failure));
    } finally {
      setBusy(false);
      onBusy(false);
    }
  }
  return (
    <>
      <div className="detail-intro">
        <span className="round-icon">
          <MapPin />
        </span>
        <div>
          <h3>{r.place_name}</h3>
          <Badge value={r.status} />
          <p>{r.address}</p>
        </div>
      </div>
      <dl className="detail-grid">
        <Detail title="Responsable">{r.full_name}</Detail>
        <Detail title="Correo de la cuenta">{r.email}</Detail>
        <Detail title="WhatsApp público">
          {r.public_whatsapp ? (
            <External href={`https://wa.me/${r.public_whatsapp}`}>
              +{r.public_whatsapp}
            </External>
          ) : (
            "No indicado"
          )}
        </Detail>
        <Detail title="Horarios">{r.hours}</Detail>
        <Detail title="Aparatos que recibe">
          {r.categories.map(label).join(", ")}
        </Detail>
        <Detail title="Condiciones">{r.conditions}</Detail>
        <Detail title="Ubicación">
          {r.latitude !== null && r.longitude !== null ? (
            <External
              href={`https://www.openstreetmap.org/?mlat=${r.latitude}&mlon=${r.longitude}#map=18/${r.latitude}/${r.longitude}`}
            >
              Ver ubicación en el mapa
            </External>
          ) : (
            "Falta la ubicación exacta"
          )}
        </Detail>
        <Detail title="Última actualización">{dateTime(r.updated_at)}</Detail>
        <Detail title="Aceptación de términos">
          {r.legal_version} · {dateTime(r.accepted_at)}
        </Detail>
      </dl>
      {r.status !== "draft" && <PrivatePhotos paths={r.photos} />}
      {r.review_note && (
        <section className="soft-block">
          <h3>Revisión anterior</h3>
          <p>{r.review_note}</p>
          <p className="small muted">
            {r.reviewed_by} · {dateTime(r.reviewed_at)}
          </p>
        </section>
      )}
      {self && (
        <Message
          notice={{
            kind: "info",
            message:
              "Esta es tu solicitud. El otro administrador debe revisarla para mantener una revisión independiente.",
          }}
        />
      )}
      {reviewable && !self && (
        <form className="review-form stack" onSubmit={review}>
          <h3>Tu revisión</h3>
          <p className="muted">
            Comprueba que el lugar, sus fotos, ubicación, horarios y contacto
            sean coherentes. Contacta al responsable si necesitas aclaraciones.
          </p>
          <label>
            Decisión
            <select
              value={decision}
              disabled={busy}
              onChange={(e) => {
                setDecision(e.target.value);
                setChecked(false);
              }}
            >
              {r.status !== "approved" && (
                <option value="approved">Aprobar receptor</option>
              )}
              {r.status === "pending" && (
                <option value="needs_changes">Solicitar cambios</option>
              )}
              {r.status === "approved" && (
                <option value="suspended">Suspender receptor</option>
              )}
            </select>
          </label>
          <label>
            Nota para el responsable
            <textarea
              required
              minLength={10}
              maxLength={1000}
              rows={3}
              placeholder="Explica qué comprobaste o qué necesita corregir."
              value={note}
              onChange={(e) => setNote(e.target.value)}
              disabled={busy}
            />
          </label>
          <label className="check-label">
            <input
              type="checkbox"
              checked={checked}
              onChange={(e) => setChecked(e.target.checked)}
              required
              disabled={busy}
            />
            <span>
              {decision === "approved"
                ? "Revisé la información y las fotos del lugar. Confirmo su publicación como receptor aprobado en Noki."
                : decision === "suspended"
                  ? "Entiendo que el lugar dejará de estar disponible como destino activo en la app."
                  : "La nota explica con claridad los cambios que debe hacer el responsable."}
            </span>
          </label>
          {error && <Message notice={{ kind: "error", message: error }} />}
          <button
            className={`button ${decision === "suspended" ? "danger" : "primary"}`}
            disabled={busy || !checked || note.trim().length < 10}
          >
            {busy
              ? "Guardando revisión…"
              : decision === "approved"
                ? "Confirmar aprobación"
                : decision === "needs_changes"
                  ? "Enviar solicitud de cambios"
                  : "Confirmar suspensión"}
          </button>
        </form>
      )}
      {!reviewable && (
        <p className="muted">
          {r.status === "draft"
            ? "El responsable todavía está preparando su solicitud. Podrás revisarla cuando la envíe."
            : r.status === "needs_changes"
              ? "Esperando a que el responsable corrija y vuelva a enviar su solicitud."
              : "La solicitud fue retirada por su responsable."}
        </p>
      )}
    </>
  );
}
