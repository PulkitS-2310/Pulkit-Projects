import { Link, useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import "./M1Pages.css";

function LogoutPage({ user, onLogout }) {
  const navigate = useNavigate();

  function confirmLogout() {
    if (window.confirm("Are you sure you want to log out?")) {
      onLogout();
      navigate("/login");
    }
  }

  return (
    <div className="page-layout">
      <Navbar />
      <main className="auth-page">
        <div className="auth-card">
          <div className="auth-card__icon">{user ? "👋" : "🔒"}</div>
          <p className="section-eyebrow">Account</p>
          <h1 className="auth-card__title">{user ? "Log Out" : "Not signed in"}</h1>

          {user ? (
            <>
              <p className="auth-card__sub">You're signed in as <strong>{user.email}</strong>. Ready to leave?</p>
              <button className="btn btn-primary" style={{width:"100%"}} onClick={confirmLogout}>Log Out</button>
              <button className="btn btn-secondary" style={{width:"100%",marginTop:8}} onClick={() => navigate("/")}>Stay — Go Home</button>
            </>
          ) : (
            <>
              <p className="auth-card__sub">You're not currently signed in.</p>
              <Link to="/login" className="btn btn-primary" style={{width:"100%",textAlign:"center"}}>Go to Login</Link>
            </>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
}

export default LogoutPage;
