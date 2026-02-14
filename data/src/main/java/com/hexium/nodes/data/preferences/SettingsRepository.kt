package com.hexium.nodes.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hexium.nodes.data.model.RemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
}

data class SettingsData(
    val themeMode: AppTheme,
    val useDynamicColors: Boolean,
    val serverUrl: String,
    val cachedAdLimit: Int,
    val cachedAdRate: Double,
    val cachedAdExpiry: Int,
    val cachedAdDelaySeconds: Long,
)

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")

        // Read-only server URL
        val SERVER_URL = stringPreferencesKey("server_url")

        // Cached values from server
        val CACHED_AD_LIMIT = intPreferencesKey("cached_ad_limit")
        val CACHED_AD_RATE_STRING = stringPreferencesKey("cached_ad_rate_str")
        val CACHED_AD_EXPIRY = intPreferencesKey("cached_ad_expiry")
        val CACHED_AD_DELAY = longPreferencesKey("cached_ad_delay")
    }

    val settingsFlow: Flow<SettingsData> = dataStore.data.map { preferences ->
        val themeModeName = preferences[THEME_MODE] ?: AppTheme.SYSTEM.name
        val themeMode = try {
            AppTheme.valueOf(themeModeName)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM
        }

        val rateStr = preferences[CACHED_AD_RATE_STRING] ?: "1.0"
        val rate = rateStr.toDoubleOrNull() ?: 1.0

        SettingsData(
            themeMode = themeMode,
            useDynamicColors = preferences[DYNAMIC_COLORS] ?: false,
            serverUrl = preferences[SERVER_URL] ?: "https://SjnExe.github.io/HexiumNodes",
            cachedAdLimit = preferences[CACHED_AD_LIMIT] ?: 20,
            cachedAdRate = rate,
            cachedAdExpiry = preferences[CACHED_AD_EXPIRY] ?: 24,
            cachedAdDelaySeconds = preferences[CACHED_AD_DELAY] ?: 10L,
        )
    }

    suspend fun setThemeMode(mode: AppTheme) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    suspend fun setDynamicColors(useDynamic: Boolean) {
        dataStore.edit { it[DYNAMIC_COLORS] = useDynamic }
    }

    suspend fun setServerUrl(url: String) {
        dataStore.edit { it[SERVER_URL] = url }
    }

    // Batch update from remote config
    suspend fun updateFromRemoteConfig(config: RemoteConfig) {
        dataStore.edit {
            it[CACHED_AD_LIMIT] = config.devAdLimit
            it[CACHED_AD_RATE_STRING] = config.devAdRate.toString()
            it[CACHED_AD_EXPIRY] = config.devAdExpiry
            it[CACHED_AD_DELAY] = config.devAdDelaySeconds
        }
    }
}
