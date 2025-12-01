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
  // Store the role (ADMIN or USER)
  role: {
    type: String,
    default: 'USER', 
    enum: ['USER', 'ADMIN']
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

    // --- UPDATED LOGIC HERE ---
    // Now, any email containing the word "admin" becomes an ADMIN.
    // Example: "admin2@gmail.com", "superadmin@test.com" -> ADMIN
    const assignedRole = email.includes('admin') ? 'ADMIN' : 'USER';

    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);

    const newUser = new User({
      email: email,
      password_hash: passwordHash,
      role: assignedRole // Save the role to the database
    });

    const savedUser = await newUser.save();
    
    res.status(201).json({ 
        message: 'User registered successfully!', 
        user: { 
            id: savedUser._id, 
            email: savedUser.email,
            role: savedUser.role 
        } 
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

    // Prepare the payload
    const payload = { 
        user: { 
            id: user._id,
            role: user.role 
        } 
    };

    const token = jwt.sign(
      payload,
      process.env.JWT_SECRET,
      { expiresIn: '1h' }
    );

    // Send role to Android app
    res.json({ 
        message: 'Logged in successfully!', 
        token,
        userId: user._id,
        role: user.role || 'USER' 
    });

  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

// --- Database Connection ---
console.log("Connecting to MongoDB...");
mongoose.connect(process.env.DATABASE_URL)
  .then(() => {
    console.log('Successfully connected to MongoDB');
    app.listen(PORT, () => console.log(`User Service is listening on port ${PORT}`));
  })
  .catch(err => console.error('Error connecting to MongoDB', err));