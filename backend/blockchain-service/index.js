const express = require('express');
const { ethers } = require('ethers');
const contractABI = require('./abi.json');

const app = express();
const PORT = process.env.PORT || 8084;

app.use(express.json());

// --- Blockchain Connection ---
const provider = new ethers.JsonRpcProvider("http://127.0.0.1:7545");
const contractAddress = "0xD02179D16B1f0A95342c227B8282d38A46B1D911";
const GANACHE_PRIVATE_KEY = "0x0282a5aa15f76544284e32eba65d83ce30b013efcee1881f35ac87c69d8832cc"; // Your key
// ADD THESE LOGS TO DEBUG:
console.log("------------------------------------------------");
console.log("DEBUG: Connecting to Provider -> http://127.0.0.1:7545"); 
console.log("DEBUG: Using Contract Address ->", contractAddress);
console.log("------------------------------------------------");

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

// The '0.0.0.0' argument is critical. It allows connections from the emulator.
app.listen(8084, '0.0.0.0', () => {
    console.log('Blockchain service running on port 8084 (Accessible externally)');
});