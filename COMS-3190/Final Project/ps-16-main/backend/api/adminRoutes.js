const express = require("express");
const Item = require("../models/Item");
const Category = require("../models/Category");
const User = require("../models/User");
const Outfit = require("../models/Outfit");
const PlannerEntry = require("../models/PlannerEntry");

const router = express.Router();

router.get("/stats", async (req, res) => {
  try {
    const [
      totalItems,
      totalCategories,
      totalUsers,
      totalOutfits,
      totalPlannerEntries,
      itemsByCategory,
    ] = await Promise.all([
      Item.countDocuments(),
      Category.countDocuments(),
      User.countDocuments(),
      Outfit.countDocuments(),
      PlannerEntry.countDocuments(),
      Item.aggregate([
        {
          $group: {
            _id: "$category",
            count: { $sum: 1 },
          },
        },
        {
          $project: {
            _id: 0,
            category: "$_id",
            count: 1,
          },
        },
        {
          $sort: {
            category: 1,
          },
        },
      ]),
    ]);

    res.json({
      totalItems,
      totalCategories,
      totalUsers,
      totalOutfits,
      totalPlannerEntries,
      itemsByCategory,
    });
  } catch (error) {
    console.error("GET /api/admin/stats error:", error);
    res.status(500).json({ error: "Unable to fetch admin stats." });
  }
});

module.exports = router;