package com.aiafmaster.gpt.repository

import com.aiafmaster.gpt.db.DBManager
import com.aiafmaster.gpt.db.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class SettingsRepository(private val dbManager: DBManager,
                        private val dispatcher: CoroutineDispatcher=Dispatchers.IO) {
    private val _apiKey = MutableStateFlow<String>("");
    val apiKey: StateFlow<String> = _apiKey

    private var settings: Settings? = null

    companion object {
        const val API_KEY = "api_key";
    }

    suspend fun updateApiKey(apiKey: String) {
        withContext(dispatcher) {
            if (settings == null) {
                dbManager.insertSetting(Settings(-1, API_KEY, apiKey))
            } else {
                settings!!.value = apiKey
                dbManager.updateSetting(settings!!)
            }
            fetchAPIKey()
        }
    }

    suspend fun fetchAPIKey() {
        withContext(dispatcher) {
            val key = dbManager.settings.filter {
                it.key == API_KEY
            }.map { settings= it
                    it.value }
            if (key.isNotEmpty()) {
                _apiKey.emit(key[0])
            } else {
                _apiKey.emit("")
            }
        }
    }
}