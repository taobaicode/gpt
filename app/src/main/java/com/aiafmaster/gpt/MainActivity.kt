package com.aiafmaster.gpt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiafmaster.gpt.databinding.ActivityMainBinding
import com.aiafmaster.gpt.db.DBManager
import com.aiafmaster.gpt.recording.Recording
import com.aiafmaster.gpt.ui.APISettingDialog

class MainActivity: AppCompatActivity() {
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true;
        val recyclerView = binding.chatRecyclerView
        chatAdapter = ChatRecyclerViewAdapter()
        with(recyclerView) {
            adapter = chatAdapter
            layoutManager = linearLayoutManager
        }
        chatViewModel = ViewModelProvider(this, ChatViewModel.Factory(DBManager(applicationContext)))[ChatViewModel::class.java]
        val chatRecyclerViewAdapter = recyclerView.adapter as ChatRecyclerViewAdapter
        chatViewModel.getConversationUi().observe(this) {
            chatRecyclerViewAdapter.onConversionUpdate(it)
            recyclerView.scrollToPosition(chatRecyclerViewAdapter.itemCount-1)
        }
        chatViewModel.chatHistory.observe(this) {
            chatRecyclerViewAdapter.onChatHistory(it)
            recyclerView.scrollToPosition(chatRecyclerViewAdapter.itemCount-1)
        }
        chatViewModel.apiKey.observe(this) {
            val app = application as ChatGPTApplication
            app.apiKey = it.value
        }
        val sendButton = binding.chatSendButton
        val editText = binding.chatEditText
        sendButton.setOnClickListener() {
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
                    Recording.start(this@MainActivity)
                }
                MotionEvent.ACTION_UP-> {
                    println("Up")
                    val file = Recording.stop()
                    file?.let {chatViewModel.transcript(it)}
                }
            }
            false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.menu_api_key -> {
//                val apiSettingDialog =APISettingDialog()
//                apiSettingDialog.show(supportFragmentManager, "Setting")
//            }
//            R.id.menu_delete_all -> {
//                chatViewModel.deleteChatHistory()
//                chatAdapter.clearChat()
//            }
//            R.id.menu_image -> {
//                val intent = Intent(application, ImageActivity::class.java)
//                startActivity(intent)
//            }
//        }
//        return true
//    }
}