package com.aiafmaster.gpt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aiafmaster.gpt.ChatViewModel
import com.aiafmaster.gpt.databinding.ApiSettingFragmentBinding
import kotlinx.coroutines.launch

class APISettingDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        val bind = ApiSettingFragmentBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.apiKey.collect {
                    bind.editTextAPIKey.setText(it)
                    println("Collect API Key $it")
                }
            }
        }
        bind.okButton.setOnClickListener {
            chatViewModel.setAPIKey(bind.editTextAPIKey.text.toString())
            this.dismiss()
        }
        return bind.root
    }
}