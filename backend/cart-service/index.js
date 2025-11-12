require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 8083;

app.use(express.json());

// --- Database Connection ---
mongoose.connect(process.env.DATABASE_URL)
  .then(() => console.log('Successfully connected to MongoDB'))
  .catch(err => console.error('Error connecting to MongoDB', err));

// --- Mongoose Schemas ---
// This schema stores the individual items
const CartItemSchema = new mongoose.Schema({
  productId: { // This is the numeric ID from the Product model
    type: Number,
    required: true,
  },
  quantity: {
    type: Number,
    required: true,
    default: 1,
  },
  // We'll also store product details here for easy access
  name: String,
  price: Number,
});

// This schema represents a user's single shopping cart
const CartSchema = new mongoose.Schema({
  userId: { // This is the MongoDB _id string (e.g., "60c7...f")
    type: String,
    required: true,
    unique: true,
  },
  items: [CartItemSchema], // An array of items
  updated_at: {
    type: Date,
    default: Date.now,
  },
});

const Cart = mongoose.model('Cart', CartSchema);

// --- Authentication Middleware (No changes) ---
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (token == null) return res.sendStatus(401);

  jwt.verify(token, process.env.JWT_SECRET, (err, payload) => {
    if (err) return res.sendStatus(403);
    req.userId = payload.user.id; // This is the MongoDB _id string
    next();
  });
};

// --- API Endpoints ---

// GET /cart - Get all items in the user's cart
app.get('/cart', authenticateToken, async (req, res) => {
  try {
    const cart = await Cart.findOne({ userId: req.userId });
    if (!cart) {
      return res.json([]); // No cart, return empty array
    }
    res.json(cart.items);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server Error');
  }
});

// POST /cart - Add an item to the user's cart
app.post('/cart', authenticateToken, async (req, res) => {
  const { productId, quantity } = req.body;
  if (!productId || !quantity) {
    return res.status(400).json({ message: 'Product ID and quantity are required.' });
  }

  try {
    // Find the user's cart, or create a new one if it doesn't exist
    let cart = await Cart.findOne({ userId: req.userId });
    if (!cart) {
      cart = new Cart({ userId: req.userId, items: [] });
    }

    // Check if the item already exists in the cart
    const existingItem = cart.items.find(item => item.productId === productId);

    if (existingItem) {
      // Update quantity
      existingItem.quantity += quantity;
    } else {
      // Add new item to the cart
      // In a real app, you'd fetch the name/price from the product-service first
      cart.items.push({
        productId: productId,
        quantity: quantity,
        name: "Product " + productId, // Placeholder name
        price: 0.00, // Placeholder price
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

app.listen(PORT, () => {
  console.log(`Cart service listening on port ${PORT}`);
});