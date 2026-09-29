import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import "./M1Pages.css";

const COLOR_MAP = {
  black:"#1A1A2E", white:"#F8F9FA", grey:"#8D9DB6", gray:"#8D9DB6",
  red:"#E74C3C", blue:"#3498DB", navy:"#1A3A5C", green:"#2ECC71",
  yellow:"#F1C40F", pink:"#FF6B9D", purple:"#9B59B6", brown:"#8B4513",
  orange:"#E67E22", beige:"#C8A882", cream:"#FFF8DC",
};
function colorDot(c) {
  const k = (c||"").toLowerCase();
  return COLOR_MAP[k] || "#B8D4E8";
}

function MyClosetPage({ items, categories }) {
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [search, setSearch] = useState("");

  const visible = useMemo(() => {
    let list = selectedCategory === "All" ? items : items.filter(i => i.category === selectedCategory);
    if (search.trim()) list = list.filter(i => i.name.toLowerCase().includes(search.toLowerCase()));
    return list;
  }, [items, selectedCategory, search]);

  return (
    <div className="m1-page">
      <div className="m1-page__header">
        <div>
          <p className="section-eyebrow">Wardrobe</p>
          <h1 className="m1-page__title">My Closet</h1>
          <p className="m1-page__sub">Browse and filter your wardrobe items by category.</p>
        </div>
        <Link to="/items" className="btn btn-primary">+ Add Item</Link>
      </div>

      <div className="m1-toolbar">
        <div className="m1-cats">
          <button className={`m1-cat ${selectedCategory==="All"?"m1-cat--active":""}`} onClick={() => setSelectedCategory("All")}>All</button>
          {categories.map(c => (
            <button key={c.id} className={`m1-cat ${selectedCategory===c.name?"m1-cat--active":""}`} onClick={() => setSelectedCategory(c.name)}>{c.name}</button>
          ))}
        </div>
        <div className="m1-search">
          <svg width="15" height="15" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
          <input type="text" placeholder="Search items…" value={search} onChange={e => setSearch(e.target.value)} />
        </div>
      </div>

      {visible.length === 0 ? (
        <div className="m1-empty">
          <span style={{fontSize:48}}>🧺</span>
          <h3>No items found</h3>
          <p>{items.length === 0 ? "Start building your wardrobe by adding your first item." : "Try a different category or search term."}</p>
          <Link to="/items" className="btn btn-primary">Add Clothing Item</Link>
        </div>
      ) : (
        <div className="m1-grid">
          {visible.map(item => (
            <div className="closet-card" key={item.id}>
              <div className="closet-card__swatch" style={{ background: colorDot(item.color) }}>
                <span className="closet-card__swatch-initial">{(item.name||"?")[0].toUpperCase()}</span>
              </div>
              <div className="closet-card__body">
                <h3 className="closet-card__name">{item.name}</h3>
                <div className="closet-card__tags">
                  <span className="tag tag-blue">{item.category}</span>
                  {item.color && <span className="tag tag-muted">{item.color}</span>}
                  {item.size  && <span className="tag tag-muted">Size {item.size}</span>}
                </div>
                {item.description && <p className="closet-card__desc">{item.description}</p>}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default MyClosetPage;