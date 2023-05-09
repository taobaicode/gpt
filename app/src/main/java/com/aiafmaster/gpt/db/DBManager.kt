package com.aiafmaster.gpt.db

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.*

interface DBManager {
    val settings: List<Settings>
    val chats: List<Chat>
    fun updateSetting(setting : Settings) {}
    fun insertSetting(setting: Settings) {}
    fun insertChat(chat: Chat) {}
    fun deleteChatHistory() {}
}
class DBManagerImpl(private val appContext: Context) : DBManager {
    private val db : ChatGPTDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            ChatGPTDatabase::class.java, "chatgpt-db"
        ).build()
    }

    override val settings : List<Settings> by lazy {db.settingsDao().getAll()}

    override fun updateSetting(setting : Settings) {
        db.settingsDao().updateSettings(setting.key, setting.value)
    }

    override fun insertSetting(setting: Settings) {
        db.settingsDao().insertAll(setting)
    }

    override val chats: List<Chat> by lazy { db.chatDao().getAll() }

    override fun insertChat(chat: Chat) {
        db.chatDao().insert(chat)
    }

    override fun deleteChatHistory() {
        db.chatDao().deleteAllRows()
    }
}