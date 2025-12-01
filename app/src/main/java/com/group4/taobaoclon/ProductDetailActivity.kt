package com.group4.taobaoclon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            fetchProductDetails(currentProductId)
        } else {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupButtons()
    }

    private fun fetchProductDetails(productId: Int) {
        lifecycleScope.launch {
            try {
                val product = ApiClient.productApiService.getProductById(productId)

                // Set Text Data
                binding.detailProductNameTextView.text = product.name
                binding.detailProductPriceTextView.text = "$${product.price}"

                // Set Images based on Product ID
                setupImages(product.id)

            } catch (e: Exception) {
                Log.e("ProductDetailActivity", "Error: ${e.message}")
                binding.detailProductNameTextView.text = "Error loading details"
            }
        }
    }

    private fun setupImages(productId: Int) {
        val sampleImages = when (productId) {
            1 -> listOf(
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600",
                "https://images.unsplash.com/photo-1598327105666-5b89351aff70?w=300",
                "https://images.unsplash.com/photo-1556656793-02715d8dd6f8?w=300"
            )
            2 -> listOf(
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600",
                "https://images.unsplash.com/photo-1572569028738-411a783143b9?w=300",
                "https://images.unsplash.com/photo-1608156639585-b3a032ef9689?w=300"
            )
            3 -> listOf(
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=300",
                "https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=300"
            )
            else -> listOf(
                "https://via.placeholder.com/600/cccccc/000000?text=No+Image",
                "https://via.placeholder.com/300"
            )
        }

        Glide.with(this)
            .load(sampleImages[0])
            .centerCrop()
            .into(binding.mainProductImage)

        binding.thumbnailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.thumbnailRecyclerView.adapter = ThumbnailAdapter(sampleImages) { clickedImageUrl ->
            Glide.with(this)
                .load(clickedImageUrl)
                .centerCrop()
                .into(binding.mainProductImage)
        }
    }

    private fun setupButtons() {
        // Add to Cart Logic
        binding.addToCartButton.setOnClickListener {
            val sharedPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
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
            val priceString = binding.detailProductPriceTextView.text.toString().replace("$", "")
            val price = priceString.toDoubleOrNull() ?: 0.0
            val name = binding.detailProductNameTextView.text.toString()

            val product = Product(currentProductId, name, price, "Seller")
            val sheetImage = "https://via.placeholder.com/150"

            val sheet = SelectionBottomSheet(product, sheetImage)
            sheet.show(supportFragmentManager, "SelectionSheet")
        }

        // History
        binding.viewHistoryButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("PRODUCT_ID", currentProductId)
            startActivity(intent)
        }

        // Bottom action placeholders - Access them via 'binding'
        binding.storeButton.setOnClickListener {
            Toast.makeText(this, "Store clicked", Toast.LENGTH_SHORT).show()
        }
        binding.chatButton.setOnClickListener {
            Toast.makeText(this, "Chat clicked", Toast.LENGTH_SHORT).show()
        }
        binding.wishlistButton.setOnClickListener {
            Toast.makeText(this, "Wishlist clicked", Toast.LENGTH_SHORT).show()
        }
    }
}