package com.group4.taobaoclon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Get data passed from ProductDetailActivity
        val productName = intent.getStringExtra("PRODUCT_NAME") ?: "Unknown Product"
        val productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0)
        val productImage = intent.getStringExtra("PRODUCT_IMAGE")

        // Initialize Views
        val nameView = findViewById<TextView>(R.id.checkoutProductName)
        val priceView = findViewById<TextView>(R.id.checkoutProductPrice)
        val totalPriceView = findViewById<TextView>(R.id.totalPriceText)
        val imageView = findViewById<ImageView>(R.id.checkoutProductImage)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val placeOrderButton = findViewById<Button>(R.id.placeOrderButton)

        // Set Data
        nameView.text = productName
        priceView.text = "$$productPrice"
        totalPriceView.text = "$$productPrice"

        Glide.with(this)
            .load(productImage)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(imageView)

        backButton.setOnClickListener { finish() }

        // --- PLACE ORDER LOGIC ---
        placeOrderButton.setOnClickListener {
            val sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val token = sharedPrefs.getString("USER_TOKEN", null)

            if (token == null) {
                Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the order object
            // Note: Since we are "Buying Now", we create a list with just this one item.
            // We use a dummy ID '0' because we don't have the real ID passed here yet,
            // but in a real app you would pass the ID too.
            val orderItem = OrderItem(
                productId = 0,
                name = productName,
                price = productPrice,
                quantity = 1,
                image = productImage
            )

            val orderRequest = OrderRequest(
                items = listOf(orderItem),
                totalAmount = productPrice,
                shippingAddress = "123 Russian Blvd, Phnom Penh",
                phone = "+855 12 345 678"
            )

            lifecycleScope.launch {
                try {
                    val response = ApiClient.orderApiService.createOrder("Bearer $token", orderRequest)

                    if (response.isSuccessful) {
                        Toast.makeText(this@CheckoutActivity, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                        finish() // Close screen
                    } else {
                        Toast.makeText(this@CheckoutActivity, "Failed: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("CheckoutActivity", "Error: ${e.message}")
                    Toast.makeText(this@CheckoutActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}