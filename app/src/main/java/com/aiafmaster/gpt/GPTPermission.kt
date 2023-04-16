package com.aiafmaster.gpt

import retrofit2.http.Field

data class GPTPermission(
    @Field("id") val gptId : String,
    @Field("object") val gptObject : String,
    @Field("created") val gptCreated : Int,
    @Field("allow_create_engine") val gptAllowCreateEngine : Boolean
) {
}