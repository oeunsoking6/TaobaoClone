require('dotenv').config(); // <-- Added
const express = require('express');
const mongoose = require('mongoose'); // <-- Only mongoose

const app = express();
const PORT = process.env.PORT || 8081;

app.use(express.json());

// --- Database Connection (Fixed) ---
// 1. Use mongoose.connect() to establish the default connection
mongoose.connect(process.env.DATABASE_URL);
const connection = mongoose.connection; // 2. Get the default connection

connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
  seedDatabase();
});
connection.on('error', (err) => {
  console.error('Error connecting to MongoDB', err);
});

// --- Mongoose Schema (Fixed) ---
const ProductSchema = new mongoose.Schema({
  id: { type: Number, unique: true }, // The product ID our app uses
  name: String,
  price: Number,
  seller: String,
});

// 5. Use the default connection to create the model
const Product = mongoose.model('Product', ProductSchema); 

// --- API Endpoints (No changes) ---

// Endpoint to get ALL products
app.get('/products', async (req, res) => {
  try {
    const allProducts = await Product.find();
    res.json(allProducts);
  } catch (err) {
    console.error(err.message);
    res.status(500).send("Server Error");
  }
});

// Endpoint to get a SINGLE product by its ID
app.get('/products/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const product = await Product.findOne({ id: id }); 

    if (!product) {
      return res.status(404).json({ msg: 'Product not found' });
    }
    res.json(product);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server Error');
  }
});

app.listen(PORT, () => {
  console.log(`Product service listening on port ${PORT}`);
});

// --- Database Seeding Function (Fixed) ---
// This will add your sample products with manual IDs
async function seedDatabase() {
  try {
    const count = await Product.countDocuments();
    if (count === 0) {
      console.log('No products found. Seeding database...');
      const sampleProducts = [
        { id: 1, name: 'Cloud Smartphone X', price: 799.99, seller: 'ElectroCorp' },
        { id: 2, name: 'Cloud Wireless Earbuds', price: 129.99, seller: 'AudioPhile' },
        { id: 3, name: 'Cloud Smartwatch Series 5', price: 249.99, seller: 'TechGear' },
      ];
      await Product.create(sampleProducts);
      console.log('Database seeded successfully.');
    } else {
      console.log('Database already contains data. No seeding necessary.');
    }
  } catch (err) {
    console.error('Error seeding database:', err.message);
  }
}