import { Link } from "react-router-dom";
import "./M1Pages.css";

function AdminDashboard({ items, categories }) {
  const newestItems = [...items].reverse().slice(0, 5);

  const byCategory = categories.map(cat => ({
    ...cat,
    count: items.filter(i => i.category === cat.name).length,
  }));

  return (
    <div className="m1-page">
      <div className="m1-page__header">
        <div>
          <p className="section-eyebrow">Admin Only</p>
          <h1 className="m1-page__title">Admin Dashboard</h1>
          <p className="m1-page__sub">Overview of all closet items, categories, and system data.</p>
        </div>
        <div style={{ display:"flex", gap:10 }}>
          <Link to="/items"      className="btn btn-primary btn-sm">Manage Items</Link>
          <Link to="/categories" className="btn btn-secondary btn-sm">Manage Categories</Link>
        </div>
      </div>

      {/* Stats */}
      <div className="admin-stats">
        <div className="admin-stat-card">
          <div className="admin-stat-card__icon" style={{background:"var(--blue-pale)",color:"var(--cta)"}}>👗</div>
          <div>
            <p className="admin-stat-card__label">Total Items</p>
            <p className="admin-stat-card__value">{items.length}</p>
          </div>
        </div>
        <div className="admin-stat-card">
          <div className="admin-stat-card__icon" style={{background:"var(--peri-pale)",color:"var(--peri-deep)"}}>🏷</div>
          <div>
            <p className="admin-stat-card__label">Categories</p>
            <p className="admin-stat-card__value">{categories.length}</p>
          </div>
        </div>
        <div className="admin-stat-card">
          <div className="admin-stat-card__icon" style={{background:"var(--gold-pale)",color:"#8A6A1A"}}>📊</div>
          <div>
            <p className="admin-stat-card__label">Avg Items / Category</p>
            <p className="admin-stat-card__value">
              {categories.length > 0 ? (items.length / categories.length).toFixed(1) : "—"}
            </p>
          </div>
        </div>
        <div className="admin-stat-card">
          <div className="admin-stat-card__icon" style={{background:"var(--cta-pale)",color:"var(--cta)"}}>✅</div>
          <div>
            <p className="admin-stat-card__label">Ready to use</p>
            <p className="admin-stat-card__value">{categories.length > 0 && items.length > 0 ? "Yes" : "No"}</p>
          </div>
        </div>
      </div>

      <div className="admin-body">

        {/* Recently added */}
        <div className="admin-panel">
          <div className="admin-panel__header">
            <h2 className="admin-panel__title">Recently Added Items</h2>
            <Link to="/items" className="btn btn-ghost btn-sm">View all →</Link>
          </div>
          {newestItems.length === 0 ? (
            <div className="m1-empty" style={{padding:"32px 0"}}>
              <p>No items yet. <Link to="/items" style={{color:"var(--cta)"}}>Add your first item →</Link></p>
            </div>
          ) : (
            <div className="admin-table">
              <div className="admin-table__head">
                <span>Name</span><span>Category</span><span>Color</span><span>Size</span>
              </div>
              {newestItems.map(item => (
                <div className="admin-table__row" key={item.id}>
                  <span className="admin-table__name">{item.name}</span>
                  <span className="tag tag-blue">{item.category}</span>
                  <span className="tag tag-muted">{item.color}</span>
                  <span className="tag tag-muted">{item.size}</span>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Categories breakdown */}
        <div className="admin-panel">
          <div className="admin-panel__header">
            <h2 className="admin-panel__title">Category Breakdown</h2>
            <Link to="/categories" className="btn btn-ghost btn-sm">Manage →</Link>
          </div>
          {byCategory.length === 0 ? (
            <div className="m1-empty" style={{padding:"32px 0"}}>
              <p>No categories yet. <Link to="/categories" style={{color:"var(--cta)"}}>Create one →</Link></p>
            </div>
          ) : (
            <div style={{display:"flex",flexDirection:"column",gap:10}}>
              {byCategory.map(cat => (
                <div key={cat.id} className="admin-cat-row">
                  <span className="admin-cat-row__name">{cat.name}</span>
                  <div className="admin-cat-row__bar-wrap">
                    <div className="admin-cat-row__bar"
                      style={{ width: items.length > 0 ? `${(cat.count/items.length)*100}%` : "0%" }} />
                  </div>
                  <span className="admin-cat-row__count">{cat.count} item{cat.count!==1?"s":""}</span>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
}

export default AdminDashboard;