package com.hexium.nodes.data

import com.hexium.nodes.data.model.RemoteConfig
import com.hexium.nodes.data.preferences.SettingsRepository
import com.hexium.nodes.data.remote.ConfigService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val configService: ConfigService,
    private val settingsRepository: SettingsRepository
) {
    suspend fun fetchConfig(): RemoteConfig? {
        val serverUrl = settingsRepository.settingsFlow.first().serverUrl
        // Ensure serverUrl ends with / or handle it.
        // If serverUrl is "https://user.github.io/repo", we need to append "/config/config.json"
        // But Retrofit @Url replaces the whole path if it is absolute, or appends if relative?
        // Actually, if I pass the full URL to @Url, it ignores base URL.

        val fullUrl = if (serverUrl.endsWith("/")) {
            "${serverUrl}config/config.json"
        } else {
            "$serverUrl/config/config.json"
        }

        return try {
            configService.getConfig(fullUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
