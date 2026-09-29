import { useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import "./M1Pages.css";

const FAQS = [
  { q:"How do I add a clothing item?",        a:'Go to Items, fill in the item name, category, color, size, and description, then click Add Item.' },
  { q:"Why do I need categories first?",       a:'Items are organised by category. Create at least one category (e.g. Tops, Bottoms) before adding clothing items.' },
  { q:"Can I edit or delete items?",           a:'Yes. On the Items page, each item card has Edit and Delete buttons for updating or removing that item.' },
  { q:"How do I build an outfit?",             a:'Go to Builder, select clothing items from your closet by clicking them, give the outfit a name, and click Save Outfit.' },
  { q:"How does the Planner work?",            a:'Click any date on the Planner calendar to open the day panel. Click "+ Add" to schedule a saved outfit for that day.' },
  { q:"How do I save a favorite outfit?",      a:'Open any outfit from the Featured Outfits section or the Builder and click the ♡ Add to Favorites button.' },
  { q:"How does signup and login work?",       a:'Signup stores a demo account in your browser, then takes you to Login. Use the same email and password you signed up with.' },
  { q:"What is the password rule?",            a:'Passwords must be at least 6 characters long.' },
  { q:"What is the Admin Dashboard?",          a:'The Admin Dashboard is for admin users only. It shows item counts, category breakdowns, and recently added items.' },
  { q:"Where is my data saved?",               a:'Closet items and categories are saved through the backend API. Login accounts are stored in your browser\'s localStorage for this demo.' },
];

export default function FAQPage() {
  const [open, setOpen] = useState(null);

  return (
    <div className="page-layout">
      <Navbar />
      <main className="faq-page">
        <div className="page-container">

          {/* Hero */}
          <div className="faq-hero">
            <p className="section-eyebrow">Help Center</p>
            <h1 className="faq-hero__title">Frequently Asked<br />Questions</h1>
            <p className="faq-hero__sub">Quick answers about using Outfitly — from adding items to planning outfits.</p>
            <div style={{ display:"flex", gap:12, flexWrap:"wrap", marginTop:8 }}>
              <Link to="/items"      className="btn btn-primary">Manage Items</Link>
              <Link to="/categories" className="btn btn-secondary">Manage Categories</Link>
              <Link to="/builder"    className="btn btn-secondary">Build an Outfit</Link>
            </div>
          </div>

          {/* FAQ list */}
          <div className="faq-list">
            {FAQS.map((faq, i) => (
              <div key={i} className={`faq-item ${open===i?"faq-item--open":""}`}>
                <button className="faq-item__q" onClick={() => setOpen(open===i ? null : i)}>
                  <span>{faq.q}</span>
                  <span className="faq-item__chevron">{open===i ? "▲" : "▼"}</span>
                </button>
                {open===i && <div className="faq-item__a">{faq.a}</div>}
              </div>
            ))}
          </div>

          {/* Still need help */}
          <div className="faq-footer-card">
            <span style={{fontSize:32}}>💬</span>
            <div>
              <h3>Still need help?</h3>
              <p>Contact the team at <a href="mailto:pulkit@iastate.edu" style={{color:"var(--cta)"}}>pulkit@iastate.edu</a> or <a href="mailto:mokshi28@iastate.edu" style={{color:"var(--cta)"}}>mokshi28@iastate.edu</a></p>
            </div>
          </div>

        </div>
      </main>
      <Footer />
    </div>
  );
}
