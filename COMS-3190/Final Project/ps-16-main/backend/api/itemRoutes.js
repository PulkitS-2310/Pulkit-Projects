const express = require("express");
const Item = require("../models/Item");

const router = express.Router();

function formatItem(item) {
  return {
    id: item._id.toString(),
    name: item.name,
    category: item.category,
    color: item.color,
    size: item.size,
    season: item.season || "",
    occasion: item.occasion || "",
    image: item.image || "",
    description: item.description || "",
    notes: item.notes || "",
    createdAt: item.createdAt,
    updatedAt: item.updatedAt,
  };
}

router.get("/", async (req, res) => {
  try {
    const filter = {};

    if (req.query.category) {
      filter.category = req.query.category;
    }

    const items = await Item.find(filter).sort({ createdAt: -1 });
    res.json(items.map(formatItem));
  } catch (error) {
    console.error("GET /api/items error:", error);
    res.status(500).json({ error: "Unable to fetch items." });
  }
});

router.get("/:id", async (req, res) => {
  try {
    const item = await Item.findById(req.params.id);

    if (!item) {
      return res.status(404).json({ error: "Item not found." });
    }

    res.json(formatItem(item));
  } catch (error) {
    res.status(400).json({ error: "Invalid item id." });
  }
});

router.post("/", async (req, res) => {
  try {
    const {
      name,
      category,
      color,
      size,
      season,
      occasion,
      image,
      description,
      notes,
    } = req.body;

    if (!name || !category || !color || !size) {
      return res.status(400).json({
        error: "Name, category, color, and size are required.",
      });
    }

    const item = await Item.create({
      name: String(name).trim(),
      category: String(category).trim(),
      color: String(color).trim(),
      size: String(size).trim(),
      season: season || "",
      occasion: occasion || "",
      image: image || "",
      description: description || notes || "",
      notes: notes || description || "",
    });

    res.status(201).json(formatItem(item));
  } catch (error) {
    console.error("POST /api/items error:", error);
    res.status(500).json({ error: "Unable to create item." });
  }
});

router.put("/:id", async (req, res) => {
  try {
    const item = await Item.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });

    if (!item) {
      return res.status(404).json({ error: "Item not found." });
    }

    res.json(formatItem(item));
  } catch (error) {
    console.error("PUT /api/items/:id error:", error);
    res.status(400).json({ error: "Unable to update item." });
  }
});

router.delete("/:id", async (req, res) => {
  try {
    const item = await Item.findByIdAndDelete(req.params.id);

    if (!item) {
      return res.status(404).json({ error: "Item not found." });
    }

    res.json(formatItem(item));
  } catch (error) {
    console.error("DELETE /api/items/:id error:", error);
    res.status(400).json({ error: "Unable to delete item." });
  }
});

module.exports = router;