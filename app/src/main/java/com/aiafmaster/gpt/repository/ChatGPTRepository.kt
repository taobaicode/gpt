package com.aiafmaster.gpt.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aiafmaster.gpt.ChatData
import com.aiafmaster.gpt.api.ChatGPTManager
import com.aiafmaster.gpt.db.Chat
import com.aiafmaster.gpt.db.DBManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.Date

class ChatGPTRepository(
        private val settingsRepository: SettingsRepository,
        private val dbManager: DBManager,
        coroutineScope: CoroutineScope,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
        ) {
    private var apiKey:String = ""

    private val _conversationUi = MutableLiveData<ChatData>()
    val conversationUi:LiveData<ChatData> = _conversationUi
    init {
        coroutineScope.launch {
            println("start collect")
            settingsRepository.apiKey.collect {apiKey=it}
            println("end collect")
        }
    }

    suspend fun onAsk(question : String ) : Unit {
        withContext(dispatcher) {
            val chatData = ChatData(question, false, Date())
            _conversationUi.postValue(chatData)
            dbManager.insertChat(Chat(message=chatData.content, who=chatData.bot, time = Date().time))
            gptComplete(question)
            println("onAsk done")
        }
    }
    private fun gptComplete(content: String) {
        val response = ChatGPTManager(apiKey).complete(content)
        if (response != null) {
            _conversationUi.postValue(ChatData(response, true, Date()))
            dbManager.insertChat(
                Chat(
                    message = response,
                    who = true,
                    time = Date().time
                )
            )
        }
    }

    suspend fun transcript(file : File) {
        withContext(dispatcher) {
            val question = ChatGPTManager(apiKey).transcript(file)
            question?.let { onAsk(it) }
        }
    }
}