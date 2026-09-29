const express = require("express");
const Category = require("../models/Category");
const Item = require("../models/Item");

const router = express.Router();

function escapeRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function formatCategory(category) {
  return {
    id: category._id.toString(),
    name: category.name,
    createdAt: category.createdAt,
    updatedAt: category.updatedAt,
  };
}

router.get("/", async (req, res) => {
  try {
    const categories = await Category.find().sort({ name: 1 });
    res.json(categories.map(formatCategory));
  } catch (error) {
    console.error("GET /api/categories error:", error);
    res.status(500).json({ error: "Unable to fetch categories." });
  }
});

router.get("/:id", async (req, res) => {
  try {
    const category = await Category.findById(req.params.id);

    if (!category) {
      return res.status(404).json({ error: "Category not found." });
    }

    res.json(formatCategory(category));
  } catch (error) {
    res.status(400).json({ error: "Invalid category id." });
  }
});

router.post("/", async (req, res) => {
  try {
    const name = String(req.body.name || "").trim();

    if (!name) {
      return res.status(400).json({ error: "Category name is required." });
    }

    const duplicate = await Category.findOne({
      name: new RegExp(`^${escapeRegex(name)}$`, "i"),
    });

    if (duplicate) {
      return res.status(400).json({ error: "Category already exists." });
    }

    const category = await Category.create({ name });
    res.status(201).json(formatCategory(category));
  } catch (error) {
    console.error("POST /api/categories error:", error);
    res.status(500).json({ error: "Unable to create category." });
  }
});

router.put("/:id", async (req, res) => {
  try {
    const name = String(req.body.name || "").trim();

    if (!name) {
      return res.status(400).json({ error: "Category name is required." });
    }

    const category = await Category.findById(req.params.id);

    if (!category) {
      return res.status(404).json({ error: "Category not found." });
    }

    const duplicate = await Category.findOne({
      _id: { $ne: category._id },
      name: new RegExp(`^${escapeRegex(name)}$`, "i"),
    });

    if (duplicate) {
      return res.status(400).json({ error: "Category already exists." });
    }

    const oldName = category.name;
    category.name = name;
    await category.save();

    await Item.updateMany(
      { category: oldName },
      { $set: { category: name } }
    );

    res.json(formatCategory(category));
  } catch (error) {
    console.error("PUT /api/categories/:id error:", error);
    res.status(400).json({ error: "Unable to update category." });
  }
});

router.delete("/:id", async (req, res) => {
  try {
    const category = await Category.findByIdAndDelete(req.params.id);

    if (!category) {
      return res.status(404).json({ error: "Category not found." });
    }

    await Item.deleteMany({ category: category.name });

    res.json(formatCategory(category));
  } catch (error) {
    console.error("DELETE /api/categories/:id error:", error);
    res.status(400).json({ error: "Unable to delete category." });
  }
});

module.exports = router;