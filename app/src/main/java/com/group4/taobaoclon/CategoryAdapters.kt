package com.group4.taobaoclon

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// --- Sidebar Adapter ---
class SidebarAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<SidebarAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sidebar_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = categories[position]

        // Logic to match the design:
        // Selected: White background, Orange text, Bold
        // Unselected: Gray background, Black text, Normal
        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.bg_sidebar_item_selected)
            holder.textView.setTextColor(Color.parseColor("#FF5000"))
            holder.textView.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"))
            holder.textView.setTextColor(Color.parseColor("#333333"))
            holder.textView.setTypeface(null, android.graphics.Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onCategoryClick(categories[position])
        }
    }

    override fun getItemCount() = categories.size
}

// --- Subcategory Adapter (Grid) ---
data class Subcategory(val name: String, val imageUrl: String)

class SubcategoryAdapter(private val items: List<Subcategory>) : RecyclerView.Adapter<SubcategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.subcategoryImage)
        val textView: TextView = view.findViewById(R.id.subcategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subcategory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.name

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.imageView)
    }

    override fun getItemCount() = items.size
}