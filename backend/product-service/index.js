require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const autoIncrement = require('mongoose-auto-increment');

const app = express();
const PORT = process.env.PORT || 8081;

app.use(express.json());

// --- Database Connection ---
const connection = mongoose.createConnection(process.env.DATABASE_URL);
autoIncrement.initialize(connection); // Initialize auto-increment

connection.once('open', () => {
  console.log('Successfully connected to MongoDB');
  // Seed the database after connecting
  seedDatabase();
});
connection.on('error', (err) => {
  console.error('Error connecting to MongoDB', err);
});

// --- Mongoose Schema ---
const ProductSchema = new mongoose.Schema({
  name: String,
  price: Number,
  seller: String,
});

// Add the auto-increment plugin to the schema
// This will create a numeric 'id' field
ProductSchema.plugin(autoIncrement.plugin, {
  model: 'Product',
  field: 'id', // This is the field our Android app expects
  startAt: 1,
  incrementBy: 1,
});

const Product = connection.model('Product', ProductSchema);

// --- API Endpoints ---

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
    const product = await Product.findOne({ id: id }); // Find by our numeric 'id'

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

// --- Database Seeding Function ---
// This will add your sample products to the empty database
async function seedDatabase() {
  try {
    const count = await Product.countDocuments();
    if (count === 0) {
      console.log('No products found. Seeding database...');
      const sampleProducts = [
        { name: 'Cloud Smartphone X', price: 799.99, seller: 'ElectroCorp' },
        { name: 'Cloud Wireless Earbuds', price: 129.99, seller: 'AudioPhile' },
        { name: 'Cloud Smartwatch Series 5', price: 249.99, seller: 'TechGear' },
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