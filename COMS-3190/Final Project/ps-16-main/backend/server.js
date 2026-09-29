require("dotenv").config();

const express = require("express");
const cors = require("cors");
const connectDB = require("./config/database");

const closetRoutes = require("./api/closetRoutes");
const itemRoutes = require("./api/itemRoutes");
const categoryRoutes = require("./api/categoryRoutes");
const outfitRoutes = require("./api/outfitRoutes");
const plannerRoutes = require("./api/plannerRoutes");
const authRoutes = require("./api/authRoutes");
const adminRoutes = require("./api/adminRoutes");

const app = express();

const PORT = process.env.PORT || 5000;
const CLIENT_ORIGIN = process.env.CLIENT_ORIGIN || "http://localhost:5173";

app.use(
  cors({
    origin: [CLIENT_ORIGIN, "http://localhost:5173", "http://127.0.0.1:5173"],
    credentials: true,
  })
);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.get("/", (req, res) => {
  res.json({
    message: "Outfitly backend is running with MongoDB.",
  });
});

app.get("/api/health", (req, res) => {
  res.json({
    status: "ok",
    message: "Backend API is working.",
  });
});

app.use("/api/closet", closetRoutes);
app.use("/api/items", itemRoutes);
app.use("/api/categories", categoryRoutes);
app.use("/api/outfits", outfitRoutes);
app.use("/api/planner", plannerRoutes);
app.use("/api/auth", authRoutes);
app.use("/api/admin", adminRoutes);

app.use((req, res) => {
  res.status(404).json({
    error: "Route not found.",
  });
});

async function startServer() {
  await connectDB();

  app.listen(PORT, () => {
    console.log(`Backend running on http://localhost:${PORT}`);
  });
}

startServer();