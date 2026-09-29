const express = require("express");
const Category = require("../models/Category");
const Item = require("../models/Item");
const Outfit = require("../models/Outfit");

const router = express.Router();

function formatCategory(category) {
  return {
    id: category._id.toString(),
    name: category.name,
  };
}

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
  };
}

function formatOutfit(outfit) {
  return {
    id: outfit._id.toString(),
    name: outfit.name,
    occasion: outfit.occasion || "",
    season: outfit.season || "",
    notes: outfit.notes || "",
    items: outfit.items || [],
    favorite: Boolean(outfit.favorite),
    plannedDate: outfit.plannedDate || "",
  };
}

router.get("/", async (req, res) => {
  try {
    const [categories, items, outfits] = await Promise.all([
      Category.find().sort({ name: 1 }),
      Item.find().sort({ createdAt: -1 }),
      Outfit.find().sort({ createdAt: -1 }),
    ]);

    res.json({
      categories: categories.map(formatCategory),
      items: items.map(formatItem),
      outfits: outfits.map(formatOutfit),
    });
  } catch (error) {
    console.error("GET /api/closet error:", error);
    res.status(500).json({ error: "Unable to fetch closet data." });
  }
});

module.exports = router;