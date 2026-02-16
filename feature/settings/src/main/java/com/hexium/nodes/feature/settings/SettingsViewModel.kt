package com.hexium.nodes.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.AdRepository
import com.hexium.nodes.data.PterodactylRepository
import com.hexium.nodes.data.preferences.AppTheme
import com.hexium.nodes.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val adRepository: AdRepository,
    private val pterodactylRepository: PterodactylRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.value = _uiState.value.copy(
                    themeMode = settings.themeMode,
                    useDynamicColors = settings.useDynamicColors,
                    serverUrl = settings.serverUrl,
                    cachedAdLimit = settings.cachedAdLimit,
                    cachedAdRate = settings.cachedAdRate,
                    cachedAdExpiry = settings.cachedAdExpiry,
                )
            }
        }

        viewModelScope.launch {
            val loggedIn = adRepository.isLoggedIn()
            val username = adRepository.getUsername()
            val email = adRepository.getEmail()

            val apiKey = pterodactylRepository.getApiKey()

            _uiState.value = _uiState.value.copy(
                isLoggedIn = loggedIn,
                username = username,
                email = email,
                pterodactylApiKey = apiKey,
            )
        }
    }

    fun setThemeMode(mode: AppTheme) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun toggleDynamicColors(useDynamic: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColors(useDynamic) }
    }

    fun updateApiKey(key: String) {
        pterodactylRepository.setApiKey(key)
        _uiState.value = _uiState.value.copy(pterodactylApiKey = key)
    }

    fun logout() {
        viewModelScope.launch {
            adRepository.logout()
            _uiState.value = _uiState.value.copy(isLoggedIn = false)
        }
    }
}

data class SettingsUiState(
    val themeMode: AppTheme = AppTheme.SYSTEM,
    val useDynamicColors: Boolean = false,
    val serverUrl: String = "https://SjnExe.github.io/HexiumNodes",
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val cachedAdLimit: Int = 50,
    val cachedAdRate: Double = 1.0,
    val cachedAdExpiry: Int = 24,
    val pterodactylApiKey: String? = null,
)
