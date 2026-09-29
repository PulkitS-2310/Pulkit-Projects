import { useState } from "react";
import "./M1Pages.css";

const emptyForm = {
  name: "",
  category: "",
  color: "",
  size: "",
  description: "",
};

export default function ItemManager({ items, categories, api }) {
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState("");

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  }

  function validateForm() {
    if (!form.name.trim()) return "Item name is required.";
    if (!form.category.trim()) return "Category is required.";
    if (!form.color.trim()) return "Color is required.";
    if (!form.size.trim()) return "Size is required.";
    return "";
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setMessage("");

      const error = validateForm();

      if (error) {
        setMessage(error);
        return;
      }

      if (editingId) {
        await api.updateItem(editingId, form);
        setMessage("Item updated.");
      } else {
        await api.addItem(form);
        setMessage("Item added.");
      }

      setForm(emptyForm);
      setEditingId(null);
    } catch (error) {
      setMessage(error.message);
    }
  }

  function startEdit(item) {
    setEditingId(item.id);

    setForm({
      name: item.name || "",
      category: item.category || "",
      color: item.color || "",
      size: item.size || "",
      description: item.description || item.notes || "",
    });

    setMessage("");
  }

  function cancelEdit() {
    setEditingId(null);
    setForm(emptyForm);
    setMessage("");
  }

  async function handleDelete(id) {
    try {
      const confirmed = window.confirm("Delete this item?");

      if (!confirmed) return;

      await api.deleteItem(id);
      setMessage("Item deleted.");
    } catch (error) {
      setMessage(error.message);
    }
  }

  return (
    <section className="m1-page">
      <div className="m1-header">
        <p className="section-eyebrow">Manage</p>
        <h1>Closet Item Management</h1>
        <p>Add, edit, view, and delete closet items.</p>
      </div>

      <form className="m1-card m1-form" onSubmit={handleSubmit}>
        <input
          className="m1-input"
          name="name"
          placeholder="Item name"
          value={form.name}
          onChange={handleChange}
        />

        <select
          className="m1-input"
          name="category"
          value={form.category}
          onChange={handleChange}
        >
          <option value="">Select category</option>

          {categories.map((category) => (
            <option key={category.id} value={category.name}>
              {category.name}
            </option>
          ))}
        </select>

        <input
          className="m1-input"
          name="color"
          placeholder="Color"
          value={form.color}
          onChange={handleChange}
        />

        <input
          className="m1-input"
          name="size"
          placeholder="Size"
          value={form.size}
          onChange={handleChange}
        />

        <textarea
          className="m1-input"
          name="description"
          placeholder="Description"
          value={form.description}
          onChange={handleChange}
        />

        <div className="m1-actions">
  <button className="btn btn-primary" type="submit">
    {editingId ? "Update Item" : "Add Item"}
  </button>

  {editingId && (
    <button
      className="btn btn-secondary"
      type="button"
      onClick={resetForm}
    >
      Cancel
    </button>
  )}
</div>
      </form>

      {message && <div className="m1-message">{message}</div>}

      <div className="m1-grid">
        {items.length === 0 && <p className="m1-empty">No items added yet.</p>}

        {items.map((item) => (
          <div className="m1-card m1-item-card" key={item.id}>
            <div>
              <h3>{item.name}</h3>
              <p>
                {item.category} · {item.color} · {item.size}
              </p>

              {item.description && <p>{item.description}</p>}
            </div>

            <div className="m1-actions">
              <button
                className="btn btn-secondary btn-sm"
                type="button"
                onClick={() => startEdit(item)}
              >
                Edit
              </button>

              <button
                className="btn btn-danger btn-sm"
                type="button"
                onClick={() => handleDelete(item.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}