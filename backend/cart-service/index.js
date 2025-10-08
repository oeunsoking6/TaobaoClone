require('dotenv').config();
const express = require('express');
const { Pool } = require('pg');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 8083;

app.use(express.json());

// --- Database Connection ---
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

// --- Authentication Middleware ---
// This function will run before our cart endpoints to protect them
const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

    if (token == null) return res.sendStatus(401); // if there isn't any token

    jwt.verify(token, process.env.JWT_SECRET, (err, payload) => {
        if (err) return res.sendStatus(403); // if the token is invalid
        req.userId = payload.user.id; // Add the user ID to the request object
        next(); // Move on to the next function
    });
};

// --- API Endpoints ---

// GET /cart - Get all items in the user's cart
app.get('/cart', authenticateToken, async (req, res) => {
    try {
        const cartItems = await pool.query(
            `SELECT p.id, p.name, p.price, ci.quantity 
             FROM cart_items ci
             JOIN products p ON ci.product_id = p.id
             JOIN carts c ON ci.cart_id = c.id
             WHERE c.user_id = $1`,
            [req.userId]
        );
        res.json(cartItems.rows);
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
        // Find or create a cart for the user
        let cartResult = await pool.query("SELECT id FROM carts WHERE user_id = $1", [req.userId]);
        let cartId;
        if (cartResult.rows.length === 0) {
            const newCart = await pool.query("INSERT INTO carts (user_id) VALUES ($1) RETURNING id", [req.userId]);
            cartId = newCart.rows[0].id;
        } else {
            cartId = cartResult.rows[0].id;
        }

        // Check if item already exists in the cart
        const existingItem = await pool.query(
            "SELECT * FROM cart_items WHERE cart_id = $1 AND product_id = $2",
            [cartId, productId]
        );

        if (existingItem.rows.length > 0) {
            // Update quantity if item exists
            await pool.query(
                "UPDATE cart_items SET quantity = quantity + $1 WHERE cart_id = $2 AND product_id = $3",
                [quantity, cartId, productId]
            );
        } else {
            // Insert new item if it doesn't exist
            await pool.query(
                "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES ($1, $2, $3)",
                [cartId, productId, quantity]
            );
        }
        
        res.status(200).json({ message: 'Item added to cart' });
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server Error');
    }
});


app.listen(PORT, () => {
  console.log(`Cart service listening on port ${PORT}`);
});