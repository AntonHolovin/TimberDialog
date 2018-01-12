package com.golovin.timberdialog.ui

import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.golovin.timberdialog.LogEntry
import com.golovin.timberdialog.R

class LogAdapter : RecyclerView.Adapter<LogAdapter.ViewHolder>() {
    private val items = mutableListOf<LogEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position % 2 == 0)
    }

    override fun getItemCount(): Int = items.size

    fun setEntries(entries: List<LogEntry>) {
        items.clear()
        items.addAll(entries)

        notifyDataSetChanged()
    }

    fun addEntry(entry: LogEntry) {
        items.add(entry)

        notifyItemInserted(items.size - 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val levelTextView = itemView.findViewById<TextView>(R.id.text_level)
        private val tagTextView = itemView.findViewById<TextView>(R.id.text_tag)
        private val messageTextView = itemView.findViewById<TextView>(R.id.text_message)

        fun bind(entry: LogEntry, even: Boolean) {
            val backgroundColor = if (even) R.color.grey_50 else R.color.grey_100
            tagTextView.setBackgroundResource(backgroundColor)
            messageTextView.setBackgroundResource(backgroundColor)

            itemView.setBackgroundResource(getBackgroundForLevel(entry.level))

            levelTextView.text = entry.displayLevel
            tagTextView.text = entry.tag
            messageTextView.text = entry.message
        }

        @ColorRes
        private fun getBackgroundForLevel(level: Int): Int = when (level) {
            Log.VERBOSE, Log.DEBUG -> R.color.debug
            Log.INFO -> R.color.info
            Log.WARN -> R.color.warn
            Log.ERROR, Log.ASSERT -> R.color.error
            else -> R.color.unknown
        }
    }
}