package com.aiafmaster.gpt

import com.google.gson.annotations.SerializedName

data class ChatGPTUsage (
    @SerializedName("prompt_tokens") val promptTokens : Int
){}