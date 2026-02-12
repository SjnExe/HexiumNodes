package com.hexium.nodes.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val SERVER_URL = stringPreferencesKey("server_url")
        val DEV_AD_LIMIT = intPreferencesKey("dev_ad_limit")
        val DEV_AD_RATE = floatPreferencesKey("dev_ad_rate")
        val DEV_AD_EXPIRY = intPreferencesKey("dev_ad_expiry")
    }

    val settingsFlow: Flow<SettingsData> = dataStore.data.map { preferences ->
        val themeModeName = preferences[THEME_MODE] ?: AppTheme.SYSTEM.name
        val themeMode = try {
            AppTheme.valueOf(themeModeName)
        } catch (e: IllegalArgumentException) {
            AppTheme.SYSTEM
        }

        SettingsData(
            themeMode = themeMode,
            useDynamicColors = preferences[DYNAMIC_COLORS] ?: false,
            serverUrl = preferences[SERVER_URL] ?: "https://placeholder.hexium.nodes",
            devAdLimit = preferences[DEV_AD_LIMIT] ?: 50,
            devAdRate = preferences[DEV_AD_RATE] ?: 1.0f,
            devAdExpiry = preferences[DEV_AD_EXPIRY] ?: 24
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

    suspend fun setDevAdLimit(limit: Int) {
        dataStore.edit { it[DEV_AD_LIMIT] = limit }
    }

    suspend fun setDevAdRate(rate: Float) {
        dataStore.edit { it[DEV_AD_RATE] = rate }
    }

    suspend fun setDevAdExpiry(hours: Int) {
        dataStore.edit { it[DEV_AD_EXPIRY] = hours }
    }
}

data class SettingsData(
    val themeMode: AppTheme,
    val useDynamicColors: Boolean,
    val serverUrl: String,
    val devAdLimit: Int,
    val devAdRate: Float,
    val devAdExpiry: Int
)
