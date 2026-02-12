package com.hexium.nodes.data

import android.content.SharedPreferences
import com.hexium.nodes.data.preferences.AppTheme
import com.hexium.nodes.data.preferences.SettingsData
import com.hexium.nodes.data.preferences.SettingsRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Answers
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MockAdRepositoryTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var repository: MockAdRepository

    @BeforeEach
    fun setup() {
        sharedPreferences = mock()
        // Use RETURNS_SELF to automatically handle chaining for Editor methods
        editor = mock(defaultAnswer = Answers.RETURNS_SELF)
        settingsRepository = mock()

        whenever(sharedPreferences.edit()).thenReturn(editor)

        // Mock default settings flow
        val defaultSettings = SettingsData(
            themeMode = AppTheme.SYSTEM,
            useDynamicColors = false,
            serverUrl = "http://test",
            devAdLimit = 50,
            devAdRate = 1.0f,
            devAdExpiry = 24,
        )
        whenever(settingsRepository.settingsFlow).thenReturn(flowOf(defaultSettings))

        repository = MockAdRepository(sharedPreferences, settingsRepository)
    }

    @Test
    fun `watchAd increases credits and history`() = runBlocking {
        // Given
        whenever(sharedPreferences.getFloat(eq("credits"), any())).thenReturn(10.0f)
        whenever(sharedPreferences.getStringSet(eq("ad_history"), any())).thenReturn(emptySet())

        // When
        val result = repository.watchAd()

        // Then
        assertTrue(result)
    }

    @Test
    fun `getAvailableAds returns max when empty history`() = runBlocking {
        // Given
        whenever(sharedPreferences.getStringSet(eq("ad_history"), any())).thenReturn(emptySet())

        // When
        val available = repository.getAvailableAds()

        // Then
        assertEquals(50, available)
    }

    @Test
    fun `login succeeds with correct credentials`() = runBlocking {
        val result = repository.login("admin", "1234")
        assertTrue(result)
    }

    @Test
    fun `login fails with incorrect credentials`() = runBlocking {
        val result = repository.login("user", "password")
        assertFalse(result)
    }
}
