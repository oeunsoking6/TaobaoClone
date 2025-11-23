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
    }

    private fun fetchCart() {
        val sharedPrefs = activity?.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val token = sharedPrefs?.getString("USER_TOKEN", null)

        if (token == null) {
            Toast.makeText(context, "Please log in", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.cartApiService.getCart("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val cartItems = response.body()!!
                    binding.cartCount.text = "(${cartItems.size})"
                    binding.cartRecyclerView.adapter = CartAdapter(cartItems)

                    // Calculate Total
                    var total = 0.0
                    for (item in cartItems) {
                        total += (item.price * item.quantity)
                    }
                    binding.totalPrice.text = "$${String.format("%.2f", total)}"
                } else {
                    Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CartFragment", "Error: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}