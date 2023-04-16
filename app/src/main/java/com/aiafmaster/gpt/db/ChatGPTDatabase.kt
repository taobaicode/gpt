package com.aiafmaster.gpt.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Settings::class, Chat::class], version = 1, exportSchema = false)
abstract class ChatGPTDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun chatDao(): ChatDao
}