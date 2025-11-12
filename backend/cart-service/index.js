require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 8083;

app.use(express.json());

// --- Mongoose Schemas ---
const CartItemSchema = new mongoose.Schema({
  productId: { type: Number, required: true },
  quantity: { type: Number, required: true, default: 1 },
  name: String,
  price: Number,
});
const CartSchema = new mongoose.Schema({
  userId: { type: String, required: true, unique: true },
  items: [CartItemSchema],
  updated_at: { type: Date, default: Date.now },
});
const Cart = mongoose.model('Cart', CartSchema);

// --- Authentication Middleware ---
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (token == null) return res.sendStatus(401);
  jwt.verify(token, process.env.JWT_SECRET, (err, payload) => {
    if (err) return res.sendStatus(403);
    req.userId = payload.user.id;
    next();
  });
};

// --- API Endpoints ---
app.get('/cart', authenticateToken, async (req, res) => {
  try {
    const cart = await Cart.findOne({ userId: req.userId });
    if (!cart) {
      return res.json([]); 
    }
    res.json(cart.items);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server Error');
  }
});
app.post('/cart', authenticateToken, async (req, res) => {
  const { productId, quantity } = req.body;
  if (!productId || !quantity) {
    return res.status(400).json({ message: 'Product ID and quantity are required.' });
  }
  try {
    let cart = await Cart.findOne({ userId: req.userId });
    if (!cart) {
      cart = new Cart({ userId: req.userId, items: [] });
    }
    const existingItem = cart.items.find(item => item.productId === productId);
    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      cart.items.push({
        productId: productId,
        quantity: quantity,
        name: "Product " + productId, 
        price: 0.00,
      });
    }
    cart.updated_at = Date.now();
    await cart.save();
    res.status(200).json({ message: 'Item added to cart' });
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server Error');
  }
});

// --- Database Connection (THE FIX) ---
console.log("Connecting to MongoDB...");
mongoose.connect(process.env.DATABASE_URL)
  .then(() => {
    console.log('Successfully connected to MongoDB');
    // Start listening for requests
    app.listen(PORT, () => console.log(`Cart service listening on port ${PORT}`));
  })
  .catch(err => console.error('Error connecting to MongoDB', err));