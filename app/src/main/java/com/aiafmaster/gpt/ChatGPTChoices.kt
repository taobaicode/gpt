package com.aiafmaster.gpt

import com.google.gson.annotations.SerializedName

data class ChatGPTChoices (
    @SerializedName("message") val gptMessage: ChatGPTMessage,
    @SerializedName("index") val gptIndex : Int
        ) {
}