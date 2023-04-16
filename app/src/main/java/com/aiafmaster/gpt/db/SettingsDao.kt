package com.aiafmaster.gpt.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings")
    fun getAll() : List<Settings>

    @Insert
    fun insertAll(vararg settings: Settings)

    @Update
    fun update(vararg settings: Settings)
}