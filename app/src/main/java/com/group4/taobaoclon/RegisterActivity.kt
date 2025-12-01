package com.group4.taobaoclon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val request = RegisterRequest(email, password)
                        val response = ApiClient.userApiService.register(request)

                        if (response.isSuccessful && response.body() != null) {
                            val responseData = response.body()!!
                            val assignedRole = responseData.user.role // Backend decides (ADMIN vs USER)

                            // --- CRITICAL FIX: Use "TaobaoStore" ---
                            val sharedPrefs = getSharedPreferences("TaobaoStore", MODE_PRIVATE)
                            with(sharedPrefs.edit()) {
                                putString("USER_ID", responseData.user.id)
                                putString("ROLE", assignedRole)
                                apply()
                            }

                            Toast.makeText(this@RegisterActivity, "Registered as $assignedRole! Please Log In.", Toast.LENGTH_LONG).show()

                            // Go to Login Screen
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("RegisterActivity", "Registration failed: ${response.errorBody()?.string()}")
                            Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterActivity", "Exception during registration: ${e.message}")
                        Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        loginTextView.setOnClickListener {
            finish()
        }
    }
}