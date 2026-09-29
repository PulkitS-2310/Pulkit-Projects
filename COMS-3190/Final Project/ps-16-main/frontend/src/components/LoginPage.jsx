import { useState } from "react";
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import "./M1Pages.css";

export default function LoginPage({ onLogin, user }) {
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from || "/";
  const startingMessage = location.state?.message || "";

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [message, setMessage] = useState(startingMessage);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (user) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setLoading(true);
      setError("");
      setMessage("");

      const result = await onLogin({
        email,
        password,
      });

      if (!result.ok) {
        setError(result.message || "Login failed.");
        return;
      }

      navigate(from, { replace: true });
    } catch {
      setError("Could not reach backend.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-layout">
      <Navbar />

      <main className="auth-page">
        <section className="auth-card">
          <div className="auth-card__icon">✦</div>

          <p className="section-eyebrow">Welcome Back</p>

          <h1 className="auth-title">Log In</h1>

          <p className="auth-card__sub">
            Sign in to manage your closet, plan outfits, and more.
          </p>

          {message && (
            <div className="auth-message auth-message--success">
              {message}
            </div>
          )}

          {error && (
            <div className="auth-message auth-message--error">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form">
            <label className="auth-label">
              Email
              <input
                className="auth-input"
                type="email"
                placeholder="name@example.com"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
              />
            </label>

            <label className="auth-label">
              Password
              <input
                className="auth-input"
                type="password"
                placeholder="Enter password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
              />
            </label>

            <button
              className="btn btn-primary auth-submit"
              type="submit"
              disabled={loading}
            >
              {loading ? "Logging in..." : "Log In"}
            </button>
          </form>

          <p className="auth-card__switch">
            Need an account? <Link to="/signup">Sign up</Link>
          </p>
        </section>
      </main>

      <Footer />
    </div>
  );
}