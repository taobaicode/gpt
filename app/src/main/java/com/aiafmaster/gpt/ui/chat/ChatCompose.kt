package com.aiafmaster.gpt.ui.chat

import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ChatCompose(chat: String) {
    Surface {
        Text(chat)
    }
}