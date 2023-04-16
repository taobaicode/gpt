package com.aiafmaster.gpt.db

import android.content.Context
import androidx.room.Room

class DBManager(private val appContext: Context) {

    private val db : ChatGPTDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            ChatGPTDatabase::class.java, "chatgpt-db"
        ).build()
    }

    val settings : List<Settings> by lazy { db.settingsDao().getAll()}

    fun updateSetting(setting : Settings) {
        db.settingsDao().update(setting)
    }

    fun insertSetting(setting: Settings) {
        db.settingsDao().insertAll(setting)
    }

    val chats: List<Chat> by lazy { db.chatDao().getAll() }

    fun insertChat(chat: Chat) {
        db.chatDao().insert(chat)
    }

    fun deleteChatHistory() {
        db.chatDao().deleteAllRows()
    }
}