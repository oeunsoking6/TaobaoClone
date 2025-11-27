package com.group4.taobaoclon

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String? = null,
    // --- NEW FIELDS ---
    val variant: String? = "Default",
    val storeName: String? = "Official Store",
    val coupons: List<String>? = emptyList(),
    val shippingLabel: String? = "Free Shipping"
)