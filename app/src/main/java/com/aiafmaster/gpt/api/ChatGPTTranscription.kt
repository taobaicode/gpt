package com.aiafmaster.gpt.api

import com.google.gson.annotations.SerializedName

class ChatGPTTranscription (
    @SerializedName("text") val transcription : String)