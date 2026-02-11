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

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val SERVER_URL = stringPreferencesKey("server_url")
    }

    val settingsFlow: Flow<SettingsData> = dataStore.data.map { preferences ->
        SettingsData(
            isDarkTheme = preferences[DARK_THEME] ?: true,
            useDynamicColors = preferences[DYNAMIC_COLORS] ?: false,
            serverUrl = preferences[SERVER_URL] ?: "https://api.hexium.nodes"
        )
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { it[DARK_THEME] = isDark }
    }

    suspend fun setDynamicColors(useDynamic: Boolean) {
        dataStore.edit { it[DYNAMIC_COLORS] = useDynamic }
    }

    suspend fun setServerUrl(url: String) {
        dataStore.edit { it[SERVER_URL] = url }
    }
}

data class SettingsData(
    val isDarkTheme: Boolean,
    val useDynamicColors: Boolean,
    val serverUrl: String
)
