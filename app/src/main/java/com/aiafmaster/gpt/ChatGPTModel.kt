package com.aiafmaster.gpt

import com.google.gson.annotations.SerializedName

data class ChatGPTModel(
    @SerializedName("id") var gptId : String,
    @SerializedName("object") var gptObject : String,
    @SerializedName("created") var gptCreated : Int,
    @SerializedName("owned_by") var gptOwnedBy : String,
    @SerializedName("permission") var gptPermissions : List<GPTPermission>,
    @SerializedName("root") var gptRoot : String,
    @SerializedName("parent") var gptParent : String?
    )