import { useCallback, useEffect, useRef, useState } from "react";
import {
  Accessibility,
  Activity,
  BarChart3,
  Check,
  ClipboardList,
  LayoutDashboard,
  LogOut,
  MapPin,
  Menu,
  RefreshCw,
  ShieldCheck,
  Users,
  Wifi,
  WifiOff,
  X,
  type LucideIcon,
} from "lucide-react";
import { api, errorMessage, supabase } from "./api";
import type { Admin, Notice, Section } from "./types";
import { Loading, Message } from "./ui";
import Login from "./Login";
import OverviewScreen from "./Overview";
import Records from "./Records";

const navigation: {
  id: Section;
  title: string;
  icon: LucideIcon;
  group: string;
}[] = [
  { id: "overview", title: "Resumen", icon: LayoutDashboard, group: "GENERAL" },
  { id: "users", title: "Usuarios", icon: Users, group: "COMUNIDAD" },
  {
    id: "receivers",
    title: "Receptores",
    icon: ShieldCheck,
    group: "COMUNIDAD",
  },
  {
    id: "points",
    title: "Lugares de entrega",
    icon: MapPin,
    group: "COMUNIDAD",
  },
  {
    id: "deliveries",
    title: "Entregas",
    icon: ClipboardList,
    group: "SEGUIMIENTO",
  },
  { id: "reports", title: "Informes", icon: BarChart3, group: "SEGUIMIENTO" },
  {
    id: "audit",
    title: "Actividad del equipo",
    icon: Activity,
    group: "SEGUIMIENTO",
  },
];
const descriptions: Record<Section, string> = {
  overview: "Una vista clara de lo que está pasando en Noki.",
  users: "Las personas que ya forman parte de la app.",
  receivers: "Revisa solicitudes y acompaña a quienes reciben aparatos.",
  points: "Los lugares que las personas encuentran en la app.",
  deliveries: "Sigue cada entrega, desde la preparación hasta la recepción.",
  reports: "Resultados reales, sin estimaciones ni datos de ejemplo.",
  audit: "Consultas y decisiones registradas para cuidar la información.",
};

function Preferences() {
  const [theme, setTheme] = useState(
    () => localStorage.getItem("noki-admin-theme") || "system",
  );
  const [scale, setScale] = useState(
    () => localStorage.getItem("noki-admin-scale") || "100",
  );
  const [motion, setMotion] = useState(
    () => localStorage.getItem("noki-admin-motion") === "reduce",
  );
  const [contrast, setContrast] = useState(
    () => localStorage.getItem("noki-admin-contrast") === "high",
  );
  useEffect(() => {
    const mq = matchMedia("(prefers-color-scheme: dark)");
    const apply = () =>
      (document.documentElement.dataset.theme =
        theme === "system" ? (mq.matches ? "dark" : "light") : theme);
    apply();
    mq.addEventListener("change", apply);
    localStorage.setItem("noki-admin-theme", theme);
    return () => mq.removeEventListener("change", apply);
  }, [theme]);
  useEffect(() => {
    document.documentElement.style.fontSize = `${scale}%`;
    document.documentElement.dataset.scale = scale;
    localStorage.setItem("noki-admin-scale", scale);
  }, [scale]);
  useEffect(() => {
    document.documentElement.dataset.motion = motion ? "reduce" : "auto";
    localStorage.setItem("noki-admin-motion", motion ? "reduce" : "auto");
  }, [motion]);
  useEffect(() => {
    document.documentElement.dataset.contrast = contrast ? "high" : "normal";
    localStorage.setItem("noki-admin-contrast", contrast ? "high" : "normal");
  }, [contrast]);
  return (
    <details className="preferences">
      <summary aria-label="Opciones de accesibilidad">
        <Accessibility size={21} />
        <span>Accesibilidad</span>
      </summary>
      <div className="preferences-panel">
        <h3>A tu manera</h3>
        <label>
          Apariencia
          <select value={theme} onChange={(e) => setTheme(e.target.value)}>
            <option value="system">Como el dispositivo</option>
            <option value="light">Clara</option>
            <option value="dark">Oscura</option>
          </select>
        </label>
        <label>
          Tamaño del texto
          <select value={scale} onChange={(e) => setScale(e.target.value)}>
            <option value="100">100 %</option>
            <option value="125">125 %</option>
            <option value="150">150 %</option>
            <option value="200">200 %</option>
          </select>
        </label>
        <label className="check-label">
          <input
            type="checkbox"
            checked={motion}
            onChange={(e) => setMotion(e.target.checked)}
          />
          Reducir movimiento
        </label>
        <label className="check-label">
          <input
            type="checkbox"
            checked={contrast}
            onChange={(e) => setContrast(e.target.checked)}
          />
          Aumentar contraste
        </label>
      </div>
    </details>
  );
}

