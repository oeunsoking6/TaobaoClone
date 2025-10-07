require('dotenv').config();
const express = require('express');
const { Pool } = require('pg');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 8080; // Render provides the PORT variable

app.use(express.json());

// --- Database Connection ---
// Use the DATABASE_URL from environment variables for cloud deployment
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: process.env.DATABASE_URL ? { rejectUnauthorized: false } : false
});

pool.query('SELECT NOW()', (err) => {
  if (err) console.error('Error connecting to the database', err.stack);
  else console.log('Successfully connected to the database');
});

// --- API Endpoints ---

// User Registration Endpoint
app.post('/register', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ message: 'Email and password are required.' });

    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);

    const newUser = await pool.query(
      "INSERT INTO users (email, password_hash) VALUES ($1, $2) RETURNING id, email",
      [email, passwordHash]
    );

    res.status(201).json({ message: 'User registered successfully!', user: newUser.rows[0] });
  } catch (err) {
    if (err.code === '23505') return res.status(409).json({ message: 'Email already in use.' });
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

// User Login Endpoint
app.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ message: 'Email and password are required.' });

    // 1. Find user in database
    const userResult = await pool.query("SELECT * FROM users WHERE email = $1", [email]);
    if (userResult.rows.length === 0) {
      return res.status(401).json({ message: 'Invalid credentials.' });
    }
    const user = userResult.rows[0];

    // 2. Compare submitted password with stored hash
    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      return res.status(401).json({ message: 'Invalid credentials.' });
    }

    // 3. Create and sign a JWT
    const payload = { user: { id: user.id } };
    const token = jwt.sign(
      payload,
      process.env.JWT_SECRET, // Get secret from .env file
      { expiresIn: '1h' } // Token expires in 1 hour
    );

    // 4. Send token back to client
    res.json({ message: 'Logged in successfully!', token });

  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

app.listen(PORT, () => console.log(`User Service is listening on port ${PORT}`));