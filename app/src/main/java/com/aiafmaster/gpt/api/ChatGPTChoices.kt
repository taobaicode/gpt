package com.aiafmaster.gpt.api

import com.aiafmaster.gpt.api.ChatGPTMessage
import com.google.gson.annotations.SerializedName

data class ChatGPTChoices (
    @SerializedName("message") val gptMessage: ChatGPTMessage,
    @SerializedName("index") val gptIndex : Int
        )