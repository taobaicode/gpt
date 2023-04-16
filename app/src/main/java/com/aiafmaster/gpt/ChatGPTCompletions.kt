package com.aiafmaster.gpt

import com.google.gson.annotations.SerializedName

data class ChatGPTCompletions(
    @SerializedName("model") val gptModel : String,
    @SerializedName("temperature") val gptTemperature : Double,
    @SerializedName("messages") val gptMessages : List<ChatGPTMessage>
) {
}