package com.group4.taobaoclon

data class Order(
    val _id: String, // MongoDB ID
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String,
    val createdAt: String
)