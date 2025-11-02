// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.24;

contract ProductTracker {
    struct HistoryEvent {
        uint timestamp;
        string description;
    }

    mapping(uint => HistoryEvent[]) public productHistories;

    event HistoryAdded(uint indexed productId, uint timestamp, string description);

    function addHistory(uint _productId, string memory _description) public {
        productHistories[_productId].push(HistoryEvent({
            timestamp: block.timestamp,
            description: _description
        }));
        emit HistoryAdded(_productId, block.timestamp, _description);
    }

    function getHistory(uint _productId) public view returns (HistoryEvent[] memory) {
        return productHistories[_productId];
    }
}