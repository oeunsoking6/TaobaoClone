package com.group4.taobaoclon

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String? = null // Added image URL property
)