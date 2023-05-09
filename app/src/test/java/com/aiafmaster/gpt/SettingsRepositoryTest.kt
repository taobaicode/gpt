package com.aiafmaster.gpt

import com.aiafmaster.gpt.db.Chat
import com.aiafmaster.gpt.db.DBManager
import com.aiafmaster.gpt.db.Settings
import com.aiafmaster.gpt.repository.SettingsRepository
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SettingsRepositoryTest {
    class TestDBManager: DBManager {
        var _settings: MutableList<Settings> = mutableListOf()
        override val settings: List<Settings> = _settings
        override val chats: List<Chat> = mutableListOf()
    }

    @Test
    fun testFetchApiKey() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val testDBManager = TestDBManager()
        val settingsRepository = SettingsRepository(testDBManager, dispatcher)
        testDBManager._settings.add(Settings(-1, SettingsRepository.API_KEY, "mykey"))
        testDBManager._settings.add(Settings(-1, "otherkey", "mykey1"))
        settingsRepository.fetchAPIKey()
        assertEquals("mykey", settingsRepository.apiKey.value)
        testDBManager._settings.removeAt(0)
        settingsRepository.fetchAPIKey()
        assertTrue(settingsRepository.apiKey.value.isEmpty())
    }
}