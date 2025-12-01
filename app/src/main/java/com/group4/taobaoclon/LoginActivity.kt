package com.group4.taobaoclon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
        val registerTextView = findViewById<TextView>(R.id.registerTextView)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val request = LoginRequest(email, password)
                        val response = ApiClient.userApiService.login(request)

                        if (response.isSuccessful && response.body() != null) {
                            val loginData = response.body()!!

                            Log.d("LoginActivity", "Login successful. Role: ${loginData.role}")

                            // --- CRITICAL FIX: Use "TaobaoStore" ---
                            val sharedPrefs = getSharedPreferences("TaobaoStore", MODE_PRIVATE)
                            with(sharedPrefs.edit()) {
                                putString("TOKEN", loginData.token)
                                putString("USER_ID", loginData.userId)
                                putString("ROLE", loginData.role) // Save ADMIN or USER
                                apply()
                            }

                            Toast.makeText(this@LoginActivity, "Welcome ${loginData.role}!", Toast.LENGTH_SHORT).show()

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

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}