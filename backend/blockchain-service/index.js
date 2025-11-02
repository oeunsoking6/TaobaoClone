const express = require('express');
const { ethers } = require('ethers');
const contractABI = require('./abi.json'); // Import the ABI

const app = express();
const PORT = process.env.PORT || 8084;

app.use(express.json());

// --- Blockchain Connection ---
const provider = new ethers.JsonRpcProvider("http://127.0.0.1:7545"); // Ganache RPC URL
const contractAddress = "0x0765feA53164b9B10203e354a6F95b254A17538C"; // <-- PASTE YOUR DEPLOYED CONTRACT ADDRESS
const contract = new ethers.Contract(contractAddress, contractABI, provider);

// --- API Endpoints ---

// GET /history/:productId - Get the history of a product
app.get('/history/:productId', async (req, res) => {
    try {
        const { productId } = req.params;
        const history = await contract.getHistory(productId);
        
        // The data from the contract needs to be formatted
        const formattedHistory = history.map(event => ({
            timestamp: Number(event.timestamp),
            description: event.description
        }));
        
        res.json(formattedHistory);
    } catch (error) {
        console.error(error);
        res.status(500).send("Error fetching history from blockchain.");
    }
});

// POST /history - Add a new event to a product's history
app.post('/history', async (req, res) => {
    try {
        const { productId, description } = req.body;
        if (!productId || !description) {
            return res.status(400).json({ message: 'productId and description are required.' });
        }

        // We need a "signer" to send a transaction that changes data
        const signer = await provider.getSigner(0); // Use the first account from Ganache
        const contractWithSigner = contract.connect(signer);
        
        const tx = await contractWithSigner.addHistory(productId, description);
        await tx.wait(); // Wait for the transaction to be mined

        res.status(201).json({ message: `History added successfully for product ${productId}` });
    } catch (error) {
        console.error(error);
        res.status(500).send("Error adding history to blockchain.");
    }
});


app.listen(PORT, () => {
  console.log(`Blockchain service listening on port ${PORT}`);
});