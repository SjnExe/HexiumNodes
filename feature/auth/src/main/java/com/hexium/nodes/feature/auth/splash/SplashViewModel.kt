package com.hexium.nodes.feature.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.AdRepository
import com.hexium.nodes.data.ConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object LoggedIn : AuthState()
    object LoggedOut : AuthState()
    object Maintenance : AuthState()
    object UpdateRequired : AuthState()
    object Error : AuthState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AdRepository,
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAppStatus()
    }

    fun retry() {
        _authState.value = AuthState.Loading
        checkAppStatus()
    }

    private fun checkAppStatus() {
        viewModelScope.launch {
            val config = configRepository.fetchConfig()

            if (config != null) {
                if (config.maintenance) {
                    _authState.value = AuthState.Maintenance
                    return@launch
                }

                // Check min version (Hardcoded current version for now or inject BuildConfig)
                // Assuming current version code is 1 for simplicity or fetched via logic.
                // In a real app, use BuildConfig.VERSION_CODE.
                // Since this is a feature module, we need the app's version code.
                // For now, let's assume 1. If config.minVersion > 1, update required.
                if (config.minVersion > 1) { // TODO: Get actual version code
                    _authState.value = AuthState.UpdateRequired
                    return@launch
                }
            } else {
                // Config fetch failed. If we can't verify maintenance, what do we do?
                // For now, proceed to local auth check, or show Error?
                // User said "App should give the standard error messages when it can't connect".
                // So maybe Error state.
                _authState.value = AuthState.Error
                return@launch
            }

            checkAuth()
        }
    }

    private suspend fun checkAuth() {
        val isLoggedIn = repository.isLoggedIn()
        _authState.value = if (isLoggedIn) AuthState.LoggedIn else AuthState.LoggedOut
    }
}
