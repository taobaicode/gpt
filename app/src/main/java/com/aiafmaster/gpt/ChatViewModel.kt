package com.aiafmaster.gpt

import androidx.lifecycle.*
import com.aiafmaster.gpt.api.ChatGPTManager
import com.aiafmaster.gpt.db.Chat
import com.aiafmaster.gpt.db.DBManager
import com.aiafmaster.gpt.db.Settings
import com.aiafmaster.gpt.repository.ChatGPTRepository
import com.aiafmaster.gpt.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ChatViewModel(private val dbManager: DBManager,
                    private val settingsRepository: SettingsRepository) : ViewModel() {
    private val conversationUi = MutableLiveData<ChatData>()
    fun getConversationUi() : LiveData<ChatData> {
        return conversationUi;
    }

    private val _chatHistory = MutableLiveData<List<ChatData>>()
    val chatHistory: LiveData<List<ChatData>> = _chatHistory
    val chat = MediatorLiveData<List<ChatData>>()
    val chatGPTRepository= ChatGPTRepository(viewModelScope)

    init {
        chat.addSource(_chatHistory) {
            chat.value=it
        }
        chat.addSource(conversationUi) {
            if (chat.value == null) {
                chat.postValue(mutableListOf(it))
            } else {
                val temp = chat.value!!.toMutableList()
                temp.add(it)
                chat.value=temp
            }
        }
        loadChatHistory()
        fetchAPIKey()

    }

    companion object {
        const val API_KEY = "api_key";
    }

    class Factory(private val dbManager: DBManager,
                  private val settingsRepository:SettingsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) : T {
            return ChatViewModel(dbManager, settingsRepository) as T
        }
    }

    private fun gptLogin() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
            }
        }
    }

    fun gptComplete(content: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                apiKey.collect {
                    val response = ChatGPTManager(it).complete(content)
                    if (response != null) {
                        conversationUi.postValue(ChatData(response, true))
                        dbManager.insertChat(
                            Chat(
                                message = response,
                                who = true,
                                time = Date().time
                            )
                        )
                    }
                }
            }
        }
    }

    fun onAsk(question : String ) : Unit {
        gptComplete(question)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val chatData = ChatData(question, false)
                conversationUi.postValue(chatData)
                dbManager.insertChat(Chat(message=chatData.content, who=chatData.bot, time = Date().time))
            }
        }
    }

    fun transcript(file : File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                apiKey.collect {key->
                    val question = ChatGPTManager(key).transcript(file)
                    question?.let { onAsk(it) }
                }
            }
        }
    }

    val apiKey: StateFlow<String> = settingsRepository.apiKey

    fun fetchAPIKey() {
        viewModelScope.launch {
            settingsRepository.fetchAPIKey()
        }
    }

    fun setAPIKey(key: String) {
        viewModelScope.launch {
            settingsRepository.updateApiKey(key)
        }
    }

    fun deleteChatHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbManager.deleteChatHistory()
            }
        }
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val chatHistory = mutableListOf<ChatData>()
                println("ChatHistory size" + chatHistory.size)
                dbManager.chats.forEach {
                    println(it.message)
                    chatHistory.add(ChatData(it.message, it.who))
                }
                _chatHistory.postValue(chatHistory)
            }
        }
    }
}