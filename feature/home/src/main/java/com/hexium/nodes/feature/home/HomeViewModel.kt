package com.hexium.nodes.feature.home

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.AdRepository
import com.hexium.nodes.feature.home.ads.RewardedAdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AdRepository,
    private val adManager: RewardedAdManager,
) : ViewModel() {

    private val _credits = MutableStateFlow(0.00)
    val credits: StateFlow<Double> = _credits.asStateFlow()

    private val _availableAds = MutableStateFlow(0)
    val availableAds: StateFlow<Int> = _availableAds.asStateFlow()

    private val _maxAds = MutableStateFlow(50)
    val maxAds: StateFlow<Int> = _maxAds.asStateFlow()

    private val _adRate = MutableStateFlow(1.0)
    val adRate: StateFlow<Double> = _adRate.asStateFlow()

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

    // Seconds remaining until next ad
    private val _adCooldownSeconds = MutableStateFlow(0L)
    val adCooldownSeconds: StateFlow<Long> = _adCooldownSeconds.asStateFlow()

    val isAdLoaded: StateFlow<Boolean> = adManager.isAdLoaded

    init {
        checkLoginState()
        adManager.loadAd()
        startCooldownTimer()
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

    private fun startCooldownTimer() {
        viewModelScope.launch {
            while (isActive) {
                if (_isLoggedIn.value) {
                    val nextTime = repository.getNextAdAvailableTime()
                    val now = System.currentTimeMillis()
                    val remaining = ((nextTime - now) / 1000L).coerceAtLeast(0L)
                    _adCooldownSeconds.value = remaining
                }
                delay(1000L) // Update every second
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

                // Refresh cooldown immediately
                val nextTime = repository.getNextAdAvailableTime()
                val now = System.currentTimeMillis()
                _adCooldownSeconds.value = ((nextTime - now) / 1000L).coerceAtLeast(0L)
            }
        }
    }

    fun watchAd(activity: Activity) {
        // Double check cooldown locally before showing
        if (_adCooldownSeconds.value > 0) return

        adManager.showAd(activity) { _ ->
            // User earned reward
            viewModelScope.launch {
                val success = repository.watchAd()
                if (success) {
                    refreshData()
                }
            }
        }
    }
}
