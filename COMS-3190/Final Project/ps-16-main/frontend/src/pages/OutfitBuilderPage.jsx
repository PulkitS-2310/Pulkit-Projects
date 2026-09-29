import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "./OutfitBuilderPage.css";

const API_URL = "http://localhost:5000/api";

const OCCASIONS = [
  "Casual",
  "Smart Casual",
  "Formal",
  "Party",
  "Gym",
  "Date Night",
  "Work",
];

const SEASONS = ["All Season", "Spring", "Summer", "Fall", "Winter"];

function getEmoji(item) {
  const name = (item.name || "").toLowerCase();

  const NAME_EMOJI = [
    { keys: ["polo"],                                            emoji: "👕" },
    { keys: ["t-shirt","tshirt","tee"],                         emoji: "👕" },
    { keys: ["shirt","button"],                                  emoji: "👔" },
    { keys: ["blouse"],                                          emoji: "👚" },
    { keys: ["hoodie","sweatshirt","sweater","pullover","knit"], emoji: "🧶" },
    { keys: ["tank","cami","crop"],                              emoji: "🩱" },
    { keys: ["coat","trench","parka","anorak"],                  emoji: "🧥" },
    { keys: ["jacket","blazer","suit"],                          emoji: "🧥" },
    { keys: ["vest"],                                            emoji: "🦺" },
    { keys: ["jean","denim"],                                    emoji: "👖" },
    { keys: ["trouser","pant","chino","slack"],                  emoji: "👖" },
    { keys: ["short"],                                           emoji: "🩳" },
    { keys: ["skirt","mini","midi"],                             emoji: "🩱" },
    { keys: ["legging","jogger","sweatpant"],                    emoji: "🩲" },
    { keys: ["saree","sari"],                                    emoji: "🥻" },
    { keys: ["kimono"],                                          emoji: "🥻" },
    { keys: ["dress","gown","frock"],                            emoji: "👗" },
    { keys: ["jumpsuit","romper"],                               emoji: "🩱" },
    { keys: ["heel","stiletto","pump"],                          emoji: "👠" },
    { keys: ["boot","ankle boot"],                               emoji: "👢" },
    { keys: ["sneaker","trainer","runner","sport shoe"],         emoji: "👟" },
    { keys: ["sandal","flip","slipper","slide"],                 emoji: "🩴" },
    { keys: ["loafer","moccasin","flat"],                        emoji: "🥿" },
    { keys: ["formal shoe","oxford","derby","brogue"],           emoji: "👞" },
    { keys: ["wedge"],                                           emoji: "👡" },
    { keys: ["bag","tote","backpack","satchel"],                 emoji: "👜" },
    { keys: ["purse","clutch","wristlet"],                       emoji: "👛" },
    { keys: ["hat","cap","beanie","beret","fedora"],             emoji: "🧢" },
    { keys: ["sun hat","straw hat","wide brim"],                 emoji: "👒" },
    { keys: ["scarf","wrap","stole"],                            emoji: "🧣" },
    { keys: ["glove","mitten"],                                  emoji: "🧤" },
    { keys: ["sock","stocking"],                                 emoji: "🧦" },
    { keys: ["necklace","chain","pendant","choker"],             emoji: "📿" },
    { keys: ["bracelet","bangle","cuff"],                        emoji: "💍" },
    { keys: ["ring"],                                            emoji: "💍" },
    { keys: ["earring","stud","hoop"],                           emoji: "💎" },
    { keys: ["watch"],                                           emoji: "⌚" },
    { keys: ["belt"],                                            emoji: "👔" },
    { keys: ["sunglasses","glasses","shades"],                   emoji: "🕶️" },
    { keys: ["tie","bow tie","necktie"],                         emoji: "👔" },
    { keys: ["wallet"],                                          emoji: "👛" },
  ];

  for (const { keys, emoji } of NAME_EMOJI) {
    if (keys.some(k => name.includes(k))) return emoji;
  }

  const map = {
    Tops: "👕", Bottoms: "👖", Shoes: "👟", Accessories: "👜",
    Outerwear: "🧥", Dresses: "👗",
  };
  return map[item.category] || "👚";
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

function getItemImage(item) {
  return (
    item.imageUrl ||
    item.image ||
    item.photo ||
    item.picture ||
    item.photoUrl ||
    item.img ||
    ""
  );
}

function ItemImage({ item, className, selected }) {
  const imageUrl = getItemImage(item);
  const fallbackEmoji = getEmoji(item);

  return (
    <div
      className={className}
      style={{
        background: getCardColor(item.category),
        position: "relative",
        overflow: "hidden",
      }}
    >
      {imageUrl ? (
        <>
          <img
            src={imageUrl}
            alt={item.name}
            style={{
              width: "100%",
              height: "100%",
              objectFit: "cover",
              borderRadius: "inherit",
              display: "block",
            }}
            onError={(event) => {
              event.currentTarget.style.display = "none";
              const fallback = event.currentTarget.nextElementSibling;
              if (fallback) {
                fallback.style.display = "inline";
              }
            }}
          />
          <span style={{ display: "none" }}>{fallbackEmoji}</span>
        </>
      ) : (
        <span>{fallbackEmoji}</span>
      )}

      {selected && <div className="closet-item__check">✓</div>}
    </div>
  );
}

export default function OutfitBuilderPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const params = new URLSearchParams(location.search);
  const editId = params.get("edit");

  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [activeCategory, setActiveCategory] = useState("All");
  const [selectedItems, setSelectedItems] = useState([]);

  const [outfitName, setOutfitName] = useState("");
  const [occasion, setOccasion] = useState("");
  const [season, setSeason] = useState("");
  const [notes, setNotes] = useState("");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState("");

  async function loadData() {
    try {
      setLoading(true);
      setError("");

      const [itemsRes, categoriesRes] = await Promise.all([
        fetch(`${API_URL}/items`),
        fetch(`${API_URL}/categories`),
      ]);

      const itemsData = await itemsRes.json();
      const categoriesData = await categoriesRes.json();

      if (!itemsRes.ok) {
        throw new Error(itemsData.error || "Unable to load items.");
      }

      if (!categoriesRes.ok) {
        throw new Error(categoriesData.error || "Unable to load categories.");
      }

      setItems(itemsData);
      setCategories(categoriesData);

      if (editId) {
        const outfitRes = await fetch(`${API_URL}/outfits/${editId}`);
        const outfitData = await outfitRes.json();

        if (!outfitRes.ok) {
          throw new Error(outfitData.error || "Unable to load outfit.");
        }

        setOutfitName(outfitData.name || "");
        setOccasion(outfitData.occasion || "");
        setSeason(outfitData.season || "");
        setNotes(outfitData.notes || "");
        setSelectedItems(outfitData.items || []);
      }
    } catch (err) {
      setError(err.message || "Unable to load builder data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, [editId]);

  const categoryNames = useMemo(() => {
    const fromCategories = categories.map((category) => category.name);
    const fromItems = items.map((item) => item.category).filter(Boolean);

    return ["All", ...Array.from(new Set([...fromCategories, ...fromItems]))];
  }, [categories, items]);

  const filtered =
    activeCategory === "All"
      ? items
      : items.filter((item) => item.category === activeCategory);

  function toggleItem(item) {
    setSelectedItems((previousItems) =>
      previousItems.find((selected) => selected.id === item.id)
        ? previousItems.filter((selected) => selected.id !== item.id)
        : [...previousItems, item]
    );
  }

  function isSelected(id) {
    return selectedItems.some((item) => item.id === id);
  }

  function clearOutfit() {
    setSelectedItems([]);
    setOutfitName("");
    setOccasion("");
    setSeason("");
    setNotes("");
    setSaved(false);
    setError("");
  }

  async function handleSave() {
    try {
      if (!outfitName.trim()) {
        setError("Please enter an outfit name.");
        return;
      }

      if (selectedItems.length === 0) {
        setError("Please select at least one item.");
        return;
      }

      setSaving(true);
      setError("");

      const payload = {
        name: outfitName.trim(),
        occasion,
        season,
        notes,
        favorite: true,
        items: selectedItems.map((item) => ({
          id: item.id,
          name: item.name,
          category: item.category,
          color: item.color,
          size: item.size,
          description: item.description || "",
          imageUrl: getItemImage(item),
        })),
      };

      const res = await fetch(
        editId ? `${API_URL}/outfits/${editId}` : `${API_URL}/outfits`,
        {
          method: editId ? "PUT" : "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        }
      );

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to save outfit.");
      }

      setSaved(true);

      setTimeout(() => {
        navigate("/favorites");
      }, 700);
    } catch (err) {
      setError(err.message || "Unable to save outfit.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page-layout">
      <Navbar />

      <main className="builder-page">
        <div className="page-container">
          <div className="builder__header">
            <div>
              <p className="section-eyebrow">Create</p>
              <h1 className="builder__title">
                {editId ? "Edit Outfit" : "Build a New Outfit"}
              </h1>
              <p className="builder__sub">
                Select items from your closet to create and save your look.
              </p>
            </div>

            <div className="builder__header-actions">
              <button className="btn btn-secondary" onClick={clearOutfit}>
                ↺ Clear
              </button>

              <button
                className="btn btn-primary"
                onClick={handleSave}
                disabled={
                  saving ||
                  saved ||
                  !outfitName.trim() ||
                  selectedItems.length === 0
                }
              >
                {saved
                  ? "✓ Saved!"
                  : saving
                  ? "Saving…"
                  : editId
                  ? "💾 Update Outfit"
                  : "💾 Save Outfit"}
              </button>
            </div>
          </div>

          {error && (
            <div style={{ marginBottom: 16, color: "#8a1f1f", fontWeight: 700 }}>
              {error}
            </div>
          )}

          {loading ? (
            <div className="builder__empty">
              <p>Loading closet items...</p>
            </div>
          ) : (
            <div className="builder__body">
              <div className="builder__closet">
                <div className="builder__closet-head">
                  <h2 className="builder__section-title">Your Closet</h2>
                  <span className="tag tag-muted">{filtered.length} items</span>
                </div>

                <div className="builder__cats">
                  {categoryNames.map((category) => (
                    <button
                      key={category}
                      className={`builder__cat ${
                        activeCategory === category
                          ? "builder__cat--active"
                          : ""
                      }`}
                      onClick={() => setActiveCategory(category)}
                    >
                      {category}
                    </button>
                  ))}
                </div>

                <div className="builder__items">
                  {filtered.map((item) => (
                    <button
                      key={item.id}
                      className={`closet-item ${
                        isSelected(item.id) ? "closet-item--selected" : ""
                      }`}
                      onClick={() => toggleItem(item)}
                    >
                      <ItemImage
                        item={item}
                        className="closet-item__img"
                        selected={isSelected(item.id)}
                      />

                      <span className="closet-item__name">{item.name}</span>
                      <span className="closet-item__cat">{item.category}</span>
                    </button>
                  ))}
                </div>
              </div>

              <div className="builder__preview-col">
                <div className="builder__preview">
                  <h2 className="builder__section-title">Outfit Preview</h2>

                  {selectedItems.length === 0 ? (
                    <div className="builder__empty">
                      <span style={{ fontSize: 40 }}>🧺</span>
                      <p>Your outfit will appear here as you add items</p>
                    </div>
                  ) : (
                    <div className="builder__preview-items">
                      {selectedItems.map((item) => (
                        <div key={item.id} className="preview-item">
                          <ItemImage
                            item={item}
                            className="preview-item__img"
                          />

                          <div className="preview-item__info">
                            <span className="preview-item__name">
                              {item.name}
                            </span>
                            <span className="preview-item__cat">
                              {item.category}
                            </span>
                          </div>

                          <button
                            className="preview-item__remove"
                            onClick={() => toggleItem(item)}
                            type="button"
                          >
                            ✕
                          </button>
                        </div>
                      ))}
                    </div>
                  )}

                  <div className="builder__preview-count">
                    {selectedItems.length} item
                    {selectedItems.length !== 1 ? "s" : ""} selected
                  </div>
                </div>

                <div className="builder__form">
                  <h2 className="builder__section-title">Outfit Details</h2>

                  <div className="bform-group">
                    <label className="bform-label">Outfit Name *</label>
                    <input
                      className="bform-input"
                      type="text"
                      placeholder="e.g. Coffee Run Look"
                      value={outfitName}
                      onChange={(event) => setOutfitName(event.target.value)}
                    />
                  </div>

                  <div className="bform-row">
                    <div className="bform-group">
                      <label className="bform-label">Occasion</label>
                      <select
                        className="bform-input"
                        value={occasion}
                        onChange={(event) => setOccasion(event.target.value)}
                      >
                        <option value="">Select…</option>

                        {OCCASIONS.map((option) => (
                          <option key={option}>{option}</option>
                        ))}
                      </select>
                    </div>

                    <div className="bform-group">
                      <label className="bform-label">Season</label>
                      <select
                        className="bform-input"
                        value={season}
                        onChange={(event) => setSeason(event.target.value)}
                      >
                        <option value="">Select…</option>

                        {SEASONS.map((option) => (
                          <option key={option}>{option}</option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="bform-group">
                    <label className="bform-label">Notes</label>
                    <textarea
                      className="bform-input bform-textarea"
                      rows={3}
                      placeholder="Any notes about this outfit…"
                      value={notes}
                      onChange={(event) => setNotes(event.target.value)}
                    />
                  </div>

                  {selectedItems.length === 0 && (
                    <p className="bform-hint">
                      ← Select at least one item from your closet to save.
                    </p>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}