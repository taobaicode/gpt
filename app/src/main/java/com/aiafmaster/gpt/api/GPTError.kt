package com.aiafmaster.gpt.api

import com.google.gson.annotations.SerializedName

data class GPTError (
    @SerializedName("message") val gptMessage : String,
    @SerializedName("type") val gptType : String,
    @SerializedName("param") val gptParams : String?,
    @SerializedName("code") val gptCode: String?
    ){
}