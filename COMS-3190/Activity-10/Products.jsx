import { useState } from "react";

const Products = ({ products = [] }) => {
  const [selected, setSelected] = useState(null);

  return (
    <div className="flex-1 p-8 bg-[#f8f8f8]">
      <h2 className="text-2xl font-bold mb-6 text-gray-900">
        Products ({products.length})
      </h2>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {products.map((p) => (
          <div
            key={p._id}
            className="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition cursor-pointer"
            onClick={() => setSelected(p)}
          >
            <div className="relative h-72 overflow-hidden group">
              <img
                src={p.image}
                alt={p.title}
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition flex items-center justify-center">
                <button
                  className="bg-white text-black px-4 py-2 rounded text-sm font-medium"
                  onClick={(e) => {
                    e.stopPropagation();
                    setSelected(p);
                  }}
                >
                  View
                </button>
              </div>
            </div>

            <div className="p-4">
              <h3 className="text-sm font-semibold text-gray-900 mb-1">
                {p.title}
              </h3>
              <p className="text-green-600 font-bold mb-1">${p.price}</p>
              <span className="inline-block px-2 py-0.5 text-xs rounded-full bg-amber-500 text-amber-900 mb-1">
                {p.category}
              </span>
              <div className="text-yellow-400 text-sm">
                {"★".repeat(Math.round(p.rating))}
                {"☆".repeat(5 - Math.round(p.rating))}
              </div>
            </div>
          </div>
        ))}
      </div>

      {products.length === 0 && (
        <p className="text-gray-400 text-center mt-16">No products found.</p>
      )}

      {selected && (
        <div
          className="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
          onClick={() => setSelected(null)}
        >
          <div
            className="bg-white rounded-xl w-[420px] max-w-[90vw] overflow-hidden relative"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              className="absolute top-3 right-3 text-gray-500 hover:text-black bg-white rounded-full w-7 h-7 flex items-center justify-center text-sm"
              onClick={() => setSelected(null)}
            >
              ✕
            </button>
            <img
              src={selected.image}
              alt={selected.title}
              className="w-full h-60 object-cover"
            />
            <div className="p-5">
              <h2 className="font-bold text-lg mb-1">{selected.title}</h2>
              <p className="text-sm text-gray-500 mb-3">{selected.description}</p>
              <div className="flex items-center gap-3">
                <span className="text-green-600 font-bold text-lg">
                  ${selected.price}
                </span>
                <span className="text-gray-400 text-sm line-through">
                  ${selected.oldPrice}
                </span>
              </div>
              <div className="text-yellow-400 mt-1">
                {"★".repeat(Math.round(selected.rating))}
                {"☆".repeat(5 - Math.round(selected.rating))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Products;