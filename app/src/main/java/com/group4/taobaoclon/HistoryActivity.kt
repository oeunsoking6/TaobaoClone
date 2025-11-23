package com.group4.taobaoclon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        val productId = intent.getIntExtra("PRODUCT_ID", -1)

        if (productId != -1) {
            lifecycleScope.launch {
                try {
                    // Call the local blockchain service
                    val response = ApiClient.blockchainApiService.getHistory(productId)

                    if (response.isSuccessful && response.body() != null) {
                        val historyList = response.body()!!

                        if (historyList.isNotEmpty()) {
                            historyRecyclerView.adapter = HistoryAdapter(historyList)
                        } else {
                            Toast.makeText(this@HistoryActivity, "No history found for this product", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Log.e("HistoryActivity", "Failed to fetch history: ${response.errorBody()?.string()}")
                        Toast.makeText(this@HistoryActivity, "Failed to fetch history", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("HistoryActivity", "Error fetching history: ${e.message}")
                    Toast.makeText(this@HistoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show()
        }
    }
}