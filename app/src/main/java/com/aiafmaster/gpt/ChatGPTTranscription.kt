package com.aiafmaster.gpt

import com.google.gson.annotations.SerializedName

class ChatGPTTranscription (
    @SerializedName("text") val transcription : String)
{
}