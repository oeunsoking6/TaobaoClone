const express = require('express');
const { Pool } = require('pg');

const app = express();
const PORT = process.env.PORT || 8082;

app.use(express.json());

// Use the DATABASE_URL from environment variables
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

app.get('/recommendations/:productId', async (req, res) => {
  try {
    const { productId } = req.params;

    const recommendationResult = await pool.query(
      "SELECT * FROM products WHERE id != $1 ORDER BY RANDOM() LIMIT 3",
      [productId]
    );

    res.json(recommendationResult.rows);

  } catch (err) {
    console.error(err.message);
    res.status(500).send("Server Error");
  }
});

app.listen(PORT, () => {
  console.log(`Recommendation service listening on port ${PORT}`);
});