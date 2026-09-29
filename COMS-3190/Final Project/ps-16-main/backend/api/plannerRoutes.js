const express = require("express");
const PlannerEntry = require("../models/PlannerEntry");

const router = express.Router();

function formatEntry(entry) {
  return {
    id: entry._id.toString(),
    date: entry.date,
    outfitId: entry.outfitId || "",
    outfitName: entry.outfitName,
    notes: entry.notes || "",
    createdAt: entry.createdAt,
    updatedAt: entry.updatedAt,
  };
}

router.get("/", async (req, res) => {
  try {
    const filter = {};

    if (req.query.date) {
      filter.date = req.query.date;
    }

    const entries = await PlannerEntry.find(filter).sort({
      date: 1,
      createdAt: 1,
    });

    res.json(entries.map(formatEntry));
  } catch (error) {
    console.error("GET /api/planner error:", error);
    res.status(500).json({ error: "Unable to fetch planner entries." });
  }
});

router.get("/:id", async (req, res) => {
  try {
    const entry = await PlannerEntry.findById(req.params.id);

    if (!entry) {
      return res.status(404).json({ error: "Planner entry not found." });
    }

    res.json(formatEntry(entry));
  } catch (error) {
    res.status(400).json({ error: "Invalid planner entry id." });
  }
});

router.post("/", async (req, res) => {
  try {
    const { date, outfitId, outfitName, notes } = req.body;

    if (!date || !outfitName) {
      return res.status(400).json({
        error: "Date and outfit name are required.",
      });
    }

    const entry = await PlannerEntry.create({
      date,
      outfitId: outfitId || "",
      outfitName,
      notes: notes || "",
    });

    res.status(201).json(formatEntry(entry));
  } catch (error) {
    console.error("POST /api/planner error:", error);
    res.status(500).json({ error: "Unable to create planner entry." });
  }
});

router.put("/:id", async (req, res) => {
  try {
    const entry = await PlannerEntry.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });

    if (!entry) {
      return res.status(404).json({ error: "Planner entry not found." });
    }

    res.json(formatEntry(entry));
  } catch (error) {
    console.error("PUT /api/planner/:id error:", error);
    res.status(400).json({ error: "Unable to update planner entry." });
  }
});

router.delete("/:id", async (req, res) => {
  try {
    const entry = await PlannerEntry.findByIdAndDelete(req.params.id);

    if (!entry) {
      return res.status(404).json({ error: "Planner entry not found." });
    }

    res.json(formatEntry(entry));
  } catch (error) {
    console.error("DELETE /api/planner/:id error:", error);
    res.status(400).json({ error: "Unable to delete planner entry." });
  }
});

module.exports = router;