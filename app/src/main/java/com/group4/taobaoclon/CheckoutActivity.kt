package com.group4.taobaoclon

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

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
        totalPriceView.text = "$$productPrice" // Assuming quantity 1 for now

        // Load Image
        Glide.with(this)
            .load(productImage)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(imageView)

        // Back Button Logic
        backButton.setOnClickListener {
            finish() // Go back
        }

        // Place Order Logic
        placeOrderButton.setOnClickListener {
            Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
            // Here you would typically call an API to create the order
            finish() // Close checkout and go back
        }
    }
}