package com.aiafmaster.gpt.api

import com.google.gson.annotations.SerializedName

data class ChatGPTUsage (
    @SerializedName("prompt_tokens") val promptTokens : Int
)