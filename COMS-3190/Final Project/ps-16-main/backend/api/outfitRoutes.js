const express = require("express");
const Outfit = require("../models/Outfit");

const router = express.Router();

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
    createdAt: outfit.createdAt,
    updatedAt: outfit.updatedAt,
  };
}

router.get("/", async (req, res) => {
  try {
    const filter = {};

    if (req.query.favorite === "true") {
      filter.favorite = true;
    }

    const outfits = await Outfit.find(filter).sort({ createdAt: -1 });
    res.json(outfits.map(formatOutfit));
  } catch (error) {
    console.error("GET /api/outfits error:", error);
    res.status(500).json({ error: "Unable to fetch outfits." });
  }
});

router.get("/:id", async (req, res) => {
  try {
    const outfit = await Outfit.findById(req.params.id);

    if (!outfit) {
      return res.status(404).json({ error: "Outfit not found." });
    }

    res.json(formatOutfit(outfit));
  } catch (error) {
    res.status(400).json({ error: "Invalid outfit id." });
  }
});

router.post("/", async (req, res) => {
  try {
    const { name, occasion, season, notes, items, favorite, plannedDate } =
      req.body;

    if (!name || !items || items.length === 0) {
      return res.status(400).json({
        error: "Outfit name and at least one item are required.",
      });
    }

    const outfit = await Outfit.create({
      name: String(name).trim(),
      occasion: occasion || "",
      season: season || "",
      notes: notes || "",
      items,
      favorite: favorite !== undefined ? Boolean(favorite) : true,
      plannedDate: plannedDate || "",
    });

    res.status(201).json(formatOutfit(outfit));
  } catch (error) {
    console.error("POST /api/outfits error:", error);
    res.status(500).json({ error: "Unable to create outfit." });
  }
});

router.put("/:id", async (req, res) => {
  try {
    const outfit = await Outfit.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });

    if (!outfit) {
      return res.status(404).json({ error: "Outfit not found." });
    }

    res.json(formatOutfit(outfit));
  } catch (error) {
    console.error("PUT /api/outfits/:id error:", error);
    res.status(400).json({ error: "Unable to update outfit." });
  }
});

router.delete("/:id", async (req, res) => {
  try {
    const outfit = await Outfit.findByIdAndDelete(req.params.id);

    if (!outfit) {
      return res.status(404).json({ error: "Outfit not found." });
    }

    res.json(formatOutfit(outfit));
  } catch (error) {
    console.error("DELETE /api/outfits/:id error:", error);
    res.status(400).json({ error: "Unable to delete outfit." });
  }
});

module.exports = router;