export default function App() {
  const [admin, setAdmin] = useState<Admin | null>(null);
  const [checking, setChecking] = useState(true);
  const [authError, setAuthError] = useState("");
  const [section, setSection] = useState<Section>(() =>
    navigation.some((n) => n.id === location.hash.slice(1))
      ? (location.hash.slice(1) as Section)
      : "overview",
  );
  const [notice, setNotice] = useState<Notice | null>(null);
  const [revision, setRevision] = useState(0);
  const [live, setLive] = useState(false);
  const [online, setOnline] = useState(navigator.onLine);
  const [mobileNav, setMobileNav] = useState(false);
  const [idleWarning, setIdleWarning] = useState(false);
  const lastActivity = useRef(Date.now());
  const lastTouch = useRef(0);
  const loggingOut = useRef(false);
  const mainTitle = useRef<HTMLHeadingElement>(null);
  const refresh = useCallback(() => setRevision((v) => v + 1), []);
  const open = useCallback(async () => {
    const who = await api<Admin>("open");
    setAdmin(who);
    setAuthError("");
    lastActivity.current = Date.now();
  }, []);
  const logout = useCallback(async (message = "") => {
    if (loggingOut.current) return;
    loggingOut.current = true;
    setAdmin(null);
    setNotice(null);
    setAuthError(message);
    setIdleWarning(false);
    setMobileNav(false);
    try {
      await api("close");
    } catch {
      /* Access may already have expired; local sign-out still runs. */
    }
    try {
      await supabase.auth.signOut({ scope: "local" });
    } finally {
      sessionStorage.removeItem("noki-admin-auth-v2");
      loggingOut.current = false;
    }
  }, []);
  useEffect(() => {
    let cancelled = false;
    supabase.auth
      .getSession()
      .then(async ({ data, error }) => {
        if (error) throw error;
        if (data.session && !cancelled) await open();
      })
      .catch((e) => {
        if (!cancelled) setAuthError(errorMessage(e));
      })
      .finally(() => {
        if (!cancelled) setChecking(false);
      });
    const { data } = supabase.auth.onAuthStateChange((event) => {
      if (event === "SIGNED_OUT") {
        setAdmin(null);
        setNotice(null);
      }
    });
    return () => {
      cancelled = true;
      data.subscription.unsubscribe();
    };
  }, [open]);
  useEffect(() => {
    const expired = () =>
      void logout(
        "Tu sesión terminó. Solicita un código nuevo para continuar.",
      );
    window.addEventListener("noki-admin-expired", expired);
    return () => window.removeEventListener("noki-admin-expired", expired);
  }, [logout]);
  useEffect(() => {
    const hash = () => {
      const key = location.hash.slice(1);
      if (navigation.some((n) => n.id === key)) setSection(key as Section);
    };
    window.addEventListener("hashchange", hash);
    return () => window.removeEventListener("hashchange", hash);
  }, []);
  useEffect(() => {
    if (admin) {
      mainTitle.current?.focus();
      setMobileNav(false);
    }
  }, [section, admin]);
  useEffect(() => {
    if (!admin) return;
    let debounce: ReturnType<typeof setTimeout>;
    const channel = supabase
      .channel("noki-admin-changes")
      .on(
        "postgres_changes",
        { event: "*", schema: "public", table: "noki_admin_sync" },
        () => {
          clearTimeout(debounce);
          debounce = setTimeout(refresh, 350);
        },
      )
      .subscribe((status) => setLive(status === "SUBSCRIBED"));
    const connection = () => {
      setOnline(navigator.onLine);
      if (navigator.onLine) refresh();
    };
    const fallback = setInterval(() => {
      if (document.visibilityState === "visible" && navigator.onLine) refresh();
    }, 15000);
    const visible = () => {
      if (document.visibilityState === "visible") refresh();
    };
    window.addEventListener("online", connection);
    window.addEventListener("offline", connection);
    document.addEventListener("visibilitychange", visible);
    return () => {
      clearInterval(fallback);
      clearTimeout(debounce);
      void supabase.removeChannel(channel);
      setLive(false);
      window.removeEventListener("online", connection);
      window.removeEventListener("offline", connection);
      document.removeEventListener("visibilitychange", visible);
    };
  }, [admin, refresh]);
  useEffect(() => {
    if (!admin) return;
    const activity = () => {
      const now = Date.now();
      if (now - lastActivity.current >= 30 * 60000) {
        void logout("La sesión se cerró después de 30 minutos sin actividad.");
        return;
      }
      lastActivity.current = now;
      setIdleWarning(false);
      if (now - lastTouch.current > 30000) {
        lastTouch.current = now;
        void api("touch").catch(() => {
          /* RPC signals authorization expiry separately. */
        });
      }
    };
    window.addEventListener("pointerdown", activity);
    window.addEventListener("keydown", activity);
    const timer = setInterval(() => {
      const elapsed = Date.now() - lastActivity.current;
      if (elapsed >= 30 * 60000)
        void logout("La sesión se cerró después de 30 minutos sin actividad.");
      else setIdleWarning(elapsed >= 28 * 60000);
    }, 5000);
    return () => {
      clearInterval(timer);
      window.removeEventListener("pointerdown", activity);
      window.removeEventListener("keydown", activity);
    };
  }, [admin, logout]);
  function navigate(id: Section) {
    setSection(id);
    location.hash = id;
    setNotice(null);
  }
  if (checking)
    return (
      <div className="boot">
        <img src="/brand/noki_symbol.png" alt="Noki" />
        <Loading text="Comprobando acceso seguro…" />
      </div>
    );
  if (!admin)
    return (
      <>
        <div className="login-tools">
          <Preferences />
        </div>
        <Login initialError={authError} key={authError} onVerified={open} />
      </>
    );
  const current = navigation.find((n) => n.id === section)!;
  return (
    <div className="app-shell">
      <a className="skip-link" href="#main">
        Saltar al contenido
      </a>
      {mobileNav && (
        <button
          className="nav-backdrop"
          aria-label="Cerrar menú de navegación"
          onClick={() => setMobileNav(false)}
        />
      )}
      <aside className={`sidebar ${mobileNav ? "is-open" : ""}`}>
        <div className="sidebar-brand">
          <img
            className="logo-light"
            src="/brand/noki_wordmark.png"
            alt="Noki"
          />
          <img
            className="logo-dark"
            src="/brand/noki_wordmark_dark.png"
            alt="Noki"
          />
          <span>Administración</span>
          <button
            className="icon-button mobile-only"
            aria-label="Cerrar menú"
            onClick={() => setMobileNav(false)}
          >
            <X />
          </button>
        </div>
        <nav aria-label="Navegación principal">
          {navigation.map((n, i) => (
            <div key={n.id}>
              {(i === 0 || navigation[i - 1].group !== n.group) && (
                <p className="nav-group">{n.group}</p>
              )}
              <a
                href={`#${n.id}`}
                className={`nav-item ${section === n.id ? "selected" : ""}`}
                aria-current={section === n.id ? "page" : undefined}
                onClick={(e) => {
                  e.preventDefault();
                  navigate(n.id);
                }}
              >
                <n.icon size={20} />
                <span>{n.title}</span>
                {section === n.id && <span className="nav-dot" />}
              </a>
            </div>
          ))}
        </nav>
        <div className="sidebar-bottom">
          <div className="connection">
            <span className={`connection-dot ${online ? "" : "offline"}`} />
            <span>{online ? "Conectada con la app" : "Sin conexión"}</span>
          </div>
          <p>
            Un mismo Noki.
            <br />
            Información real y compartida.
          </p>
        </div>
      </aside>
      <div className="workspace">
        <header className="topbar">
          <div className="topbar-left">
            <button
              className="icon-button mobile-only"
              aria-label="Abrir menú de navegación"
              aria-expanded={mobileNav}
              onClick={() => setMobileNav(true)}
            >
              <Menu />
            </button>
            <span className="breadcrumb">
              Administración <span>/</span> <strong>{current.title}</strong>
            </span>
          </div>
          <div className="topbar-actions">
            <Preferences />
            <details className="account-menu">
              <summary>
                <span className="avatar">{admin.name.slice(0, 1)}</span>
                <span>{admin.name}</span>
              </summary>
              <div className="account-panel">
                <strong>{admin.name}</strong>
                <p>{admin.email}</p>
                <span className="badge status-approved">
                  <ShieldCheck size={14} />
                  Administrador
                </span>
                <button
                  className="button secondary full"
                  onClick={() => void logout()}
                >
                  <LogOut size={18} />
                  Cerrar sesión
                </button>
                <p className="small muted">
                  Solo se cierra esta sesión web. Tu cuenta en la app permanece
                  igual.
                </p>
              </div>
            </details>
          </div>
        </header>
        <main id="main" className="main-content">
          <div className="page-heading">
            <div>
              <span className="eyebrow">NOKI · EQUIPO</span>
              <h1 ref={mainTitle} tabIndex={-1}>
                {section === "overview" ? `Hola, ${admin.name}` : current.title}
              </h1>
              <p className="muted">{descriptions[section]}</p>
            </div>
            <button className="button secondary" onClick={refresh}>
              <RefreshCw size={17} />
              Actualizar
            </button>
          </div>
          <div className="sync-line" role="status">
            {online ? <Wifi size={14} /> : <WifiOff size={14} />}
            <span>
              {!online
                ? "Sin conexión. Los datos visibles pueden estar desactualizados."
                : live
                  ? "Actualización en vivo activa"
                  : "Comprobación automática cada 15 segundos"}
            </span>
          </div>
          {idleWarning && (
            <Message
              notice={{
                kind: "info",
                message:
                  "Tu sesión se cerrará pronto por inactividad. Interactúa con la página para continuar.",
              }}
            />
          )}
          {notice && (
            <Message notice={notice} onClose={() => setNotice(null)} />
          )}
          {section === "overview" || section === "reports" ? (
            <OverviewScreen
              mode={section}
              revision={revision}
              navigate={navigate}
              notify={setNotice}
            />
          ) : (
            <Records
              key={section}
              section={section}
              revision={revision}
              refresh={refresh}
              admin={admin}
              notify={setNotice}
            />
          )}
          <footer className="page-footer">
            <span>Noki · Administración</span>
            <span>
              <Check size={14} />
              Los cambios se guardan en el mismo sistema de la app.
            </span>
          </footer>
        </main>
      </div>
    </div>
  );
}
