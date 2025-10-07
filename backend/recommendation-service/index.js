// index.js for recommendation-service
const express = require('express');
const { Pool } = require('pg');

const app = express();
// Use a new port for this service
const PORT = 8082;

app.use(express.json());

// --- Database Connection ---
// This service also needs to read the products table
const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'postgres',
  password: 'mysecretpassword',
  port: 5432,
});

// --- API Endpoint for Recommendations ---

app.get('/recommendations/:productId', async (req, res) => {
  try {
    const { productId } = req.params;

    // This is our simple "AI" logic for now:
    // Fetch all products from the database EXCEPT the current one.
    // Order them randomly and return the first 3.
    const recommendationResult = await pool.query(
      "SELECT * FROM products WHERE id != $1 ORDER BY RANDOM() LIMIT 3",
      [productId]
    );

    res.json(recommendationResult.rows);

 } catch (err) {
    console.error(err); // <-- IT SHOULD LOOK LIKE THIS
    res.status(500).send("Server Error");
  }
});

app.listen(PORT, () => {
  console.log(`Recommendation service listening on http://localhost:${PORT}`);
});