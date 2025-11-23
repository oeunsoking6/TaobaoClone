package com.group4.taobaoclon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.taobaoclon.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup Sidebar
        val mainCategories = listOf("For you", "Women's Clothing", "Women's Shoes", "Bags & Luggage", "Underwear", "Beauty", "Jewelry", "Men's Clothing", "Sports", "Toys", "Sneakers", "Kids")

        val sidebarAdapter = SidebarAdapter(mainCategories) { selectedCategory ->
            // When a category is clicked, update the title and refresh the grid
            binding.categoryTitle.text = selectedCategory
            // In a real app, you would fetch specific data for 'selectedCategory' here
            // For now, we just reshuffle the dummy data to simulate a change
            setupSubcategoryGrid()
        }

        binding.categorySidebarRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sidebarAdapter
        }

        // 2. Setup Initial Subcategory Grid
        setupSubcategoryGrid()
    }

    private fun setupSubcategoryGrid() {
        // Dummy data to match your screenshot style
        val subcategories = listOf(
            Subcategory("Hair Accs", "https://via.placeholder.com/150/FF0000?text=Hair"),
            Subcategory("Men's Bags", "https://via.placeholder.com/150/00FF00?text=Bag"),
            Subcategory("Boots", "https://via.placeholder.com/150/0000FF?text=Boots"),
            Subcategory("Fishing", "https://via.placeholder.com/150/FFFF00?text=Fish"),
            Subcategory("T-Shirts", "https://via.placeholder.com/150/FF00FF?text=Shirt"),
            Subcategory("Dresses", "https://via.placeholder.com/150/00FFFF?text=Dress"),
            Subcategory("High Tops", "https://via.placeholder.com/150/000000?text=Shoe"),
            Subcategory("Bodysuits", "https://via.placeholder.com/150/FFFFFF?text=Body"),
            Subcategory("Casual", "https://via.placeholder.com/150/888888?text=Casual"),
        )

        val gridAdapter = SubcategoryAdapter(subcategories)
        binding.subcategoryRecyclerView.apply {
            // 3 columns for the grid
            layoutManager = GridLayoutManager(context, 3)
            adapter = gridAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}