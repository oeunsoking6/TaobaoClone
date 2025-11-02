const hre = require("hardhat");

async function main() {
  const productTracker = await hre.ethers.deployContract("ProductTracker");

  await productTracker.waitForDeployment();

  console.log(
    `ProductTracker deployed to ${productTracker.target}`
  );
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});