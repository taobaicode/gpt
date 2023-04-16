package com.aiafmaster.gpt.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * from chat")
    fun getAll(): List<Chat>

    @Insert
    fun insert(chat: Chat)

    @Query("Delete FROM chat")
    fun deleteAllRows()
}