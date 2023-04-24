package com.aiafmaster.gpt

import com.google.gson.annotations.SerializedName

data class ChatGPTMessage(
    @SerializedName("role") val gptRole : String,
    @SerializedName("content") val gptContent : String
)