package com.aiafmaster.gpt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ChatRecyclerViewAdapter : RecyclerView.Adapter<ChatViewHolder>() {
    private val conversation : MutableList<ChatData> = mutableListOf();
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        var resId = when(viewType) {
            BOT->R.layout.chat_right_view
            ASK->R.layout.chat_view
            else -> R.layout.chat_view
        }
        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        return ChatViewHolder(view)
    }

    companion object{
        const val BOT = 0;
        const val ASK = 1;
    }

    override fun getItemViewType(position: Int): Int {
        if(conversation[position].bot)
            return BOT
        return ASK
    }

    override fun getItemCount(): Int {
        return conversation.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        with(holder) {
            // imageView.
            chatTextView.text = conversation[position].content
        }
    }

    fun onConversionUpdate(chatData : ChatData) {
        val pos = conversation.size
        conversation.add(chatData)
        notifyItemRangeChanged(pos, 1)
    }

    fun onChatHistory(history: List<ChatData>) {
        val size = conversation.size
        conversation.addAll(history)
        notifyItemRangeChanged(size, history.size)
    }

    fun clearChat() {
        conversation.clear()
        notifyDataSetChanged()
    }

    fun onChatListChange(chats: List<ChatData>) {
        val size = conversation.size
        val newChats = chats.filterIndexed() {
                index, _-> index >= size
        }
        conversation.addAll(newChats)
        notifyItemRangeChanged(size, conversation.size)
    }
}