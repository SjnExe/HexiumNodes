package com.hexium.nodes.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
        // Deprecated: used for migration if needed, but we'll default to SYSTEM
        val DARK_THEME = booleanPreferencesKey("dark_theme")

        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val SERVER_URL = stringPreferencesKey("server_url")
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
            serverUrl = preferences[SERVER_URL] ?: "https://placeholder.hexium.nodes"
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
}

data class SettingsData(
    val themeMode: AppTheme,
    val useDynamicColors: Boolean,
    val serverUrl: String
)
