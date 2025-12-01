package com.group4.taobaoclon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val status: TextView = view.findViewById(R.id.orderStatus)
        val summary: TextView = view.findViewById(R.id.orderSummary)
        val total: TextView = view.findViewById(R.id.orderTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        // Show last 8 chars of ID for brevity
        val shortId = order._id.takeLast(8).uppercase()
        holder.orderId.text = "Order #$shortId"
        holder.status.text = order.status
        holder.total.text = "$${order.totalAmount}"

        // Create a simple summary string (e.g., "iPhone 17 + 1 more")
        if (order.items.isNotEmpty()) {
            val firstItem = order.items[0].name
            val extraCount = order.items.size - 1
            if (extraCount > 0) {
                holder.summary.text = "$firstItem + $extraCount more"
            } else {
                holder.summary.text = firstItem
            }
        }
    }

    override fun getItemCount() = orders.size
}