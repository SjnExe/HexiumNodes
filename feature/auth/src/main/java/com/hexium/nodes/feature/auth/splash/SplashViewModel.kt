package com.hexium.nodes.feature.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.AdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object LoggedIn : AuthState()
    object LoggedOut : AuthState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AdRepository,
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            // Simulate check auth
            val isLoggedIn = repository.isLoggedIn()
            _authState.value = if (isLoggedIn) AuthState.LoggedIn else AuthState.LoggedOut
        }
    }
}
