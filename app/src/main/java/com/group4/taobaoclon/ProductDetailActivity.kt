package com.group4.taobaoclon

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {
    private var currentProductId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val nameTextView = findViewById<TextView>(R.id.detailProductNameTextView)
        val priceTextView = findViewById<TextView>(R.id.detailProductPriceTextView)
        val addToCartButton = findViewById<Button>(R.id.addToCartButton)

        currentProductId = intent.getIntExtra("PRODUCT_ID", -1)

        if (currentProductId != -1) {
            lifecycleScope.launch {
                try {
                    val product = ApiClient.productApiService.getProductById(currentProductId)
                    nameTextView.text = product.name
                    priceTextView.text = "$${product.price}"
                } catch (e: Exception) {
                    Log.e("ProductDetailActivity", "Error fetching product details: ${e.message}")
                    nameTextView.text = "Error loading product"
                }
            }
        } else {
            nameTextView.text = "Product ID not found"
        }

        // --- NEW CODE: ADD TO CART BUTTON LOGIC ---
        addToCartButton.setOnClickListener {
            // Read the saved token
            val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("USER_TOKEN", null)

            if (token == null) {
                Toast.makeText(this, "You must be logged in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Make the API call
            lifecycleScope.launch {
                try {
                    val request = AddToCartRequest(productId = currentProductId, quantity = 1)
                    val response = ApiClient.cartApiService.addToCart("Bearer $token", request)

                    if (response.isSuccessful) {
                        Toast.makeText(this@ProductDetailActivity, "Added to cart!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProductDetailActivity, "Failed to add to cart.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ProductDetailActivity", "Error adding to cart: ${e.message}")
                    Toast.makeText(this@ProductDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}