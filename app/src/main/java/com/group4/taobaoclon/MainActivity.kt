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

        Log.d("MainActivity", "onCreate: Activity starting.")

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            Log.d("MainActivity", "onCreate: Coroutine launched, starting to fetch products.")
            fetchProductsAndRecommendations()
        }
    }

    private suspend fun fetchProductsAndRecommendations() {
        try {
            Log.d("MainActivity", "fetchProducts: Calling productApiService.getProducts()")
            val productList = ApiClient.productApiService.getProducts()
            Log.d("MainActivity", "fetchProducts: Received ${productList.size} products.")

            if (productList.isNotEmpty()) {
                adapter = ProductAdapter(productList) { product ->
                    val intent = Intent(this, ProductDetailActivity::class.java)
                    intent.putExtra("PRODUCT_ID", product.id)
                    startActivity(intent)
                }
                productRecyclerView.adapter = adapter
                Log.d("MainActivity", "fetchProducts: Adapter set with products.")

                val firstProductId = productList[0].id
                fetchRecommendations(firstProductId)
            } else {
                Log.d("MainActivity", "fetchProducts: Product list is empty.")
            }

        } catch (e: Exception) {
            // This is the most important log to check
            Log.e("MainActivity", "Error fetching products: ${e.message}", e)
        }
    }

    private suspend fun fetchRecommendations(productId: Int) {
        try {
            Log.d("MainActivity", "fetchRecs: Calling getRecommendations for product ID $productId")
            val recommendedProducts = ApiClient.recommendationApiService.getRecommendations(productId)
            Log.d("MainActivity", "fetchRecs: Found ${recommendedProducts.size} recommendations.")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error fetching recommendations: ${e.message}", e)
        }
    }
}