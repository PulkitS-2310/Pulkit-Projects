import logo from "../assets/logo.png";

const Sidebar = ({ categories, active, setActive, query, setQuery }) => {
  return (
    <div className="w-[240px] min-w-[240px] bg-[#0f172a] text-white flex flex-col items-center p-6 min-h-screen">
      <h1 className="text-3xl font-bold mb-4 text-center">Product Catalog</h1>

      <img src={logo} className="w-32 mb-6 object-contain" alt="logo" />

      <p className="text-xs uppercase tracking-widest text-gray-400 mb-3">
        Categories
      </p>

      <div className="flex flex-wrap justify-center gap-2 mb-6">
        {categories?.map((cat) => (
          <button
            key={cat}
            onClick={() => setActive(cat)}
            className={`px-4 py-1.5 rounded-full text-sm font-medium transition ${
              active === cat
                ? "bg-amber-500 text-black"
                : "bg-slate-700 text-gray-200 hover:bg-slate-600"
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      <input
        type="search"
        placeholder="Search products..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        className="w-full p-2.5 rounded-lg bg-slate-700 text-white text-sm placeholder-gray-400 outline-none"
      />
    </div>
  );
};

export default Sidebar;