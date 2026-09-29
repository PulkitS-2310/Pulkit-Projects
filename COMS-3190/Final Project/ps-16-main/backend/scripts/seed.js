require("dotenv").config();

const mongoose = require("mongoose");
const bcrypt = require("bcryptjs");

const Category = require("../models/Category");
const Item = require("../models/Item");
const Outfit = require("../models/Outfit");
const User = require("../models/User");
const PlannerEntry = require("../models/PlannerEntry");

const categories = [
  "Tops",
  "Bottoms",
  "Shoes",
  "Accessories",
  "Outerwear",
  "Dresses",
];

const items = [
  {
    name: "White T-Shirt",
    category: "Tops",
    color: "White",
    size: "M",
    season: "Summer",
    occasion: "Casual",
    description: "Simple everyday t-shirt.",
  },
  {
    name: "Black Blazer",
    category: "Tops",
    color: "Black",
    size: "M",
    season: "All Season",
    occasion: "Work",
    description: "Dressy blazer for formal looks.",
  },
  {
    name: "Striped Sweater",
    category: "Tops",
    color: "Blue",
    size: "L",
    season: "Winter",
    occasion: "Casual",
    description: "Warm striped sweater.",
  },
  {
    name: "Denim Jeans",
    category: "Bottoms",
    color: "Blue",
    size: "32",
    season: "All Season",
    occasion: "Casual",
    description: "Everyday slim-fit jeans.",
  },
  {
    name: "Flowy Skirt",
    category: "Bottoms",
    color: "Pink",
    size: "S",
    season: "Spring",
    occasion: "Smart Casual",
    description: "Light skirt for spring outfits.",
  },
  {
    name: "White Sneakers",
    category: "Shoes",
    color: "White",
    size: "10",
    season: "All Season",
    occasion: "Casual",
    description: "Clean daily sneakers.",
  },
  {
    name: "Ankle Boots",
    category: "Shoes",
    color: "Brown",
    size: "9",
    season: "Fall",
    occasion: "Smart Casual",
    description: "Boots for fall outfits.",
  },
  {
    name: "Gold Necklace",
    category: "Accessories",
    color: "Gold",
    size: "One Size",
    season: "All Season",
    occasion: "Formal",
    description: "Simple gold accessory.",
  },
  {
    name: "Canvas Tote",
    category: "Accessories",
    color: "Beige",
    size: "One Size",
    season: "All Season",
    occasion: "Casual",
    description: "Everyday tote bag.",
  },
  {
    name: "Denim Jacket",
    category: "Outerwear",
    color: "Blue",
    size: "M",
    season: "Spring",
    occasion: "Casual",
    description: "Light jacket for cool weather.",
  },
  {
    name: "Floral Dress",
    category: "Dresses",
    color: "Blue",
    size: "S",
    season: "Spring",
    occasion: "Date Night",
    description: "Floral spring dress.",
  },
];

async function seed() {
  try {
    if (!process.env.MONGO_URI) {
      throw new Error("MONGO_URI is missing from backend/.env");
    }

    await mongoose.connect(process.env.MONGO_URI);

    await Category.deleteMany({});
    await Item.deleteMany({});
    await Outfit.deleteMany({});
    await User.deleteMany({});
    await PlannerEntry.deleteMany({});

    await Category.insertMany(categories.map((name) => ({ name })));

    const createdItems = await Item.insertMany(items);

    const casualItems = createdItems.filter((item) =>
      ["White T-Shirt", "Denim Jeans", "White Sneakers"].includes(item.name)
    );

    const formalItems = createdItems.filter((item) =>
      ["Black Blazer", "Gold Necklace", "Ankle Boots"].includes(item.name)
    );

    const outfits = await Outfit.insertMany([
      {
        name: "Coffee Run Look",
        occasion: "Casual",
        season: "All Season",
        notes: "Easy daily outfit.",
        favorite: true,
        items: casualItems.map((item) => ({
          id: item._id.toString(),
          name: item.name,
          category: item.category,
          color: item.color,
          size: item.size,
        })),
      },
      {
        name: "Smart Casual Evening",
        occasion: "Smart Casual",
        season: "Fall",
        notes: "Good for dinner or evening events.",
        favorite: true,
        items: formalItems.map((item) => ({
          id: item._id.toString(),
          name: item.name,
          category: item.category,
          color: item.color,
          size: item.size,
        })),
      },
    ]);

    const passwordHash = await bcrypt.hash("password123", 10);

    await User.insertMany([
      {
        name: "Demo User",
        email: "demo@example.com",
        passwordHash,
        role: "user",
      },
      {
        name: "Admin User",
        email: "admin@example.com",
        passwordHash,
        role: "admin",
      },
    ]);

    const today = new Date().toISOString().slice(0, 10);

    await PlannerEntry.create({
      date: today,
      outfitId: outfits[0]._id.toString(),
      outfitName: outfits[0].name,
      notes: "Demo planner entry.",
    });

    console.log("Database seeded successfully.");
    console.log("Demo login: demo@example.com / password123");
    console.log("Admin login: admin@example.com / password123");

    process.exit(0);
  } catch (error) {
    console.error("Seed failed:", error.message);
    process.exit(1);
  }
}

seed();