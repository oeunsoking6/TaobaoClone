package com.group4.taobaoclon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val nameTextView: TextView = view.findViewById(R.id.productName)
        val priceTextView: TextView = view.findViewById(R.id.productPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        // Set Text
        holder.nameTextView.text = product.name
        holder.priceTextView.text = "$${product.price}"

        // --- ASSIGN DIFFERENT IMAGES BASED ON PRODUCT ID ---
        val imageUrl = when (product.id) {
            1 -> "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500" // Smartphone
            2 -> "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500" // Earbuds
            3 -> "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500" // Smartwatch
            else -> "https://via.placeholder.com/300" // Default for others
        }

        // Load the specific image
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.banner_home) // Show banner while loading
            .into(holder.productImage)

        holder.itemView.setOnClickListener {
            onItemClicked(product)
        }
    }

    override fun getItemCount() = products.size
}