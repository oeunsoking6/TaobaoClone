// index.js for user-service
const express = require('express');
const app = express();
const PORT = 8080; // Port for the user service

app.use(express.json());

// A simple test route to make sure the server is working
app.get('/', (req, res) => {
  res.send('User Service is running!');
});

app.listen(PORT, () => {
  console.log(`User Service is listening on http://localhost:${PORT}`);
});