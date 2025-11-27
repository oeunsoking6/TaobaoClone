require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 8085; // New port for Order Service

app.use(express.json());
app.use(cors());

// --- Database Connection ---
console.log("Connecting to MongoDB...");
mongoose.connect(process.env.DATABASE_URL)
  .then(() => {
    console.log('Successfully connected to MongoDB');
    app.listen(PORT, () => console.log(`Order service listening on port ${PORT}`));
  })
  .catch(err => console.error('Error connecting to MongoDB', err));

// --- Mongoose Schema ---
const OrderItemSchema = new mongoose.Schema({
    productId: Number,
    name: String,
    price: Number,
    quantity: Number,
    image: String
});

const OrderSchema = new mongoose.Schema({
    userId: { type: String, required: true }, // Link to User
    items: [OrderItemSchema],                 // Snapshot of what they bought
    totalAmount: Number,
    shippingAddress: String,
    phone: String,
    status: { type: String, default: "Pending" }, // Pending, Shipped, Delivered
    createdAt: { type: Date, default: Date.now }
});

const Order = mongoose.model('Order', OrderSchema);

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

// 1. Create a New Order
app.post('/orders', authenticateToken, async (req, res) => {
    try {
        const { items, totalAmount, shippingAddress, phone } = req.body;

        if (!items || items.length === 0) {
            return res.status(400).json({ message: "Cannot place an empty order" });
        }

        const newOrder = new Order({
            userId: req.userId,
            items: items,
            totalAmount: totalAmount,
            shippingAddress: shippingAddress || "Default Address",
            phone: phone || ""
        });

        const savedOrder = await newOrder.save();
        res.status(201).json({ message: "Order placed successfully!", orderId: savedOrder._id });

    } catch (err) {
        console.error(err);
        res.status(500).json({ message: "Error placing order" });
    }
});

// 2. Get My Orders
app.get('/orders', authenticateToken, async (req, res) => {
    try {
        // Find all orders for this user, sort by newest first
        const orders = await Order.find({ userId: req.userId }).sort({ createdAt: -1 });
        res.json(orders);
    } catch (err) {
        console.error(err);
        res.status(500).json({ message: "Error fetching orders" });
    }
});