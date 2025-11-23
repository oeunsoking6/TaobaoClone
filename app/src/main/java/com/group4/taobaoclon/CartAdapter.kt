package com.group4.taobaoclon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private val cartItems: List<CartItem>) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.productName)
        val price: TextView = view.findViewById(R.id.productPrice)
        val quantity: TextView = view.findViewById(R.id.productQuantity)
        val image: ImageView = view.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cartItems[position]
        holder.name.text = item.name
        holder.price.text = "$${item.price}"
        holder.quantity.text = item.quantity.toString()

        // Logic to load image
        // In a real app, this URL comes from the API.
        // Since our backend doesn't return it yet, we will simulate it based on ID, just like ProductAdapter.

        val imageUrl = item.image ?: when (item.productId) {
            1 -> "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200" // Phone
            2 -> "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=200" // Earbuds
            3 -> "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200" // Watch
            else -> "https://via.placeholder.com/150"
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.image)

        // Handle quantity buttons (optional logic for later)
        // holder.itemView.findViewById<View>(R.id.btnPlus).setOnClickListener { ... }
    }

    override fun getItemCount() = cartItems.size
}