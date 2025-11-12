require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const autoIncrement = require('mongoose-auto-increment'); // We'll initialize this differently

const app = express();
const PORT = process.env.PORT || 8081;

app.use(express.json());

// --- Database Connection (THE FIX) ---
// 1. Use mongoose.connect() to establish the default connection
mongoose.connect(process.env.DATABASE_URL);
const connection = mongoose.connection; // 2. Get the default connection

// 3. Initialize auto-increment on the default connection
autoIncrement.initialize(connection); 

connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
  seedDatabase();
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

// 5. Use the default connection to create the model
const Product = mongoose.model('Product', ProductSchema); 

// --- API Endpoints (No changes from here down) ---

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

// --- Database Seeding Function (No changes) ---
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