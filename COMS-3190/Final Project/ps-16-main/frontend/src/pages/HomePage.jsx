import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "./HomePage.css";

const FEATURED_OUTFITS = [
  { id: 1, name: "Coffee Run",     tag: "Casual",  itemCount: 3, color: "var(--blue-pale)",  emoji: "👕" },
  { id: 2, name: "Classy Evening", tag: "Dressy",  itemCount: 4, color: "var(--peri-pale)",  emoji: "👗" },
  { id: 3, name: "Campus Day",     tag: "Student", itemCount: 5, color: "var(--cta-pale)",   emoji: "🧢" },
  { id: 4, name: "Weekend Vibes",  tag: "Casual",  itemCount: 4, color: "var(--gold-pale)",  emoji: "🧣" },
];

const QUICK_LINKS = [
  { to: "/closet",    icon: "🧺", label: "My Closet",   sub: "View all your items",  color: "var(--cta)",        bg: "var(--cta-pale)"  },
  { to: "/builder",   icon: "✨", label: "Build Outfit", sub: "Create a new look",    color: "var(--periwinkle)", bg: "var(--peri-pale)" },
  { to: "/planner",   icon: "📅", label: "Planner",      sub: "Plan by date",         color: "var(--cta)",        bg: "var(--blue-pale)" },
  { to: "/favorites", icon: "♡",  label: "Favorites",    sub: "See saved looks",      color: "var(--gold)",       bg: "var(--gold-pale)" },
];

const STATS = [
  { value: "24", label: "Items in closet"   },
  { value: "8",  label: "Saved outfits"     },
  { value: "3",  label: "Planned this week" },
  { value: "5",  label: "Favorites"         },
];

export default function HomePage() {
  return (
    <div className="page-layout">
      <Navbar />

      <main className="home-page">

        {/* ── Hero ── */}
        <section className="hero">
          <div className="hero__bg-shapes" aria-hidden="true">
            <div className="hero__blob hero__blob--1" />
            <div className="hero__blob hero__blob--2" />
            <div className="hero__blob hero__blob--3" />
          </div>

          <div className="page-container hero__inner">
            <div className="hero__text">
              <p className="section-eyebrow">Digital Wardrobe</p>
              <h1 className="hero__heading">
                Plan Your Outfits.<br />
                <em>Elevate</em> Your Style.
              </h1>
              <p className="hero__sub">
                Organize your wardrobe, create complete looks, save your favorite outfits,
                and plan what to wear — all in one place.
              </p>
              <div className="hero__cta">
                <Link to="/builder" className="btn btn-primary btn-lg">
                  Start Building Outfits
                  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2.2" viewBox="0 0 24 24">
                    <path d="M5 12h14M12 5l7 7-7 7"/>
                  </svg>
                </Link>
                <Link to="/closet" className="btn btn-secondary btn-lg">
                  Go to My Closet
                </Link>
              </div>
            </div>

            <div className="hero__visual" aria-hidden="true">
              <div className="hero__card hero__card--back">
                <div className="hero__card-img" style={{ background: "linear-gradient(135deg, var(--peri-pale), var(--periwinkle))" }}>
                  <span style={{ fontSize: 48 }}>👗</span>
                </div>
                <div className="hero__card-label">Evening Gown · Dressy</div>
              </div>
              <div className="hero__card hero__card--front">
                <div className="hero__card-img" style={{ background: "linear-gradient(135deg, var(--blue-pale), #A8D0F0)" }}>
                  <span style={{ fontSize: 48 }}>🧥</span>
                </div>
                <div className="hero__card-label">Tan Blazer · Smart Casual</div>
                <div className="hero__card-badge">
                  <svg width="12" height="12" fill="var(--cta)" viewBox="0 0 24 24">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l7.78 7.78 7.78-7.78a5.5 5.5 0 0 0 0-7.78z"/>
                  </svg>
                  Favorited
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* ── Stats strip ── */}
        <section className="stats-strip">
          <div className="page-container">
            <div className="stats-strip__grid">
              {STATS.map((s, i) => (
                <div className="stats-strip__item" key={i}>
                  <span className="stats-strip__value">{s.value}</span>
                  <span className="stats-strip__label">{s.label}</span>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* ── Featured Outfits ── */}
        <section className="featured">
          <div className="page-container">
            <div className="featured__header">
              <div>
                <p className="section-eyebrow">Curated looks</p>
                <h2 className="featured__title">Featured Outfits</h2>
              </div>
              <Link to="/favorites" className="btn btn-ghost btn-sm">View Favorites →</Link>
            </div>
            <div className="featured__grid">
              {FEATURED_OUTFITS.map((o, i) => (
                <Link to={`/outfit/${o.id}`} key={o.id} className="outfit-card" style={{ animationDelay: `${i * 0.1}s` }}>
                  <div className="outfit-card__img" style={{ background: `linear-gradient(145deg, ${o.color}, ${o.color}cc)` }}>
                    <span className="outfit-card__emoji">{o.emoji}</span>
                    <button className="outfit-card__heart" onClick={e => e.preventDefault()} aria-label="Favorite">
                      <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l7.78 7.78 7.78-7.78a5.5 5.5 0 0 0 0-7.78z"/>
                      </svg>
                    </button>
                  </div>
                  <div className="outfit-card__body">
                    <h3 className="outfit-card__name">{o.name}</h3>
                    <div className="outfit-card__meta">
                      <span className="tag tag-muted">{o.tag}</span>
                      <span className="outfit-card__count">{o.itemCount} items</span>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </section>

        {/* ── Quick Links ── */}
        <section className="quicklinks">
          <div className="page-container">
            <p className="section-eyebrow" style={{ textAlign: "center", marginBottom: 8 }}>Navigate</p>
            <h2 className="quicklinks__title">Quick Access</h2>
            <div className="quicklinks__grid">
              {QUICK_LINKS.map((ql) => (
                <Link to={ql.to} key={ql.to} className="quicklink-card">
                  <div className="quicklink-card__icon" style={{ background: ql.bg, color: ql.color }}>
                    <span style={{ fontSize: 24 }}>{ql.icon}</span>
                  </div>
                  <div className="quicklink-card__text">
                    <span className="quicklink-card__label">{ql.label}</span>
                    <span className="quicklink-card__sub">{ql.sub}</span>
                  </div>
                  <svg className="quicklink-card__arrow" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                    <path d="M5 12h14M12 5l7 7-7 7"/>
                  </svg>
                </Link>
              ))}
            </div>
          </div>
        </section>

      </main>
      <Footer />
    </div>
  );
}