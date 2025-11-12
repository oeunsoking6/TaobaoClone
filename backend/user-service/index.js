require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 8080;

app.use(express.json());

// --- Mongoose Schema ---
const UserSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
  },
  password_hash: {
    type: String,
    required: true,
  },
  created_at: {
    type: Date,
    default: Date.now,
  },
});
const User = mongoose.model('User', UserSchema);

// --- API Endpoints ---
app.post('/register', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ message: 'Email and password are required.' });
    const existingUser = await User.findOne({ email: email });
    if (existingUser) {
      return res.status(409).json({ message: 'Email already in use.' });
    }
    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);
    const newUser = new User({
      email: email,
      password_hash: passwordHash,
    });
    const savedUser = await newUser.save();
    res.status(201).json({ 
        message: 'User registered successfully!', 
        user: { id: savedUser._id, email: savedUser.email } 
    });
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});
app.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) return res.status(400).json({ message: 'Email and password are required.' });
    const user = await User.findOne({ email: email });
    if (!user) {
      return res.status(401).json({ message: 'Invalid credentials.' });
    }
    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      return res.status(401).json({ message: 'Invalid credentials.' });
    }
    const payload = { user: { id: user._id } };
    const token = jwt.sign(
      payload,
      process.env.JWT_SECRET,
      { expiresIn: '1h' }
    );
    res.json({ message: 'Logged in successfully!', token });
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

// --- Database Connection (THE FIX) ---
// We only start the server *after* the database is connected.
console.log("Connecting to MongoDB...");
mongoose.connect(process.env.DATABASE_URL)
  .then(() => {
    console.log('Successfully connected to MongoDB');
    // Start listening for requests
    app.listen(PORT, () => console.log(`User Service is listening on port ${PORT}`));
  })
  .catch(err => console.error('Error connecting to MongoDB', err));