package com.group4.taobaoclon

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView // <-- Import TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerTextView = findViewById<TextView>(R.id.registerTextView) // <-- NEW

        loginButton.setOnClickListener {
            // ... (your existing login code remains here)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val request = LoginRequest(email, password)
                        val response = ApiClient.userApiService.login(request)

                        if (response.isSuccessful && response.body() != null) {
                            val token = response.body()!!.token
                            Log.d("LoginActivity", "Login successful. Token: $token")

                            val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                            with(sharedPrefs.edit()) {
                                putString("USER_TOKEN", token)
                                apply()
                            }

                            Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("LoginActivity", "Login failed: ${response.errorBody()?.string()}")
                            Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Exception during login: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // --- NEW CLICK LISTENER ---
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}