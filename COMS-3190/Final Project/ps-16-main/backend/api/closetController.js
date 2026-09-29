const { readData, writeData } = require("../config/database");

function nextId(records) {
  if (records.length === 0) {
    return 1;
  }

  return Math.max(...records.map((record) => Number(record.id))) + 1;
}

function cleanText(value) {
  return String(value || "").trim();
}

function sendJson(res, statusCode, payload) {
  res.writeHead(statusCode, { "Content-Type": "application/json" });
  res.end(JSON.stringify(payload));
}

function sendError(res, statusCode, message) {
  sendJson(res, statusCode, { error: message });
}

async function getCloset(req, res) {
  const data = await readData();
  sendJson(res, 200, data);
}

async function createCategory(req, res, body) {
  const name = cleanText(body.name);

  if (!name) {
    sendError(res, 400, "Category name is required.");
    return;
  }

  const data = await readData();
  const duplicate = data.categories.some(
    (category) => category.name.toLowerCase() === name.toLowerCase(),
  );

  if (duplicate) {
    sendError(res, 409, "That category already exists.");
    return;
  }

  const category = { id: nextId(data.categories), name };
  data.categories.push(category);
  await writeData(data);
  sendJson(res, 201, category);
}

async function updateCategory(req, res, id, body) {
  const name = cleanText(body.name);

  if (!name) {
    sendError(res, 400, "Category name is required.");
    return;
  }

  const data = await readData();
  const category = data.categories.find((entry) => entry.id === id);

  if (!category) {
    sendError(res, 404, "Category not found.");
    return;
  }

  const duplicate = data.categories.some(
    (entry) => entry.id !== id && entry.name.toLowerCase() === name.toLowerCase(),
  );

  if (duplicate) {
    sendError(res, 409, "That category already exists.");
    return;
  }

  const previousName = category.name;
  category.name = name;
  data.items = data.items.map((item) =>
    item.category === previousName ? { ...item, category: name } : item,
  );

  await writeData(data);
  sendJson(res, 200, category);
}

async function deleteCategory(req, res, id) {
  const data = await readData();
  const category = data.categories.find((entry) => entry.id === id);

  if (!category) {
    sendError(res, 404, "Category not found.");
    return;
  }

  data.categories = data.categories.filter((entry) => entry.id !== id);
  data.items = data.items.filter((item) => item.category !== category.name);
  await writeData(data);
  sendJson(res, 200, { deletedId: id });
}

async function createItem(req, res, body) {
  const item = {
    name: cleanText(body.name),
    category: cleanText(body.category),
    color: cleanText(body.color),
    size: cleanText(body.size),
    description: cleanText(body.description),
  };

  if (!item.name || !item.category || !item.color || !item.size) {
    sendError(res, 400, "Name, category, color, and size are required.");
    return;
  }

  const data = await readData();
  const categoryExists = data.categories.some(
    (category) => category.name === item.category,
  );

  if (!categoryExists) {
    sendError(res, 400, "Choose an existing category.");
    return;
  }

  const newItem = { id: nextId(data.items), ...item };
  data.items.push(newItem);
  await writeData(data);
  sendJson(res, 201, newItem);
}

async function updateItem(req, res, id, body) {
  const updates = {
    name: cleanText(body.name),
    category: cleanText(body.category),
    color: cleanText(body.color),
    size: cleanText(body.size),
    description: cleanText(body.description),
  };

  if (!updates.name || !updates.category || !updates.color || !updates.size) {
    sendError(res, 400, "Name, category, color, and size are required.");
    return;
  }

  const data = await readData();
  const itemIndex = data.items.findIndex((item) => item.id === id);

  if (itemIndex === -1) {
    sendError(res, 404, "Item not found.");
    return;
  }

  const categoryExists = data.categories.some(
    (category) => category.name === updates.category,
  );

  if (!categoryExists) {
    sendError(res, 400, "Choose an existing category.");
    return;
  }

  data.items[itemIndex] = { id, ...updates };
  await writeData(data);
  sendJson(res, 200, data.items[itemIndex]);
}

async function deleteItem(req, res, id) {
  const data = await readData();
  const itemExists = data.items.some((item) => item.id === id);

  if (!itemExists) {
    sendError(res, 404, "Item not found.");
    return;
  }

  data.items = data.items.filter((item) => item.id !== id);
  await writeData(data);
  sendJson(res, 200, { deletedId: id });
}

module.exports = {
  getCloset,
  createCategory,
  updateCategory,
  deleteCategory,
  createItem,
  updateItem,
  deleteItem,
  sendError,
};
