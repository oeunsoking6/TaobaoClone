package com.group4.taobaoclon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            fetchProductsAndRecommendations()
        }
    }

    private suspend fun fetchProductsAndRecommendations() {
        try {
            val productList = ApiClient.productApiService.getProducts()

            adapter = ProductAdapter(productList) { product ->
                // Create an Intent to open ProductDetailActivity
                val intent = Intent(this, ProductDetailActivity::class.java)

                // Pass the ID of the clicked product to the new activity
                intent.putExtra("PRODUCT_ID", product.id)

                // Start the new activity
                startActivity(intent)
            }
            productRecyclerView.adapter = adapter

            if (productList.isNotEmpty()) {
                val firstProductId = productList[0].id
                fetchRecommendations(firstProductId)
            }

        } catch (e: Exception) {
            Log.e("MainActivity", "Error fetching products: ${e.message}")
        }
    }

    private suspend fun fetchRecommendations(productId: Int) {
        try {
            val recommendedProducts = ApiClient.recommendationApiService.getRecommendations(productId)
            Log.d("MainActivity", "Recommendations for product $productId: $recommendedProducts")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error fetching recommendations: ${e.message}")
        }
    }
}