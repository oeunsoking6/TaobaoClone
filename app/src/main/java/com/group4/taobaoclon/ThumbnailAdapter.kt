package com.group4.taobaoclon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ThumbnailAdapter(
    private val imageUrls: List<String>,
    private val onThumbnailClick: (String) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>() {

    class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnailImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)
        return ThumbnailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val imageUrl = imageUrls[position]

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.thumbnailImage)

        holder.itemView.setOnClickListener {
            onThumbnailClick(imageUrl)
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}