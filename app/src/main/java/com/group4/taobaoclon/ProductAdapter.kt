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

        holder.nameTextView.text = product.name
        holder.priceTextView.text = "$${product.price}"

        // --- Logic to show different images based on Product ID ---
        val imageUrl = when (product.id) {
            1 -> "https://i.pinimg.com/1200x/e9/9f/d3/e99fd3881d16ea75b2ca0d5283536a02.jpg" // Smartphone
            2 -> "https://i.pinimg.com/736x/e1/ba/c3/e1bac38b6c7f361a10c480ae9b57fcd0.jpg" // Earbuds
            3 -> "https://i.pinimg.com/1200x/f9/e5/88/f9e5889fa2fc666b2f4b272726ad1778.jpg" // Smartwatch
            else -> "https://via.placeholder.com/300"
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.banner_home)
            .into(holder.productImage)

        holder.itemView.setOnClickListener {
            onItemClicked(product)
        }
    }

    override fun getItemCount() = products.size
}