require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');

const app = express();
const PORT = process.env.PORT || 8082;

app.use(express.json());

// --- Database Connection ---
const connection = mongoose.createConnection(process.env.DATABASE_URL);

connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
});
connection.on('error', (err) => {
  console.error('Error connecting to MongoDB', err);
});

// --- Mongoose Schema (Simple read-only version) ---
// We just define the structure so Mongoose can read the data
const ProductSchema = new mongoose.Schema({
  id: Number,
  name: String,
  price: Number,
  seller: String,
});

// We tell Mongoose to use the *existing* "products" collection
const Product = connection.model('Product', ProductSchema, 'products');

// --- API Endpoint for Recommendations ---
app.get('/recommendations/:productId', async (req, res) => {
  try {
    const productId = parseInt(req.params.productId);

    // Mongoose query for "get 3 random products"
    const recommendations = await Product.aggregate([
      { $match: { id: { $ne: productId } } },
      { $sample: { size: 3 } }
    ]);

    res.json(recommendations);
  } catch (err) {
    console.error(err.message);
    res.status(500).send("Server Error");
  }
});

app.listen(PORT, () => {
  console.log(`Recommendation service listening on port ${PORT}`);
});