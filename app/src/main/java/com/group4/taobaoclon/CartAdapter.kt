package com.group4.taobaoclon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Matching IDs from item_cart.xml
        val storeName: TextView = view.findViewById(R.id.storeName)
        val name: TextView = view.findViewById(R.id.productName)
        val variant: TextView = view.findViewById(R.id.productVariant)
        val price: TextView = view.findViewById(R.id.productPrice)
        val quantity: TextView = view.findViewById(R.id.productQuantity)
        val image: ImageView = view.findViewById(R.id.productImage)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
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

        // --- Simulated Data to match the "Official" look ---

        // 1. Store Name
        holder.storeName.text = when (item.productId) {
            1 -> "Apple Store From RPC"
            2 -> "AudioPhile Official"
            3 -> "TechGear Mall"
            else -> "Taobao Selected"
        }

        // 2. Variant Info
        holder.variant.text = when (item.productId) {
            1 -> "Color: Golden; Size: 512GB"
            2 -> "Color: White; Wireless Case"
            3 -> "Series 9; Midnight; Sport Band"
            else -> "Standard"
        }

        // 3. Image Logic
        val imageUrl = item.image ?: when (item.productId) {
            1 -> "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300"
            2 -> "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=300"
            3 -> "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300"
            else -> "https://via.placeholder.com/300"
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.banner_home)
            .into(holder.image)

        // Click Listeners
        holder.btnPlus.setOnClickListener { onQuantityChanged(item, 1) }
        holder.btnMinus.setOnClickListener { onQuantityChanged(item, -1) }
    }

    override fun getItemCount() = cartItems.size
}