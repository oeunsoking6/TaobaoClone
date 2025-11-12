require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const autoIncrement = require('mongoose-auto-increment'); // We'll initialize this differently

const app = express();
const PORT = process.env.PORT || 8082;

app.use(express.json());

// --- Database Connection (THE FIX) ---
// 1. Use mongoose.connect()
mongoose.connect(process.env.DATABASE_URL);
const connection = mongoose.connection; // 2. Get the default connection

// 3. Initialize auto-increment on the default connection
autoIncrement.initialize(connection); 

connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
});
connection.on('error', (err) => {
  console.error('Error connecting to MongoDB', err);
});

// --- Mongoose Schema ---
const ProductSchema = new mongoose.Schema({
  id: { type: Number, unique: true },
  name: String,
  price: Number,
  seller: String,
});

// 4. Attach the plugin to the schema
ProductSchema.plugin(autoIncrement.plugin, {
  model: 'Product',
  field: 'id',
  startAt: 1,
  incrementBy: 1,
});

// 5. Use the default mongoose connection
const Product = mongoose.model('Product', ProductSchema); 

// --- API Endpoint for Recommendations (No changes from here down) ---
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