package com.group4.taobaoclon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private var currentProductId: String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        val passedId = intent.getIntExtra("PRODUCT_ID", -1)
        if (passedId != -1) {
            currentProductId = passedId.toString()
        }

        // --- ADMIN BUTTON LOGIC ---
        val fab = findViewById<FloatingActionButton>(R.id.fabAddHistory)

        if (isUserAdmin()) {
            fab.visibility = View.VISIBLE
            fab.setOnClickListener {
                val intent = Intent(this, AddHistoryActivity::class.java)
                intent.putExtra("PRODUCT_ID", currentProductId)
                startActivity(intent)
            }
        } else {
            fab.visibility = View.GONE
        }
        // --------------------------
    }

    override fun onResume() {
        super.onResume()
        fetchHistory(currentProductId)
    }

    private fun fetchHistory(productId: String) {
        // NOTE: Using BlockchainClient (Port 8084)
        val api = BlockchainClient.instance

        api.getProductHistory(productId).enqueue(object : Callback<List<HistoryItem>> {
            override fun onResponse(call: Call<List<HistoryItem>>, response: Response<List<HistoryItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    val historyList = response.body()!!
                    if (historyList.isNotEmpty()) {
                        adapter = HistoryAdapter(historyList)
                        historyRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@HistoryActivity, "No history found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("HistoryActivity", "Server Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<HistoryItem>>, t: Throwable) {
                Log.e("HistoryActivity", "Network Error: ${t.message}")
                Toast.makeText(this@HistoryActivity, "Cannot connect to Blockchain", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- CHECK ROLE FROM "TaobaoStore" ---
    private fun isUserAdmin(): Boolean {
        // CRITICAL FIX: Must match the name used in LoginActivity
        val sharedPreferences = getSharedPreferences("TaobaoStore", MODE_PRIVATE)

        // Safety: Default to "USER" if nothing is found
        val role = sharedPreferences.getString("ROLE", "USER")

        Log.d("HistoryActivity", "Current User Role is: $role")

        return role == "ADMIN"
    }
}