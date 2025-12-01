require("@nomicfoundation/hardhat-toolbox"); 
// (or whatever plugins you have at the top)

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: "0.8.24", // (Your version might be different, keep yours)
  networks: {
    // ADD or EDIT this section:
    localhost: {
      url: "http://127.0.0.1:7545", // <--- CHANGE THIS PORT TO 7545
      chainId: 1337, // Standard Ganache chainId
    },
  },
};