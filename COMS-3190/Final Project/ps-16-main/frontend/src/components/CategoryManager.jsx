import { useState } from "react";
import "./M1Pages.css";

export default function CategoryManager({ categories, api }) {
  const [name, setName] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [editingName, setEditingName] = useState("");
  const [message, setMessage] = useState("");

  async function handleAdd(event) {
    event.preventDefault();

    try {
      setMessage("");

      if (!name.trim()) {
        setMessage("Category name is required.");
        return;
      }

      await api.addCategory(name.trim());
      setName("");
      setMessage("Category added.");
    } catch (error) {
      setMessage(error.message);
    }
  }

  function startEdit(category) {
    setEditingId(category.id);
    setEditingName(category.name);
    setMessage("");
  }

  function cancelEdit() {
    setEditingId(null);
    setEditingName("");
  }

  async function saveEdit(categoryId) {
    try {
      if (!editingName.trim()) {
        setMessage("Category name is required.");
        return;
      }

      await api.updateCategory(categoryId, editingName.trim());
      setEditingId(null);
      setEditingName("");
      setMessage("Category updated.");
    } catch (error) {
      setMessage(error.message);
    }
  }

  async function handleDelete(categoryId) {
    try {
      const confirmed = window.confirm(
        "Delete this category? Items in this category will also be removed."
      );

      if (!confirmed) return;

      await api.deleteCategory(categoryId);
      setMessage("Category deleted.");
    } catch (error) {
      setMessage(error.message);
    }
  }

  return (
    <section className="m1-page">
      <div className="m1-header">
        <p className="section-eyebrow">Manage</p>
        <h1>Category Management</h1>
        <p>Create, read, update, and delete closet categories.</p>
      </div>

      <form className="m1-card m1-form" onSubmit={handleAdd}>
        <input
          className="m1-input"
          type="text"
          placeholder="Category name"
          value={name}
          onChange={(event) => setName(event.target.value)}
        />

        <button className="btn btn-primary" type="submit">
          Add Category
        </button>
      </form>

      {message && <div className="m1-message">{message}</div>}

      <div className="m1-grid">
        {categories.length === 0 && (
          <p className="m1-empty">No categories added yet.</p>
        )}

        {categories.map((category) => (
          <div className="m1-card m1-row-card" key={category.id}>
            {editingId === category.id ? (
              <>
                <input
                  className="m1-input"
                  value={editingName}
                  onChange={(event) => setEditingName(event.target.value)}
                />

                <div className="m1-actions">
                  <button
                    className="btn btn-primary btn-sm"
                    type="button"
                    onClick={() => saveEdit(category.id)}
                  >
                    Save
                  </button>

                  <button
                    className="btn btn-secondary btn-sm"
                    type="button"
                    onClick={cancelEdit}
                  >
                    Cancel
                  </button>
                </div>
              </>
            ) : (
              <>
                <div>
                  <h3>{category.name}</h3>
                  
                </div>

                <div className="m1-actions">
                  <button
                    className="btn btn-secondary btn-sm"
                    type="button"
                    onClick={() => startEdit(category)}
                  >
                    Edit
                  </button>

                  <button
                    className="btn btn-danger btn-sm"
                    type="button"
                    onClick={() => handleDelete(category.id)}
                  >
                    Delete
                  </button>
                </div>
              </>
            )}
          </div>
        ))}
      </div>
    </section>
  );
}