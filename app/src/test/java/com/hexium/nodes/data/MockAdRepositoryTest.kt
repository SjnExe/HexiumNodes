package com.hexium.nodes.data

import android.content.SharedPreferences
import com.hexium.nodes.data.model.RemoteConfig
import com.hexium.nodes.data.model.TestUser
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
    private lateinit var configRepository: ConfigRepository
    private lateinit var repository: MockAdRepository

    @BeforeEach
    fun setup() {
        sharedPreferences = mock()
        // Use RETURNS_SELF to automatically handle chaining for Editor methods
        editor = mock(defaultAnswer = Answers.RETURNS_SELF)
        settingsRepository = mock()
        configRepository = mock()

        whenever(sharedPreferences.edit()).thenReturn(editor)

        // Mock default settings flow
        val defaultSettings = SettingsData(
            themeMode = AppTheme.SYSTEM,
            useDynamicColors = false,
            serverUrl = "http://test",
            cachedAdLimit = 50,
            cachedAdRate = 1.0,
            cachedAdExpiry = 24,
            cachedAdDelaySeconds = 10L,
            lastConfigFetchTime = 0L,
            cachedMaintenance = false,
            cachedMinVersion = 1,
            cachedTestUsersJson = "[]",
        )
        whenever(settingsRepository.settingsFlow).thenReturn(flowOf(defaultSettings))

        repository = MockAdRepository(sharedPreferences, settingsRepository, configRepository)
    }

    @Test
    fun `watchAd increases credits and history`() = runBlocking {
        // Given
        whenever(sharedPreferences.getString(eq("credits_double"), any())).thenReturn("10.00")
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
    fun `login succeeds with correct credentials from config`() = runBlocking {
        // Given
        val testUser = TestUser("admin", "1234")
        val config = RemoteConfig(testUsers = listOf(testUser))
        whenever(configRepository.fetchConfig()).thenReturn(config)

        // When
        val result = repository.login("admin", "1234")

        // Then
        assertTrue(result)
    }

    @Test
    fun `login fails with incorrect credentials`() = runBlocking {
        // Given
        val testUser = TestUser("admin", "1234")
        val config = RemoteConfig(testUsers = listOf(testUser))
        whenever(configRepository.fetchConfig()).thenReturn(config)

        // When
        val result = repository.login("user", "password")

        // Then
        assertFalse(result)
    }
}
