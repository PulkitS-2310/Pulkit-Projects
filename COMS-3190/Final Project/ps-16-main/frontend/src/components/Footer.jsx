import "./Footer.css";

export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer__inner page-container">
        <span className="footer__logo">
          <span style={{ color: "var(--cta)" }}>✦</span> Outfitly
        </span>
        <p className="footer__copy">© 2026 Outfitly · Digital Closet & Outfit Planner · PS-16</p>
      </div>
    </footer>
  );
}