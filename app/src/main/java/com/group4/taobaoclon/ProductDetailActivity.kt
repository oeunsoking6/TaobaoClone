package com.group4.taobaoclon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Get the product ID that was passed from MainActivity
        val productId = intent.getIntExtra("PRODUCT_ID", -1)

        // Find the TextViews from our layout
        val nameTextView = findViewById<TextView>(R.id.detailProductNameTextView)
        val priceTextView = findViewById<TextView>(R.id.detailProductPriceTextView)

        if (productId != -1) {
            // Launch a coroutine to fetch the product details
            lifecycleScope.launch {
                try {
                    // Corrected function name here
                    val product = ApiClient.productApiService.  getProductById(productId)

                    // Update the UI with the product details
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
    }
}