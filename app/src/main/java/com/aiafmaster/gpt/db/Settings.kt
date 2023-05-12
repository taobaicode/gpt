package com.aiafmaster.gpt.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Settings(
    @PrimaryKey var id: Int,
    @ColumnInfo(name="key") val key: String,
    @ColumnInfo(name="vale") var value: String,
){}

