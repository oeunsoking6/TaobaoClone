require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');

const app = express();
const PORT = process.env.PORT || 8082;

app.use(express.json());

// --- Mongoose Schema ---
const ProductSchema = new mongoose.Schema({
  id: Number,
  name: String,
  price: Number,
  seller: String,
});
const Product = mongoose.model('Product', ProductSchema, 'products'); 

// --- API Endpoint ---
app.get('/recommendations/:productId', async (req, res) => {
  try {
    const productId = parseInt(req.params.productId);
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

// --- Database Connection (THE FIX) ---
console.log("Connecting to MongoDB...");
mongoose.connect(process.env.DATABASE_URL);
const connection = mongoose.connection;
connection.on('error', (err) => {
  console.error('Error connecting to MongoDB', err);
});
connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
  // Start listening for requests
  app.listen(PORT, () => {
    console.log(`Recommendation service listening on port ${PORT}`);
  });
});