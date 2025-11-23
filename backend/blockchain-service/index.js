const express = require('express');
const { ethers } = require('ethers');
const contractABI = require('./abi.json');

const app = express();
const PORT = process.env.PORT || 8084;

app.use(express.json());

// --- Blockchain Connection ---
const provider = new ethers.JsonRpcProvider("http://127.0.0.1:7545");
const contractAddress = "0xe2ac0181bfBa27A4185f161B65e2B3a3024B69b0"; 
const GANACHE_PRIVATE_KEY = "0xdfbdb3a7c1b171523e361922717cf02e9032b3add23736f0825f99802ac1e9e7";

const contract = new ethers.Contract(contractAddress, contractABI, provider);
const signer = new ethers.Wallet(GANACHE_PRIVATE_KEY, provider);
const contractWithSigner = contract.connect(signer);


// --- API Endpoints ---
app.get('/history/:productId', async (req, res) => {
    // --- NEW LOGGING ---
    console.log(`GET /history/${req.params.productId} - Request received.`);
    
    try {
        const productId = parseInt(req.params.productId);
        const history = await contract.getHistory(productId);
        
        const formattedHistory = history.map(event => ({
            timestamp: Number(event.timestamp),
            description: event.description
        }));
        
        console.log(`GET /history/${productId} - Found ${formattedHistory.length} events.`);
        res.json(formattedHistory);
    } catch (error) {
        console.error(`GET /history/${req.params.productId} - ERROR:`, error);
        res.status(500).send("Error fetching history from blockchain.");
    }
});

app.post('/history', async (req, res) => {
    try {
        const { productId, description } = req.body;
        if (!productId || !description) {
            return res.status(400).json({ message: 'productId and description are required.' });
        }
        const tx = await contractWithSigner.addHistory(productId, description, {
            gasPrice: ethers.parseUnits("10", "gwei"),
            gasLimit: 300000
        });
        await tx.wait(); 
        res.status(201).json({ message: `History added successfully for product ${productId}` });
    } catch (error) {
        console.error(error); 
        res.status(500).send("Error adding history to blockchain.");
    }
});

app.listen(PORT, () => {
  console.log(`Blockchain service listening on port ${PORT}`);
});