package com.group4.taobaoclon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val historyEvents: List<HistoryEvent>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // We use built-in Android IDs text1 and text2 because we are using a built-in layout
        val descriptionTextView: TextView = view.findViewById(android.R.id.text1)
        val timestampTextView: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use a built-in Android layout for simplicity
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = historyEvents[position]
        holder.descriptionTextView.text = event.description
        holder.timestampTextView.text = formatTimestamp(event.timestamp)
    }

    override fun getItemCount() = historyEvents.size

    private fun formatTimestamp(timestamp: Long): String {
        // The blockchain timestamp is in seconds, so multiply by 1000 to get milliseconds
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}