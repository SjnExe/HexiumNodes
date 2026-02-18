package com.hexium.nodes.data

import com.hexium.nodes.core.common.util.LogUtils
import com.hexium.nodes.data.model.RemoteConfig
import com.hexium.nodes.data.preferences.SettingsRepository
import com.hexium.nodes.data.remote.ConfigService
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val configService: ConfigService,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun fetchConfig(force: Boolean = false): RemoteConfig? {
        val settings = settingsRepository.settingsFlow.first()
        val currentTime = System.currentTimeMillis()
        // Cache for 5 minutes (300,000 ms) to optimize API usage
        if (!force && currentTime - settings.lastConfigFetchTime < 300000) {
            val gson = com.google.gson.Gson()
            val testUsersType = object : com.google.gson.reflect.TypeToken<List<com.hexium.nodes.data.model.TestUser>>() {}.type
            val testUsers: List<com.hexium.nodes.data.model.TestUser> = try {
                gson.fromJson(settings.cachedTestUsersJson, testUsersType)
            } catch (e: Exception) {
                emptyList()
            }

            return RemoteConfig(
                devAdLimit = settings.cachedAdLimit,
                devAdRate = settings.cachedAdRate,
                devAdExpiry = settings.cachedAdExpiry,
                devAdDelaySeconds = settings.cachedAdDelaySeconds,
                maintenance = settings.cachedMaintenance,
                minVersion = settings.cachedMinVersion,
                testUsers = testUsers,
            )
        }

        val serverUrl = settings.serverUrl
        // GitHub Pages deploys the contents of the 'config' folder to the root of the site.
        // So 'config.json' is accessible at the base URL + "config.json".

        val fullUrl = if (serverUrl.endsWith("/")) {
            "${serverUrl}config.json"
        } else {
            "$serverUrl/config.json"
        }

        return try {
            val config = configService.getConfig(fullUrl)

            // Cache the critical values immediately
            settingsRepository.updateFromRemoteConfig(config = config)

            config
        } catch (e: Exception) {
            LogUtils.e("ConfigRepository", "Fetch failed for $fullUrl", e)
            null
        }
    }
}
