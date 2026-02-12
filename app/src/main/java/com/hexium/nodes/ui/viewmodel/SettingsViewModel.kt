package com.hexium.nodes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.AdRepository
import com.hexium.nodes.data.preferences.AppTheme
import com.hexium.nodes.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val adRepository: AdRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.value = _uiState.value.copy(
                    themeMode = settings.themeMode,
                    useDynamicColors = settings.useDynamicColors,
                    serverUrl = settings.serverUrl,
                    devAdLimit = settings.devAdLimit,
                    devAdRate = settings.devAdRate,
                    devAdExpiry = settings.devAdExpiry
                )
            }
        }

        viewModelScope.launch {
            val loggedIn = adRepository.isLoggedIn()
            val username = adRepository.getUsername()
            val email = adRepository.getEmail()

            _uiState.value = _uiState.value.copy(
                isLoggedIn = loggedIn,
                username = username,
                email = email
            )
        }
    }

    fun setThemeMode(mode: AppTheme) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun toggleDynamicColors(useDynamic: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColors(useDynamic) }
    }

    fun updateServerUrl(url: String) {
        viewModelScope.launch { settingsRepository.setServerUrl(url) }
    }

    fun updateDevAdLimit(limit: Int) {
        viewModelScope.launch { settingsRepository.setDevAdLimit(limit) }
    }

    fun updateDevAdRate(rate: Float) {
        viewModelScope.launch { settingsRepository.setDevAdRate(rate) }
    }

    fun updateDevAdExpiry(hours: Int) {
        viewModelScope.launch { settingsRepository.setDevAdExpiry(hours) }
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
    val serverUrl: String = "https://placeholder.hexium.nodes",
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val devAdLimit: Int = 50,
    val devAdRate: Float = 1.0f,
    val devAdExpiry: Int = 24
)
