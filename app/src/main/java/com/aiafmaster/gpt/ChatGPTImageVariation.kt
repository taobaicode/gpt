package com.aiafmaster.gpt

import com.google.gson.annotations.SerializedName

class ChatGPTImageVariation(
    @SerializedName("created") val created: Long,
    @SerializedName("data") val images: List<ChatGPTUrl>
)

class ChatGPTUrl(
    @SerializedName("url") val url: String
)

class ChatGPTImageVariationResult(
    val success: Boolean,
    val errorMessage: String,
    val urls: List<String>)