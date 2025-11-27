package com.group4.taobaoclon

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.taobaoclon.databinding.FragmentCartBinding
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(context)

        fetchCart()

        binding.checkoutButton.setOnClickListener {
            Toast.makeText(context, "Proceeding to Checkout...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCart() {
        val token = getToken() ?: return

        lifecycleScope.launch {
            try {
                val response = ApiClient.cartApiService.getCart("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val cartItems = response.body()!!
                    binding.cartCount.text = "(${cartItems.size})"
                    setupAdapter(cartItems)
                    updateTotal(cartItems)
                } else {
                    binding.cartCount.text = "(0)"
                    binding.totalPrice.text = "$0.00"
                }
            } catch (e: Exception) {
                Log.e("CartFragment", "Error: ${e.message}")
            }
        }
    }

    private fun setupAdapter(items: List<CartItem>) {
        cartAdapter = CartAdapter(items) { item, change ->
            updateCartItemQuantity(item, change)
        }
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun updateCartItemQuantity(item: CartItem, change: Int) {
        val token = getToken() ?: return

        lifecycleScope.launch {
            try {
                val request = AddToCartRequest(item.productId, change)
                val response = ApiClient.cartApiService.addToCart("Bearer $token", request)

                if (response.isSuccessful) {
                    fetchCart()
                } else {
                    Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CartFragment", "Update Error: ${e.message}")
            }
        }
    }

    private fun updateTotal(items: List<CartItem>) {
        var total = 0.0
        for (item in items) {
            if (item.quantity > 0) {
                total += (item.price * item.quantity)
            }
        }
        binding.totalPrice.text = "$${String.format("%.2f", total)}"
    }

    private fun getToken(): String? {
        val sharedPrefs = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPrefs?.getString("USER_TOKEN", null)
        if (token == null) {
            Toast.makeText(context, "Please log in", Toast.LENGTH_SHORT).show()
        }
        return token
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}