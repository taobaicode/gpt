package com.aiafmaster.gpt.repository

import com.aiafmaster.gpt.db.DBManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class SettingsRepository(
    private val dbManager: DBManager,
    coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher=Dispatchers.IO) {

    val apiKey: StateFlow<String> = dbManager.settings.map {
        val k = it.filter { settings ->
            println("VC got ${settings.key} : ${settings.value}")
            settings.key == API_KEY && settings.value.isNotEmpty()
        }.map { settings ->
            settings.value
        }
        println("VC k size ${k.size}, ${k.first()}")
        if (k.isEmpty()) "" else k.first()
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = ""
    )

    companion object {
        const val API_KEY = "api_key";
    }

    suspend fun updateApiKey(apiKey: String) {
        withContext(dispatcher) {
            dbManager.insertOrUpdateSetting(API_KEY, apiKey)
        }
    }
}