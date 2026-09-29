import { useEffect, useMemo, useState } from "react";
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
} from "react-router-dom";

import HomePage from "./pages/HomePage";
import OutfitBuilderPage from "./pages/OutfitBuilderPage";
import OutfitDetailPage from "./pages/OutfitDetailPage";
import PlannerPage from "./pages/PlannerPage";
import FavoritesPage from "./pages/FavoritesPage";

import MyClosetPage from "./components/MyClosetPage";
import ItemManager from "./components/ItemManager";
import CategoryManager from "./components/CategoryManager";
import AdminDashboard from "./components/AdminDashboard";
import LoginPage from "./components/LoginPage";
import LogoutPage from "./components/LogoutPage";
import SignupPage from "./components/SignupPage";
import FAQPage from "./components/FAQPage";

import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import "./index.css";

const API_URL = "http://localhost:5000/api";

function ClosetWrapper({ component: Component, items, categories, api, status }) {
  return (
    <div className="page-layout">
      <Navbar />

      <main
        style={{
          paddingTop: 72,
          paddingBottom: 60,
          minHeight: "100vh",
          background: "var(--cream)",
        }}
      >
        <div className="page-container" style={{ paddingTop: 40 }}>
          {status.loading && (
            <div
              style={{
                textAlign: "center",
                padding: 60,
                color: "var(--ink-muted)",
              }}
            >
              <div style={{ fontSize: 32, marginBottom: 12 }}>⏳</div>
              <p>Loading closet data…</p>
            </div>
          )}

          {status.error && (
            <div
              style={{
                marginBottom: 16,
                padding: 14,
                borderRadius: 12,
                background: "#fff0f0",
                color: "#8a1f1f",
                border: "1px solid #efb3b3",
              }}
            >
              {status.error}
            </div>
          )}

          {!status.loading && (
            <Component items={items} categories={categories} api={api} />
          )}
        </div>
      </main>

      <Footer />
    </div>
  );
}

function NotFound() {
  return (
    <div className="page-layout">
      <Navbar />

      <main
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
          gap: 16,
          fontFamily: "var(--font-body)",
          background: "var(--cream)",
        }}
      >
        <span style={{ fontSize: 64 }}>🧭</span>

        <h1
          style={{
            fontFamily: "var(--font-display)",
            fontSize: 32,
            color: "var(--ink)",
          }}
        >
          Page Not Found
        </h1>

        <a href="/" style={{ color: "var(--cta)", fontWeight: 600 }}>
          ← Back to Home
        </a>
      </main>

      <Footer />
    </div>
  );
}

function ProtectedRoute({ user, children }) {
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }

  return children;
}

function loadUser() {
  try {
    return JSON.parse(localStorage.getItem("outfitly_user")) || null;
  } catch {
    return null;
  }
}

function saveAuth(user, token) {
  if (user && token) {
    localStorage.setItem("outfitly_user", JSON.stringify(user));
    localStorage.setItem("outfitly_token", token);
  } else {
    localStorage.removeItem("outfitly_user");
    localStorage.removeItem("outfitly_token");
  }
}

