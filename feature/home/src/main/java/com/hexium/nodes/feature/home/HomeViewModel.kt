package com.hexium.nodes.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.AdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AdRepository,
) : ViewModel() {

    private val _credits = MutableStateFlow(0.00f)
    val credits: StateFlow<Float> = _credits.asStateFlow()

    private val _availableAds = MutableStateFlow(0)
    val availableAds: StateFlow<Int> = _availableAds.asStateFlow()

    private val _maxAds = MutableStateFlow(50)
    val maxAds: StateFlow<Int> = _maxAds.asStateFlow()

    private val _adRate = MutableStateFlow(1.0f)
    val adRate: StateFlow<Float> = _adRate.asStateFlow()

    private val _adExpiryHours = MutableStateFlow(24)
    val adExpiryHours: StateFlow<Int> = _adExpiryHours.asStateFlow()

    private val _history = MutableStateFlow<List<Long>>(emptyList())
    val history: StateFlow<List<Long>> = _history.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    init {
        checkLoginState()
    }

    private fun checkLoginState() {
        viewModelScope.launch {
            _isLoggedIn.value = repository.isLoggedIn()
            if (_isLoggedIn.value) {
                _username.value = repository.getUsername()
                refreshData()
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginError.value = null
            val success = repository.login(username, password)
            if (success) {
                _isLoggedIn.value = true
                _username.value = username
                refreshData()
            } else {
                _loginError.value = "Invalid credentials"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedIn.value = false
            _username.value = null
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            if (_isLoggedIn.value) {
                _credits.value = repository.getCredits()
                _availableAds.value = repository.getAvailableAds()
                _maxAds.value = repository.getMaxAds()
                _history.value = repository.getAdHistory()
                _adRate.value = repository.getAdRewardRate()
                _adExpiryHours.value = repository.getAdExpiryHours()
            }
        }
    }

    fun watchAd() {
        viewModelScope.launch {
            val success = repository.watchAd()
            if (success) {
                refreshData()
            }
        }
    }
}
