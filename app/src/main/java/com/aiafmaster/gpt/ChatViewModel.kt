package com.aiafmaster.gpt

import androidx.lifecycle.*
import com.aiafmaster.gpt.db.Chat
import com.aiafmaster.gpt.db.DBManager
import com.aiafmaster.gpt.db.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ChatViewModel(private val dbManager: DBManager) : ViewModel() {
    private val conversationUi = MutableLiveData<ChatData>()
    fun getConversationUi() : LiveData<ChatData> {
        return conversationUi;
    }

    private val _chatHistory = MutableLiveData<List<ChatData>>()
    val chatHistory: LiveData<List<ChatData>> = _chatHistory
    val chat = MediatorLiveData<List<ChatData>>()

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

    class Factory(private val dbManager: DBManager): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) : T {
            return ChatViewModel(dbManager) as T
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
                val apiKey = getAPIKey()
                if (apiKey != null) {
                    val response = ChatGPTManager(apiKey).complete(content)
                    if (response != null) {
                        conversationUi.postValue(ChatData(response, true))
                        dbManager.insertChat(Chat(message=response, who=true, time = Date().time))
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
                val apiKey = getAPIKey()
                if (apiKey != null) {
                    val question = ChatGPTManager(apiKey).transcript(file)
                    question?.let { onAsk(it) }
                }
            }
        }
    }

    private val _apiKey = MutableLiveData<Settings>(Settings(-1, API_KEY, ""))
    val apiKey: LiveData<Settings> = _apiKey

    fun fetchAPIKey() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbManager.settings.forEach() {
                    if (it.key == API_KEY) {
                        _apiKey.postValue(it)
                    }
                }
            }
        }
    }

    fun setAPIKey(key: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _apiKey.value!!.value = key
                if (_apiKey.value!!.id == -1) {
                    apiKey.value!!.id = 0
                    dbManager.insertSetting(_apiKey.value!!)
                } else {
                    dbManager.updateSetting(_apiKey.value!!)
                }
            }
        }
    }

    fun deleteChatHistory() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbManager.deleteChatHistory()
            }
        }
    }
    private suspend fun getAPIKey() : String? {
        return _apiKey.value?.value
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