package com.aiafmaster.gpt.ui.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiafmaster.gpt.*
import com.aiafmaster.gpt.databinding.ActivityMainBinding
import com.aiafmaster.gpt.db.DBManager
import com.aiafmaster.gpt.recording.Recording

class ChatFragment: Fragment() {
    private val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(requireActivity(),
            ChatViewModel.Factory(DBManager(requireContext().applicationContext)))[ChatViewModel::class.java]
    }
    private lateinit var chatAdapter: ChatRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ActivityMainBinding.inflate(layoutInflater, container, false)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        val recyclerView = binding.chatRecyclerView
        chatAdapter = ChatRecyclerViewAdapter()
        with(recyclerView) {
            adapter = chatAdapter
            layoutManager = linearLayoutManager
        }
//        chatViewModel = ViewModelProvider(requireActivity(),
//            ChatViewModel.Factory(DBManager(requireContext().applicationContext)))[ChatViewModel::class.java]
        val chatRecyclerViewAdapter = recyclerView.adapter as ChatRecyclerViewAdapter
//        chatViewModel.getConversationUi().observe(viewLifecycleOwner) {
//            chatRecyclerViewAdapter.onConversionUpdate(it)
//            recyclerView.scrollToPosition(chatRecyclerViewAdapter.itemCount-1)
//        }
//        chatViewModel.chatHistory.observe(viewLifecycleOwner) {
//            chatRecyclerViewAdapter.onChatHistory(it)
//            recyclerView.scrollToPosition(chatRecyclerViewAdapter.itemCount-1)
//        }
        chatViewModel.chat.observe(viewLifecycleOwner) {
            println("Change list changed ${it.size}")
            chatRecyclerViewAdapter.onChatListChange(it)
            recyclerView.scrollToPosition(chatRecyclerViewAdapter.itemCount-1)
        }
        chatViewModel.apiKey.observe(viewLifecycleOwner) {
            val app = requireActivity().applicationContext as ChatGPTApplication
            app.apiKey = it.value
        }
        val sendButton = binding.chatSendButton
        val editText = binding.chatEditText
        sendButton.setOnClickListener {
            chatViewModel.onAsk(editText.text.toString())
            // chatViewModel.transcript(this@MainActivity.getFileStreamPath("r8.m4a"), this@MainActivity.getFileStreamPath("request.log"))
//            chatViewModel.transcript(this@MainActivity.getFileStreamPath("r8.m4a"))
            editText.text.clear()
        }
        val recordingButton = binding.recordingButton
        recordingButton.backgroundTintList= resources.getColorStateList(R.color.recording_button, null)
        recordingButton.setOnTouchListener {
                _: View, event: MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN-> {
                    println("Down")
                    Recording.start(context as Activity)
                }
                MotionEvent.ACTION_UP-> {
                    println("Up")
                    val file = Recording.stop()
                    file?.let {chatViewModel.transcript(it)}
                }
            }
            false
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_delete_all -> {
                chatViewModel.deleteChatHistory()
                chatAdapter.clearChat()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}