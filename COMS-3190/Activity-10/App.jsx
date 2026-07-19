import { useState } from "react";
import Sidebar from "./components/Sidebar";
import Products from "./components/Products";
import { Products as AllProducts } from "./data/fashion";
import { Categories } from "./data/categories";

const App = () => {
  const [active, setActive] = useState("all");
  const [query, setQuery] = useState("");

  const filteredProducts = AllProducts.filter((p) => {
    const matchesCategory = active === "all" || p.category === active;
    const matchesSearch = p.title.toLowerCase().includes(query.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="flex min-h-screen">
      <Sidebar
        categories={Categories}
        active={active}
        setActive={setActive}
        query={query}
        setQuery={setQuery}
      />
      <Products products={filteredProducts} />
    </div>
  );
};

export default App;