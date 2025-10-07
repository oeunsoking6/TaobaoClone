// index.js for product-service
const express = require('express');
const { Pool } = require('pg');

const app = express();
const PORT = 8081;

app.use(express.json());

// Database Connection
const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'postgres',
  password: 'mysecretpassword',
  port: 5432,
});

// Endpoint to get ALL products
app.get('/products', async (req, res) => {
  try {
    const allProducts = await pool.query("SELECT * FROM products");
    res.json(allProducts.rows);
  } catch (err) {
    console.error(err.message);
    res.status(500).send("Server Error");
  }
});

// --- NEW ENDPOINT ---
// Endpoint to get a SINGLE product by its ID
app.get('/products/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const product = await pool.query("SELECT * FROM products WHERE id = $1", [id]);

        if (product.rows.length === 0) {
            return res.status(404).json({ msg: 'Product not found' });
        }

        res.json(product.rows[0]);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server Error');
    }
});


app.listen(PORT, () => {
  console.log(`Product service listening on http://localhost:${PORT}`);
});