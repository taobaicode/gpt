package com.aiafmaster.gpt

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView = itemView.findViewById<ImageView>(R.id.chatImageView)
    val chatTextView = itemView.findViewById<TextView>(R.id.chatTextView)
}