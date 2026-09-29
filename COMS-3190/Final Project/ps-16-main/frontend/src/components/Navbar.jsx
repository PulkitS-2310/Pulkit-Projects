import { useEffect, useState } from "react";
import { Link, NavLink } from "react-router-dom";
import "./Navbar.css";

const NAV_LINKS = [
  { to: "/", label: "Home" },
  { to: "/closet", label: "My Closet" },
  { to: "/builder", label: "Builder" },
  { to: "/planner", label: "Planner" },
  { to: "/favorites", label: "Favorites" },
  { to: "/items", label: "Items" },
  { to: "/categories", label: "Categories" },
  { to: "/admin", label: "Dashboard" },
  { to: "/faq", label: "FAQ" },
];

export default function Navbar() {
  const [isOpen, setIsOpen] = useState(false);
  const [isScrolled, setIsScrolled] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(
    Boolean(localStorage.getItem("outfitly_user"))
  );

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 8);

    handleScroll();
    window.addEventListener("scroll", handleScroll);

    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  useEffect(() => {
    const checkLogin = () => {
      setIsLoggedIn(Boolean(localStorage.getItem("outfitly_user")));
    };

    checkLogin();

    window.addEventListener("storage", checkLogin);
    window.addEventListener("focus", checkLogin);

    return () => {
      window.removeEventListener("storage", checkLogin);
      window.removeEventListener("focus", checkLogin);
    };
  }, []);

  const accountLink = isLoggedIn ? "/logout" : "/login";
  const accountTitle = isLoggedIn ? "Logout" : "Login";

  return (
    <nav className={`navbar ${isScrolled ? "navbar--scrolled" : ""}`}>
      <div className="navbar__inner">
        <Link className="navbar__logo" to="/" onClick={() => setIsOpen(false)}>
          <span className="navbar__logo-icon">✦</span>
          <span className="navbar__logo-text">Outfitly</span>
        </Link>

        <ul className="navbar__links">
          {NAV_LINKS.map((link) => (
            <li key={link.to}>
              <NavLink
                className={({ isActive }) =>
                  `navbar__link ${isActive ? "navbar__link--active" : ""}`
                }
                to={link.to}
                end={link.to === "/"}
              >
                {link.label}
              </NavLink>
            </li>
          ))}
        </ul>

        <div className="navbar__actions">
          <Link
            to={accountLink}
            className="navbar__avatar"
            title={accountTitle}
            onClick={() => setIsOpen(false)}
          >
            <svg
              width="18"
              height="18"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
              viewBox="0 0 24 24"
            >
              <circle cx="12" cy="8" r="4" />
              <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
            </svg>
          </Link>

          <button
            aria-label="Toggle menu"
            className={`navbar__burger ${
              isOpen ? "navbar__burger--open" : ""
            }`}
            onClick={() => setIsOpen((current) => !current)}
            type="button"
          >
            <span />
            <span />
            <span />
          </button>
        </div>
      </div>

      {isOpen && (
        <div className="navbar__mobile">
          {NAV_LINKS.map((link) => (
            <NavLink
              key={link.to}
              className={({ isActive }) =>
                `navbar__mobile-link ${
                  isActive ? "navbar__mobile-link--active" : ""
                }`
              }
              to={link.to}
              end={link.to === "/"}
              onClick={() => setIsOpen(false)}
            >
              {link.label}
            </NavLink>
          ))}

          <NavLink
            className={({ isActive }) =>
              `navbar__mobile-link ${
                isActive ? "navbar__mobile-link--active" : ""
              }`
            }
            to={accountLink}
            onClick={() => setIsOpen(false)}
          >
            {accountTitle}
          </NavLink>
        </div>
      )}
    </nav>
  );
}