require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const autoIncrement = require('mongoose-auto-increment');

const app = express();
const PORT = process.env.PORT || 8082;

app.use(express.json());

// --- Database Connection ---
const connection = mongoose.createConnection(process.env.DATABASE_URL);
autoIncrement.initialize(connection);

connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
});
connection.on('error', (err) => {
  console.error('Error connecting to MongoDB', err);
});

// --- Mongoose Schema (Must match product-service) ---
const ProductSchema = new mongoose.Schema({
  name: String,
  price: Number,
  seller: String,
});
ProductSchema.plugin(autoIncrement.plugin, {
  model: 'Product',
  field: 'id',
  startAt: 1,
  incrementBy: 1,
});
const Product = connection.model('Product', ProductSchema);

// --- API Endpoint for Recommendations ---
app.get('/recommendations/:productId', async (req, res) => {
  try {
    // Convert productId from a string to a number
    const productId = parseInt(req.params.productId);

    // This is the Mongoose query for "get 3 random products
    // where the id is not the current product's id"
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