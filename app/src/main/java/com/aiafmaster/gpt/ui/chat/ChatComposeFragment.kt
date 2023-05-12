package com.aiafmaster.gpt.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aiafmaster.gpt.ChatViewModel
import com.aiafmaster.gpt.databinding.FragmentChatComposeBinding
import com.aiafmaster.gpt.db.DBManagerImpl
import com.aiafmaster.gpt.repository.ChatGPTRepository
import com.aiafmaster.gpt.repository.SettingsRepository
import com.google.accompanist.themeadapter.material.MdcTheme

class ChatComposeFragment: Fragment() {
    private val chatViewModel: ChatViewModel by lazy {
        val dbManager= DBManagerImpl(requireContext().applicationContext);
        ViewModelProvider(requireActivity(),
            ChatViewModel.Factory(
                dbManager,
                SettingsRepository(dbManager)
            ))[ChatViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChatComposeBinding.inflate(inflater, container, false)
        // binding.composeView.consumeWindowInsets = false
        binding.composeView.setContent {
            MdcTheme {
                chatCompose(chatViewModel)
            }
        }
        return binding.root
    }
}