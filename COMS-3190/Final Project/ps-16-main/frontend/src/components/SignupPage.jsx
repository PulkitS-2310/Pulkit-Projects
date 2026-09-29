import { useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import "./M1Pages.css";

export default function SignupPage({ onSignup, user }) {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

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

      if (password !== confirmPassword) {
        setError("Passwords do not match.");
        return;
      }

      if (password.length < 6) {
        setError("Password must be at least 6 characters.");
        return;
      }

      const result = await onSignup({
        name: email.split("@")[0],
        email,
        password,
      });

      if (!result.ok) {
        setError(result.message || "Signup failed.");
        return;
      }

      navigate("/login", {
        replace: true,
        state: {
          message: "Account created. Please log in.",
        },
      });
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

          <p className="section-eyebrow">Get Started</p>

          <h1 className="auth-title">Create Account</h1>

          <p className="auth-card__sub">
            Sign up to manage your closet and plan your outfits.
          </p>

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
                placeholder="Create password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
              />
            </label>

            <label className="auth-label">
              Confirm Password
              <input
                className="auth-input"
                type="password"
                placeholder="Confirm password"
                value={confirmPassword}
                onChange={(event) => setConfirmPassword(event.target.value)}
                required
              />
            </label>

            <button
              className="btn btn-primary auth-submit"
              type="submit"
              disabled={loading}
            >
              {loading ? "Creating..." : "Create Account"}
            </button>
          </form>

          <p className="auth-card__switch">
            Already have an account? <Link to="/login">Log in</Link>
          </p>
        </section>
      </main>

      <Footer />
    </div>
  );
}