package com.aiafmaster.gpt.api

import com.google.gson.annotations.SerializedName

data class ChatGPTMessage(
    @SerializedName("role") val gptRole : String,
    @SerializedName("content") val gptContent : String
)