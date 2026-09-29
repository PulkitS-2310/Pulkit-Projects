const mongoose = require("mongoose");

const outfitSchema = new mongoose.Schema(
  {
    name: {
      type: String,
      required: true,
      trim: true,
    },

    occasion: {
      type: String,
      default: "",
    },

    season: {
      type: String,
      default: "",
    },

    notes: {
      type: String,
      default: "",
    },

    items: {
      type: [mongoose.Schema.Types.Mixed],
      default: [],
    },

    favorite: {
      type: Boolean,
      default: false,
    },

    plannedDate: {
      type: String,
      default: "",
    },
  },
  { timestamps: true }
);

module.exports =
  mongoose.models.Outfit || mongoose.model("Outfit", outfitSchema);