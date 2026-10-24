// Apply preferences before first paint; no user or authentication data is read here.
try {
  const root = document.documentElement;
  const theme = localStorage.getItem("noki-admin-theme") || "system";
  root.dataset.theme =
    theme === "system"
      ? matchMedia("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light"
      : theme;
  const scale = localStorage.getItem("noki-admin-scale");
  const safeScale = ["100", "125", "150", "200"].includes(scale)
    ? scale
    : "100";
  root.style.fontSize = safeScale + "%";
  root.dataset.scale = safeScale;
  root.dataset.motion = localStorage.getItem("noki-admin-motion") || "auto";
  root.dataset.contrast =
    localStorage.getItem("noki-admin-contrast") || "normal";
} catch {
  /* System defaults remain accessible when storage is unavailable. */
}
