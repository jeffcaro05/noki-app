import { useEffect, useState } from "react";
import {
  ArrowDownToLine,
  ArrowRight,
  CheckCheck,
  ClipboardList,
  MapPin,
  ShieldCheck,
  Users,
} from "lucide-react";
import BrandCircuit from "./BrandCircuit";
import { api, errorMessage } from "./api";
import type { Notice, Overview, Section } from "./types";
import { dateTime, Loading, Message } from "./ui";

export function csvCell(value: unknown) {
  const s = String(value ?? "");
  return `"${(/^[=+\-@\t\r\n]/.test(s) ? "'" : "") + s.replace(/"/g, '""')}"`;
}
export default function OverviewScreen({
  mode,
  revision,
  navigate,
  notify,
}: {
  mode: "overview" | "reports";
  revision: number;
  navigate: (s: Section) => void;
  notify: (n: Notice) => void;
}) {
  const [data, setData] = useState<Overview | null>(null);
  const [error, setError] = useState("");
  const [exporting, setExporting] = useState(false);
  useEffect(() => {
    let active = true;
    api<Overview>("overview")
      .then((d) => {
        if (active) {
          setData(d);
          setError("");
        }
      })
      .catch((e) => {
        if (active) setError(errorMessage(e));
      });
    return () => {
      active = false;
    };
  }, [revision]);
  async function download() {
    setExporting(true);
    try {
      const d = await api<Overview>("report_export");
      const rows = [
        ["Noki · Informe de actividad", "Valor"],
        ["Fecha de consulta", d.checkedAt],
        ["Usuarios", d.users],
        ["Correos confirmados", d.confirmedUsers],
        ["Receptores aprobados", d.receivers],
        ["Solicitudes pendientes", d.pendingReviews],
        ["Lugares activos", d.points],
        ["Entregas pendientes", d.pendingDeliveries],
        ["Entregas completadas", d.completedDeliveries],
        ["Entregas canceladas", d.cancelledDeliveries],
        ["Aparatos entregados", d.deliveredDevices],
        [],
        ["Mes (UTC)", "Entregas completadas"],
        ...d.byMonth.map((m) => [m.month, m.completed]),
      ];
      const blob = new Blob(
        ["\uFEFF" + rows.map((r) => r.map(csvCell).join(";")).join("\r\n")],
        { type: "text/csv;charset=utf-8" },
      );
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `Noki-informe-${new Date().toISOString().slice(0, 10)}.csv`;
      a.click();
      setTimeout(() => URL.revokeObjectURL(url), 1000);
      notify({
        kind: "success",
        message:
          "Informe descargado. Contiene cifras reales, sin información personal.",
      });
    } catch (e) {
      notify({ kind: "error", message: errorMessage(e) });
    } finally {
      setExporting(false);
    }
  }
  if (!data)
    return error ? (
      <Message notice={{ kind: "error", message: error }} />
    ) : (
      <Loading />
    );
  const metrics = [
    {
      title: "Usuarios registrados",
      value: data.users,
      meta: `${data.confirmedUsers} con correo confirmado`,
      icon: Users,
      to: "users",
    },
    {
      title: "Receptores aprobados",
      value: data.receivers,
      meta: `${data.pendingReviews} solicitudes por revisar`,
      icon: ShieldCheck,
      to: "receivers",
    },
    {
      title: "Entregas completadas",
      value: data.completedDeliveries,
      meta: `${data.deliveredDevices} aparatos recibidos`,
      icon: CheckCheck,
      to: "deliveries",
    },
    {
      title: "Lugares activos",
      value: data.points,
      meta: "Disponibles en el directorio de la app",
      icon: MapPin,
      to: "points",
    },
  ] as const;
  return (
    <div className="screen-enter">
      {error && <Message notice={{ kind: "error", message: error }} />}
      {mode === "overview" && (
        <section className="welcome-banner">
          <div>
            <span className="eyebrow">EL SIGUIENTE PASO EMPIEZA AQUÍ</span>
            <h2>
              Más oportunidades.
              <br />
              Mejores decisiones.
            </h2>
            <p>
              Revisa los lugares que se suman a Noki y sigue el recorrido de
              cada entrega.
            </p>
            <button
              className="button banner-button"
              onClick={() => navigate("receivers")}
            >
              Revisar receptores
              <ArrowRight size={18} />
            </button>
          </div>
          <div className="banner-art">
            <BrandCircuit compact />
          </div>
        </section>
      )}
      <div className="metrics">
        {metrics.map((m) => (
          <button
            key={m.title}
            className="metric-card"
            onClick={() => navigate(m.to)}
          >
            <div className="metric-top">
              <span className="metric-icon">
                <m.icon size={21} />
              </span>
              <ArrowRight size={16} />
            </div>
            <strong>{m.value.toLocaleString("es-CO")}</strong>
            <h2>{m.title}</h2>
            <p>{m.meta}</p>
          </button>
        ))}
      </div>
      <div className="overview-columns">
        <section className="card">
          <div className="card-heading">
            <div>
              <span className="eyebrow">SEGUIMIENTO</span>
              <h2>Entregas de los últimos meses</h2>
            </div>
            <span className="badge">6 meses</span>
          </div>
          <div
            className="month-chart"
            aria-label="Entregas completadas por mes"
          >
            {data.byMonth.map((m) => (
              <div className="month-column" key={m.month}>
                <strong>{m.completed}</strong>
                <div className="bar-track">
                  <span
                    style={{
                      height: `${m.completed === 0 ? 3 : Math.max(8, (m.completed / Math.max(...data.byMonth.map((v) => v.completed), 1)) * 100)}%`,
                    }}
                  />
                </div>
                <span>
                  {new Intl.DateTimeFormat("es-CO", {
                    month: "short",
                    timeZone: "UTC",
                  }).format(new Date(m.month + "-01T12:00:00Z"))}
                </span>
              </div>
            ))}
          </div>
          <p className="small muted">
            Solo recepciones confirmadas. Agrupación mensual en UTC.
          </p>
        </section>
        <section className="card next-steps">
          <span className="eyebrow">PARA REVISAR</span>
          <h2>Lo que necesita atención</h2>
          <button className="task-link" onClick={() => navigate("receivers")}>
            <span className="round-icon small-icon">
              <ShieldCheck size={20} />
            </span>
            <span>
              <strong>Solicitudes de receptor</strong>
              <small>
                {data.pendingReviews === 0
                  ? "Todo al día, no hay solicitudes pendientes"
                  : `${data.pendingReviews} pendientes de revisión`}
              </small>
            </span>
            <ArrowRight size={18} />
          </button>
          <button className="task-link" onClick={() => navigate("deliveries")}>
            <span className="round-icon small-icon">
              <ClipboardList size={20} />
            </span>
            <span>
              <strong>Entregas en curso</strong>
              <small>{data.pendingDeliveries} pendientes de completar</small>
            </span>
            <ArrowRight size={18} />
          </button>
          <div className="soft-note">
            <ShieldCheck size={20} />
            <p>
              La aprobación de un receptor publica su lugar en la app. No
              necesitas registrarlo dos veces.
            </p>
          </div>
        </section>
      </div>
      {mode === "reports" && (
        <section className="card report-card">
          <div>
            <h2>Lleva el resumen contigo</h2>
            <p>
              Descarga las cifras de esta consulta para abrirlas en Excel o
              compartir un informe de actividad.
            </p>
            <p className="muted small">
              No se calculan kilogramos ni emisiones: la app todavía no registra
              esas mediciones.
            </p>
          </div>
          <button
            className="button primary"
            disabled={exporting}
            onClick={() => void download()}
          >
            <ArrowDownToLine size={18} />
            {exporting ? "Preparando…" : "Descargar CSV"}
          </button>
        </section>
      )}
      <p className="last-check">
        Última consulta correcta: {dateTime(data.checkedAt)} · Hora de Colombia
      </p>
    </div>
  );
}
