package com.aiafmaster.gpt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.aiafmaster.gpt.ChatViewModel
import com.aiafmaster.gpt.databinding.ApiSettingFragmentBinding

class APISettingDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        val bind = ApiSettingFragmentBinding.inflate(inflater, container, false)
        chatViewModel.fetchAPIKey()
        chatViewModel.apiKey.observe(this) {
            bind.editTextAPIKey.setText(it.value)
        }
        bind.okButton.setOnClickListener {
            chatViewModel.setAPIKey(bind.editTextAPIKey.text.toString())
            this.dismiss()
        }
        return bind.root
    }
}