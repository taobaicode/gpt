package com.aiafmaster.gpt

import java.util.Date

data class ChatData (
    var content : String,
    var bot : Boolean,
    var timeStamp: Date
)