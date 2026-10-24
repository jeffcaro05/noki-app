import { MapPin, PackageCheck, Smartphone } from "lucide-react";

/** Decorative only: no requests, timers, status indicators or input interception. */
export default function BrandCircuit({
  compact = false,
}: {
  compact?: boolean;
}) {
  return (
    <div
      className={`brand-circuit${compact ? " compact-circuit" : ""}`}
      aria-hidden="true"
    >
      <svg
        className="circuit-traces"
        viewBox="0 0 600 300"
        fill="none"
        focusable="false"
      >
        <g className="circuit-grid">
          <path d="M0 75H600M0 150H600M0 225H600M100 0V300M200 0V300M300 0V300M400 0V300M500 0V300" />
        </g>
        <g className="circuit-paths">
          <path d="M70 170H145L205 110H300" />
          <path d="M300 150H385L445 90H530" />
          <path d="M300 180H370L420 230H500" />
          <path d="M40 230H160L200 190M400 45H455L485 15M210 45H245L275 75" />
        </g>
        <g className="circuit-signal">
          <path d="M70 170H145L205 110H300" pathLength="100" />
          <path d="M300 150H385L445 90H530" pathLength="100" />
          <path d="M300 180H370L420 230H500" pathLength="100" />
        </g>
        <g className="circuit-terminals">
          <circle cx="40" cy="230" r="3" />
          <circle cx="485" cy="15" r="3" />
          <circle cx="210" cy="45" r="3" />
          <circle cx="200" cy="190" r="3" />
        </g>
      </svg>
      <div className="circuit-halo" />
      <div className="circuit-ring" />
      <div className="circuit-core">
        <img src="/brand/noki_symbol.png" alt="" />
      </div>
      <span className="circuit-node node-device">
        <Smartphone />
      </span>
      <span className="circuit-node node-place">
        <MapPin />
      </span>
      <span className="circuit-node node-delivery">
        <PackageCheck />
      </span>
    </div>
  );
}
