const mongoose = require("mongoose");

const plannerEntrySchema = new mongoose.Schema(
  {
    date: {
      type: String,
      required: true,
    },

    outfitId: {
      type: String,
      default: "",
    },

    outfitName: {
      type: String,
      required: true,
      trim: true,
    },

    notes: {
      type: String,
      default: "",
    },
  },
  { timestamps: true }
);

module.exports =
  mongoose.models.PlannerEntry ||
  mongoose.model("PlannerEntry", plannerEntrySchema);