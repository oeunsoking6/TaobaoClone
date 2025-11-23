package com.group4.taobaoclon

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.group4.taobaoclon.databinding.ActivityProductDetailBinding
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var currentProductId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentProductId = intent.getIntExtra("PRODUCT_ID", -1)

        if (currentProductId != -1) {
            fetchProductDetails()
        } else {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupButtons()
    }

    private fun fetchProductDetails() {
        lifecycleScope.launch {
            try {
                val product = ApiClient.productApiService.getProductById(currentProductId)

                // Set Text Data
                binding.detailProductNameTextView.text = product.name
                binding.detailProductPriceTextView.text = "$${product.price}"

                // Set Images (Using Placeholders for now since backend lacks image fields)
                setupImages()

            } catch (e: Exception) {
                Log.e("ProductDetailActivity", "Error: ${e.message}")
                binding.detailProductNameTextView.text = "Error loading details"
            }
        }
    }

    private fun setupImages() {
        // Dummy images for demonstration.
        // In the future, these would come from product.images
        val sampleImages = listOf(
            "https://via.placeholder.com/600/92c952",
            "https://via.placeholder.com/600/771796",
            "https://via.placeholder.com/600/24f355",
            "https://via.placeholder.com/600/d32776"
        )

        // Load the first image as main
        Glide.with(this)
            .load(sampleImages[0])
            .centerCrop()
            .into(binding.mainProductImage)

        // Setup Thumbnail List
        binding.thumbnailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.thumbnailRecyclerView.adapter = ThumbnailAdapter(sampleImages) { clickedImageUrl ->
            // When thumbnail is clicked, update main image
            Glide.with(this)
                .load(clickedImageUrl)
                .centerCrop()
                .into(binding.mainProductImage)
        }
    }

    private fun setupButtons() {
        // Add to Cart Logic
        binding.addToCartButton.setOnClickListener {
            val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val token = sharedPrefs.getString("USER_TOKEN", null)

            if (token == null) {
                Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val request = AddToCartRequest(productId = currentProductId, quantity = 1)
                    val response = ApiClient.cartApiService.addToCart("Bearer $token", request)
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProductDetailActivity, "Added to cart!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProductDetailActivity, "Failed to add.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ProductDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Buy Now -> Open Selection Sheet
        binding.buyNowButton.setOnClickListener {
            // We need to fetch the full product object first or pass it if we have it stored
            // Assuming we have 'currentProduct' object available in the class scope
            // If not, you might need to store the product result from fetchProductDetails in a variable.

            // For now, let's create a temporary product object from the displayed data
            val price = binding.detailProductPriceTextView.text.toString().replace("$", "").toDoubleOrNull() ?: 0.0
            val product = Product(currentProductId, binding.detailProductNameTextView.text.toString(), price, "Seller")

            val sheet = SelectionBottomSheet(product, "https://via.placeholder.com/600/92c952") // Use real image URL
            sheet.show(supportFragmentManager, "SelectionSheet")
        }

        // History
        binding.viewHistoryButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("PRODUCT_ID", currentProductId)
            startActivity(intent)
        }
    }
}