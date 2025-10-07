package com.group4.taobaoclon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClicked: (Product) -> Unit // Callback for when an item is clicked
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.productNameTextView)
        val priceTextView: TextView = view.findViewById(R.id.productPriceTextView)
        val sellerTextView: TextView = view.findViewById(R.id.productSellerTextView)
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
        holder.sellerTextView.text = "Sold by ${product.seller}"

        // Set the click listener on the entire list item view
        holder.itemView.setOnClickListener {
            onItemClicked(product)
        }
    }

    override fun getItemCount() = products.size
}