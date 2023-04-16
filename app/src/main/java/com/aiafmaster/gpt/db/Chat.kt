package com.aiafmaster.gpt.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices=[Index(value=["timestamp"])])
data class Chat (
    @PrimaryKey(autoGenerate = true) val id: Long=0,
    @ColumnInfo(name="message") val message: String,
    @ColumnInfo(name="who") val who: Boolean,
    @ColumnInfo(name="timestamp") val time: Long
){}