require('dotenv').config(); // <-- Added
const express = require('express');
const mongoose = require('mongoose'); // <-- Only mongoose

const app = express();
const PORT = process.env.PORT || 8082;

app.use(express.json());

// --- Database Connection (Fixed) ---
// 1. Use mongoose.connect()
mongoose.connect(process.env.DATABASE_URL);
const connection = mongoose.connection; // 2. Get the default connection

connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
});
connection.on('error', (err) => {
  console.error('Error connecting to MongoDB', err);
});

// --- Mongoose Schema (Fixed) ---
const ProductSchema = new mongoose.Schema({
  id: Number,
  name: String,
  price: Number,
  seller: String,
});

// 5. Use the default mongoose connection and tell it to use the "products" collection
const Product = mongoose.model('Product', ProductSchema, 'products'); 

// --- API Endpoint for Recommendations (No changes) ---
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

app.listen(PORT, () => {
  console.log(`Recommendation service listening on port ${PORT}`);
});