package com.group4.taobaoclon

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_history)

        val etProductId = findViewById<TextInputEditText>(R.id.etProductId)
        val etLocation = findViewById<TextInputEditText>(R.id.etLocation)
        val etStatus = findViewById<TextInputEditText>(R.id.etStatus)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateBlockchain)

        // Pre-fill Product ID if passed from previous screen
        val passedId = intent.getStringExtra("PRODUCT_ID")
        if (passedId != null) {
            etProductId.setText(passedId)
        }

        btnUpdate.setOnClickListener {
            val id = etProductId.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val status = etStatus.text.toString().trim()

            if (id.isEmpty() || location.isEmpty() || status.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Combine Location and Status into one string (Matches our PowerShell logic)
            val fullDescription = "$status - $location"

            sendToBlockchain(id, fullDescription)
        }
    }

    private fun sendToBlockchain(id: String, description: String) {
        val request = AddHistoryRequest(id, description)
        val api = BlockchainClient.instance // <--- USE THIS

        api.addHistory(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Blockchain Updated Successfully!", Toast.LENGTH_LONG).show()
                    finish() // Close screen and go back
                } else {
                    Toast.makeText(applicationContext, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(applicationContext, "Network Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}