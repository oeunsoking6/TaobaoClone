package com.group4.taobaoclon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.group4.taobaoclon.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Set up Categories with Images
        setupCategories()

        // 2. Set up Product Grid
        binding.productRecyclerView.layoutManager = GridLayoutManager(context, 2)

        // 3. Fetch Data
        lifecycleScope.launch {
            fetchProductsAndRecommendations()
        }
    }

    private fun setupCategories() {
        // Pair of (Category Name, Image URL)
        val categories = listOf(
            Pair("Shoes", "https://i.pinimg.com/736x/4f/86/da/4f86da37127de357e668e63be2f4f0bc.jpg"),
            Pair("Jeans", "https://i.pinimg.com/1200x/cb/22/0d/cb220d21895ec1f104cf22772e8a5ea4.jpg"),
            Pair("Dresses", "https://i.pinimg.com/736x/8f/e9/d9/8fe9d9fbeccba7d31256c65fdcd223c3.jpg"),
            Pair("Phones", "https://i.pinimg.com/1200x/19/28/7c/19287c8799f8c0ce38103cfe7a240bea.jpg"),
            Pair("Watches", "https://i.pinimg.com/1200x/3f/5e/bc/3f5ebc61432c495e9b92f18bd9b53df6.jpg"),
            Pair("Beauty", "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=300")
        )

        val categoryViews = listOf(
            binding.cat1, binding.cat2, binding.cat3,
            binding.cat4, binding.cat5, binding.cat6
        )

        categories.forEachIndexed { index, (name, imageUrl) ->
            if (index < categoryViews.size) {
                val rootView = categoryViews[index].root
                val nameView = rootView.findViewById<TextView>(R.id.categoryName)
                val iconView = rootView.findViewById<ImageView>(R.id.categoryIcon)

                // Set Text
                nameView.text = name

                // Set Image using Glide with CircleCrop
                Glide.with(this)
                    .load(imageUrl)
                    .transform(CircleCrop()) // Ensures the image is a perfect circle
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(iconView)
            }
        }
    }

    private suspend fun fetchProductsAndRecommendations() {
        try {
            val productList = ApiClient.productApiService.getProducts()
            if (productList.isNotEmpty()) {
                adapter = ProductAdapter(productList) { product ->
                    val intent = Intent(activity, ProductDetailActivity::class.java)
                    intent.putExtra("PRODUCT_ID", product.id)
                    startActivity(intent)
                }
                binding.productRecyclerView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}