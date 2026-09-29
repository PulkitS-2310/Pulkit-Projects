import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "./FavoritesPage.css";

const API_URL = "http://localhost:5000/api";

const FILTERS = [
  "All",
  "Casual",
  "Smart Casual",
  "Formal",
  "Party",
  "Gym",
  "Date Night",
  "Work",
];

const TAG_MAP = {
  Casual: "tag-blue",
  Formal: "tag-periwinkle",
  Work: "tag-gold",
  "Smart Casual": "tag-blue",
};

function normalizeFavorite(outfit) {
  const itemNames = (outfit.items || []).map((item) => item.name || item);

  return {
    id: outfit.id,
    outfitId: outfit.id,
    outfitName: outfit.name,
    occasion: outfit.occasion || "Casual",
    season: outfit.season || "All Season",
    itemCount: itemNames.length,
    emoji: "👕",
    color: "var(--blue-pale)",
    items: itemNames,
    addedOn: outfit.createdAt
      ? new Date(outfit.createdAt).toLocaleDateString()
      : "Recently",
  };
}

export default function FavoritesPage() {
  const [favorites, setFavorites] = useState([]);
  const [filter, setFilter] = useState("All");
  const [sort, setSort] = useState("Newest First");
  const [removing, setRemoving] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadFavorites() {
    try {
      setLoading(true);
      setError("");

      const res = await fetch(`${API_URL}/outfits?favorite=true`);
      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to load favorites.");
      }

      setFavorites(data.map(normalizeFavorite));
    } catch (err) {
      setError(err.message || "Unable to load favorites.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadFavorites();
  }, []);

  async function handleRemove(outfitId) {
    try {
      setRemoving(outfitId);

      const res = await fetch(`${API_URL}/outfits/${outfitId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ favorite: false }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to remove favorite.");
      }

      setFavorites((currentFavorites) =>
        currentFavorites.filter((favorite) => favorite.id !== outfitId)
      );
    } catch (err) {
      setError(err.message || "Unable to remove favorite.");
    } finally {
      setRemoving(null);
    }
  }

  let displayed = [...favorites];

  if (filter !== "All") {
    displayed = displayed.filter((favorite) => favorite.occasion === filter);
  }

  if (sort === "Name A-Z") {
    displayed.sort((a, b) => a.outfitName.localeCompare(b.outfitName));
  }

  if (sort === "Name Z-A") {
    displayed.sort((a, b) => b.outfitName.localeCompare(a.outfitName));
  }

  if (sort === "Oldest First") {
    displayed.reverse();
  }

  return (
    <div className="page-layout">
      <Navbar />

      <main className="favs-page">
        <div className="page-container">
          <div className="favs__header">
            <div>
              <p className="section-eyebrow">Saved</p>
              <h1 className="favs__title">My Favorites</h1>
              <p className="favs__sub">Your go-to looks, all in one place.</p>
            </div>

            <div className="favs__count-badge">
              ♥ {favorites.length} saved look
              {favorites.length !== 1 ? "s" : ""}
            </div>
          </div>

          {error && (
            <div style={{ marginBottom: 16, color: "#8a1f1f", fontWeight: 700 }}>
              {error}
            </div>
          )}

          <div className="favs__toolbar">
            <div className="favs__filters">
              {FILTERS.map((option) => (
                <button
                  key={option}
                  className={`favs__filter ${
                    filter === option ? "favs__filter--active" : ""
                  }`}
                  onClick={() => setFilter(option)}
                >
                  {option}
                </button>
              ))}
            </div>

            <select
              className="favs__sort-select"
              value={sort}
              onChange={(event) => setSort(event.target.value)}
            >
              {["Newest First", "Oldest First", "Name A-Z", "Name Z-A"].map(
                (option) => (
                  <option key={option}>{option}</option>
                )
              )}
            </select>
          </div>

          {loading && (
            <div className="favs__empty">
              <h2>Loading favorites...</h2>
            </div>
          )}

          {!loading && displayed.length === 0 && (
            <div className="favs__empty">
              <div style={{ fontSize: 56 }}>💔</div>
              <h2>No favorites yet</h2>
              <p>
                {filter !== "All"
                  ? `No ${filter} outfits saved.`
                  : "Build and save an outfit to add it here."}
              </p>
              <Link to="/builder" className="btn btn-primary">
                Build an Outfit
              </Link>
            </div>
          )}

          {!loading && displayed.length > 0 && (
            <div className="favs__grid">
              {displayed.map((favorite, index) => (
                <div
                  key={favorite.id}
                  className={`fav-card ${
                    removing === favorite.id ? "fav-card--removing" : ""
                  }`}
                  style={{ animationDelay: `${index * 0.07}s` }}
                >
                  <Link
                    to={`/outfit/${favorite.outfitId}`}
                    className="fav-card__img-link"
                  >
                    <div
                      className="fav-card__img"
                      style={{
                        background: `linear-gradient(145deg, ${favorite.color}, ${favorite.color}bb)`,
                      }}
                    >
                      <span className="fav-card__emoji">{favorite.emoji}</span>

                      <div className="fav-card__items-preview">
                        {favorite.items.slice(0, 3).map((item, itemIndex) => (
                          <span key={itemIndex} className="fav-card__item-pill">
                            {item}
                          </span>
                        ))}

                        {favorite.items.length > 3 && (
                          <span className="fav-card__item-pill">
                            +{favorite.items.length - 3} more
                          </span>
                        )}
                      </div>
                    </div>
                  </Link>

                  <div className="fav-card__body">
                    <div className="fav-card__top">
                      <Link
                        to={`/outfit/${favorite.outfitId}`}
                        className="fav-card__name"
                      >
                        {favorite.outfitName}
                      </Link>

                      <button
                        className="fav-card__remove"
                        onClick={() => handleRemove(favorite.id)}
                        disabled={removing === favorite.id}
                        title="Remove from favorites"
                      >
                        ♥
                      </button>
                    </div>

                    <div className="fav-card__tags">
                      <span
                        className={`tag ${
                          TAG_MAP[favorite.occasion] || "tag-muted"
                        }`}
                      >
                        {favorite.occasion}
                      </span>
                      <span className="tag tag-muted">{favorite.season}</span>
                    </div>

                    <div className="fav-card__footer">
                      <span className="fav-card__meta">
                        {favorite.itemCount} items · {favorite.addedOn}
                      </span>

                      <div className="fav-card__actions">
                        <Link
                          to={`/outfit/${favorite.outfitId}`}
                          className="btn btn-secondary btn-sm"
                        >
                          View
                        </Link>

                        <Link to="/planner" className="btn btn-primary btn-sm">
                          Plan
                        </Link>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}