export default function App() {
  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [status, setStatus] = useState({ loading: true, error: "" });
  const [user, setUser] = useState(loadUser);

  async function loadCloset() {
    try {
      setStatus({ loading: true, error: "" });

      const res = await fetch(`${API_URL}/closet`);
      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.error || "Unable to load closet data.");
      }

      setItems(data.items || []);
      setCategories(data.categories || []);
      setStatus({ loading: false, error: "" });
    } catch (error) {
      console.error("Closet request failed:", error);

      setStatus({
        loading: false,
        error: "Could not load closet data. Make sure backend is running.",
      });
    }
  }

  useEffect(() => {
    loadCloset();
  }, []);

  async function handleLogin({ email, password }) {
    try {
      const res = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email,
          password,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        return {
          ok: false,
          message: data.error || "Login failed.",
        };
      }

      setUser(data.user);
      saveAuth(data.user, data.token);

      return {
        ok: true,
        message: "Logged in successfully.",
      };
    } catch (error) {
      console.error("Login request failed:", error);

      return {
        ok: false,
        message: "Could not reach backend.",
      };
    }
  }

  async function handleSignup({ name, email, password }) {
    try {
      const res = await fetch(`${API_URL}/auth/signup`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: name || email.split("@")[0],
          email,
          password,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        return {
          ok: false,
          message: data.error || "Signup failed.",
        };
      }

      return {
        ok: true,
        message: "Account created. Please log in.",
      };
    } catch (error) {
      console.error("Signup request failed:", error);

      return {
        ok: false,
        message: "Could not reach backend.",
      };
    }
  }

  function handleLogout() {
    setUser(null);
    saveAuth(null, null);
  }

  const api = useMemo(
    () => ({
      async refresh() {
        await loadCloset();
      },

      async addCategory(name) {
        const res = await fetch(`${API_URL}/categories`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ name }),
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.error || "Unable to add category.");
        }

        setCategories((current) => [...current, data]);
      },

      async updateCategory(id, name) {
        const res = await fetch(`${API_URL}/categories/${id}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ name }),
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.error || "Unable to update category.");
        }

        const oldCategory = categories.find((category) => category.id === id);

        setCategories((current) =>
          current.map((category) => (category.id === id ? data : category))
        );

        if (oldCategory) {
          setItems((current) =>
            current.map((item) =>
              item.category === oldCategory.name
                ? { ...item, category: data.name }
                : item
            )
          );
        }
      },

      async deleteCategory(id) {
        const res = await fetch(`${API_URL}/categories/${id}`, {
          method: "DELETE",
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.error || "Unable to delete category.");
        }

        const deletedCategory = categories.find(
          (category) => category.id === id
        );

        setCategories((current) =>
          current.filter((category) => category.id !== id)
        );

        if (deletedCategory) {
          setItems((current) =>
            current.filter((item) => item.category !== deletedCategory.name)
          );
        }
      },

      async addItem(item) {
        const res = await fetch(`${API_URL}/items`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(item),
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.error || "Unable to add item.");
        }

        setItems((current) => [data, ...current]);
      },

      async updateItem(id, item) {
        const res = await fetch(`${API_URL}/items/${id}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(item),
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.error || "Unable to update item.");
        }

        setItems((current) =>
          current.map((currentItem) =>
            currentItem.id === id ? data : currentItem
          )
        );
      },

      async deleteItem(id) {
        const res = await fetch(`${API_URL}/items/${id}`, {
          method: "DELETE",
        });

        const data = await res.json();

        if (!res.ok) {
          throw new Error(data.error || "Unable to delete item.");
        }

        setItems((current) => current.filter((item) => item.id !== id));
      },
    }),
    [categories]
  );

  const closetProps = {
    items,
    categories,
    api,
    status,
  };

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={<LoginPage onLogin={handleLogin} user={user} />}
        />

        <Route
          path="/signup"
          element={<SignupPage onSignup={handleSignup} user={user} />}
        />

        <Route path="/faq" element={<FAQPage />} />

        <Route
          path="/"
          element={
            <ProtectedRoute user={user}>
              <HomePage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/builder"
          element={
            <ProtectedRoute user={user}>
              <OutfitBuilderPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/outfit/:id"
          element={
            <ProtectedRoute user={user}>
              <OutfitDetailPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/planner"
          element={
            <ProtectedRoute user={user}>
              <PlannerPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/favorites"
          element={
            <ProtectedRoute user={user}>
              <FavoritesPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/closet"
          element={
            <ProtectedRoute user={user}>
              <ClosetWrapper {...closetProps} component={MyClosetPage} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/items"
          element={
            <ProtectedRoute user={user}>
              <ClosetWrapper {...closetProps} component={ItemManager} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/categories"
          element={
            <ProtectedRoute user={user}>
              <ClosetWrapper {...closetProps} component={CategoryManager} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin"
          element={
            <ProtectedRoute user={user}>
              <ClosetWrapper {...closetProps} component={AdminDashboard} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute user={user}>
              <ClosetWrapper {...closetProps} component={AdminDashboard} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/logout"
          element={
            <ProtectedRoute user={user}>
              <LogoutPage onLogout={handleLogout} user={user} />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}