package com.aiafmaster.gpt.api

import com.google.gson.annotations.SerializedName

data class ChatGPTResponse(
    @SerializedName("object") val gptObject: String,
    @SerializedName("data") val gptModel: List<ChatGPTModel>,
    @SerializedName("error") val gptError : GPTError,
    @SerializedName("usage") val gptUsage: ChatGPTUsage,
    @SerializedName("choices") val gptChoices : List<ChatGPTChoices>,
    )