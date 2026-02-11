package com.hexium.nodes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.value = SettingsUiState(
                    isDarkTheme = settings.isDarkTheme,
                    useDynamicColors = settings.useDynamicColors,
                    serverUrl = settings.serverUrl
                )
            }
        }
    }

    fun toggleDarkTheme(isDark: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkTheme(isDark) }
    }

    fun toggleDynamicColors(useDynamic: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColors(useDynamic) }
    }

    fun updateServerUrl(url: String) {
        viewModelScope.launch { settingsRepository.setServerUrl(url) }
    }

    fun launchChucker() {
        // Implementation to launch Chucker Intent
    }
}

data class SettingsUiState(
    val isDarkTheme: Boolean = true,
    val useDynamicColors: Boolean = false,
    val serverUrl: String = "https://api.hexium.nodes"
)
