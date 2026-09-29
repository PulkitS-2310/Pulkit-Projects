import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "./OutfitDetailPage.css";

const API_URL = "http://localhost:5000/api";

function getEmoji(category) {
  const map = {
    Tops: "👕",
    Bottoms: "👖",
    Shoes: "👟",
    Accessories: "👜",
    Outerwear: "🧥",
    Dresses: "👗",
  };

  return map[category] || "👕";
}

function getCardColor(category) {
  const map = {
    Tops: "#EEF5FB",
    Bottoms: "#D0E4F5",
    Shoes: "#E0F0F9",
    Accessories: "#FAF3E0",
    Outerwear: "#DCEAF5",
    Dresses: "#EBF0FA",
  };

  return map[category] || "#EEF5FB";
}

export default function OutfitDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [outfit, setOutfit] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const [showPlannerForm, setShowPlannerForm] = useState(false);
  const [plannerDate, setPlannerDate] = useState(
    new Date().toISOString().slice(0, 10)
  );
  const [plannerNote, setPlannerNote] = useState("");

  async function loadOutfit() {
    try {
      setLoading(true);
      setError("");

      const res = await fetch(`${API_URL}/outfits/${id}`);
      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to load outfit.");
      }

      setOutfit(data);
    } catch (err) {
      setError(err.message || "Unable to load outfit.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadOutfit();
  }, [id]);

  async function toggleFavorite() {
    try {
      if (!outfit) return;

      setSaving(true);
      setMessage("");
      setError("");

      const res = await fetch(`${API_URL}/outfits/${outfit.id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          favorite: !outfit.favorite,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to update favorite.");
      }

      setOutfit(data);
      setMessage(data.favorite ? "Added to favorites." : "Removed from favorites.");
    } catch (err) {
      setError(err.message || "Unable to update favorite.");
    } finally {
      setSaving(false);
    }
  }

  function addToPlanner() {
    setShowPlannerForm(true);
    setMessage("");
    setError("");
  }

  async function saveToPlanner() {
    try {
      if (!outfit) return;

      if (!plannerDate) {
        setError("Please choose a date.");
        return;
      }

      setSaving(true);
      setMessage("");
      setError("");

      const res = await fetch(`${API_URL}/planner`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          date: plannerDate,
          outfitId: outfit.id,
          outfitName: outfit.name,
          notes: plannerNote,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to add outfit to planner.");
      }

      setMessage("Added to planner.");
      setShowPlannerForm(false);
      setPlannerNote("");

      navigate(`/planner?date=${plannerDate}`);
    } catch (err) {
      setError(err.message || "Unable to add outfit to planner.");
    } finally {
      setSaving(false);
    }
  }

  async function deleteOutfit() {
    try {
      if (!outfit) return;

      const confirmed = window.confirm("Delete this outfit?");

      if (!confirmed) return;

      setSaving(true);
      setMessage("");
      setError("");

      const res = await fetch(`${API_URL}/outfits/${outfit.id}`, {
        method: "DELETE",
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to delete outfit.");
      }

      navigate("/favorites");
    } catch (err) {
      setError(err.message || "Unable to delete outfit.");
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <div className="page-layout">
        <Navbar />
        <main className="detail-page">
          <div className="page-container">
            <p>Loading outfit...</p>
          </div>
        </main>
        <Footer />
      </div>
    );
  }

  if (error && !outfit) {
    return (
      <div className="page-layout">
        <Navbar />
        <main className="detail-page">
          <div className="page-container">
            <p style={{ color: "#8a1f1f", fontWeight: 700 }}>{error}</p>
            <Link to="/favorites" className="btn btn-primary">
              Back to Favorites
            </Link>
          </div>
        </main>
        <Footer />
      </div>
    );
  }

  return (
    <div className="page-layout">
      <Navbar />

      <main className="detail-page">
        <div className="page-container">
          <div style={{ marginBottom: 28 }}>
            <Link
              to="/favorites"
              style={{ color: "var(--cta)", fontWeight: 700 }}
            >
              Outfits
            </Link>
            <span style={{ margin: "0 8px" }}>›</span>
            <span>{outfit.name}</span>
          </div>

          {message && (
            <div style={{ marginBottom: 16, color: "#1f7a3a", fontWeight: 700 }}>
              {message}
            </div>
          )}

          {error && (
            <div style={{ marginBottom: 16, color: "#8a1f1f", fontWeight: 700 }}>
              {error}
            </div>
          )}

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "minmax(320px, 0.9fr) minmax(420px, 1.4fr)",
              gap: 40,
              alignItems: "start",
            }}
          >
            <section
              style={{
                background: "linear-gradient(145deg, #dceaf5, #eef5fb)",
                border: "1px solid var(--line)",
                borderRadius: 28,
                overflow: "hidden",
                boxShadow: "var(--shadow-soft)",
              }}
            >
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr",
                  gap: 16,
                  padding: 56,
                }}
              >
                {(outfit.items || []).map((item, index) => (
                  <div
                    key={`${item.id || item.name}-${index}`}
                    style={{
                      minHeight: 150,
                      borderRadius: 18,
                      background: getCardColor(item.category),
                      border: "1px solid var(--line)",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontSize: 42,
                    }}
                  >
                    {getEmoji(item.category)}
                  </div>
                ))}
              </div>

              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  gap: 12,
                  padding: "18px 24px",
                  background: "rgba(255,255,255,0.65)",
                }}
              >
                <div>
                  <span className="tag tag-blue">
                    {outfit.occasion || "Casual"}
                  </span>
                  <span className="tag tag-muted" style={{ marginLeft: 8 }}>
                    {outfit.season || "All Season"}
                  </span>
                </div>

                <span style={{ color: "var(--ink-muted)" }}>
                  Created{" "}
                  {outfit.createdAt
                    ? new Date(outfit.createdAt).toLocaleDateString()
                    : "recently"}
                </span>
              </div>
            </section>

            <section>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  gap: 24,
                  alignItems: "center",
                  marginBottom: 24,
                }}
              >
                <h1
                  style={{
                    fontFamily: "var(--font-display)",
                    fontSize: 52,
                    color: "var(--ink)",
                    margin: 0,
                  }}
                >
                  {outfit.name}
                </h1>

                <button
                  className="btn btn-secondary"
                  onClick={toggleFavorite}
                  disabled={saving}
                >
                  {outfit.favorite ? "♥ Remove Favorite" : "♡ Add to Favorites"}
                </button>
              </div>

              <div
                style={{
                  background: "white",
                  borderLeft: "4px solid var(--cta-light)",
                  borderRadius: 16,
                  padding: 22,
                  color: "var(--ink-muted)",
                  marginBottom: 28,
                  fontSize: 18,
                }}
              >
                {outfit.notes || "No notes added for this outfit."}
              </div>

              <div style={{ marginBottom: 18 }}>
                <h2
                  style={{
                    fontFamily: "var(--font-display)",
                    fontSize: 24,
                    marginBottom: 12,
                  }}
                >
                  Items in this outfit{" "}
                  <span className="tag tag-muted">
                    {outfit.items?.length || 0}
                  </span>
                </h2>

                <div style={{ display: "grid", gap: 12 }}>
                  {(outfit.items || []).map((item, index) => (
                    <div
                      key={`${item.id || item.name}-${index}`}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: 16,
                        background: "white",
                        border: "1px solid var(--line)",
                        borderRadius: 16,
                        padding: 16,
                      }}
                    >
                      <div
                        style={{
                          width: 54,
                          height: 54,
                          borderRadius: 10,
                          background: getCardColor(item.category),
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                          fontSize: 26,
                        }}
                      >
                        {getEmoji(item.category)}
                      </div>

                      <div>
                        <strong>{item.name}</strong>
                        <p style={{ margin: 0, color: "var(--ink-muted)" }}>
                          {item.category}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div
                style={{
                  display: "flex",
                  gap: 14,
                  marginTop: 28,
                  flexWrap: "wrap",
                }}
              >
                <button
                  className="btn btn-primary"
                  onClick={addToPlanner}
                  disabled={saving}
                >
                  🗓️ Add to Planner
                </button>

                <Link
                  to={`/builder?edit=${outfit.id}`}
                  className="btn btn-secondary"
                >
                  Edit Outfit
                </Link>

                <button
                  className="btn btn-danger"
                  onClick={deleteOutfit}
                  disabled={saving}
                >
                  Delete
                </button>
              </div>

              {showPlannerForm && (
                <div
                  style={{
                    marginTop: 22,
                    background: "white",
                    border: "1px solid var(--line)",
                    borderRadius: 18,
                    padding: 20,
                    boxShadow: "var(--shadow-soft)",
                  }}
                >
                  <h3
                    style={{
                      fontFamily: "var(--font-display)",
                      fontSize: 24,
                      marginBottom: 14,
                      color: "var(--ink)",
                    }}
                  >
                    Schedule this outfit
                  </h3>

                  <div style={{ display: "grid", gap: 12 }}>
                    <label style={{ fontWeight: 700 }}>
                      Choose date
                      <input
                        className="bform-input"
                        type="date"
                        value={plannerDate}
                        onChange={(event) => setPlannerDate(event.target.value)}
                        style={{ marginTop: 6 }}
                      />
                    </label>

                    <label style={{ fontWeight: 700 }}>
                      Notes
                      <textarea
                        className="bform-input bform-textarea"
                        rows={3}
                        placeholder="Optional note for this planned outfit..."
                        value={plannerNote}
                        onChange={(event) => setPlannerNote(event.target.value)}
                        style={{ marginTop: 6 }}
                      />
                    </label>

                    <div
                      style={{
                        display: "flex",
                        gap: 10,
                        justifyContent: "flex-end",
                      }}
                    >
                      <button
                        className="btn btn-secondary"
                        type="button"
                        onClick={() => {
                          setShowPlannerForm(false);
                          setPlannerNote("");
                        }}
                      >
                        Cancel
                      </button>

                      <button
                        className="btn btn-primary"
                        type="button"
                        onClick={saveToPlanner}
                        disabled={saving || !plannerDate}
                      >
                        {saving ? "Saving..." : "Save to Planner"}
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </section>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}