package com.hexium.nodes.data

import android.content.SharedPreferences
import com.hexium.nodes.data.preferences.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAdRepository @Inject constructor(
    private val prefs: SharedPreferences,
    private val settingsRepository: SettingsRepository,
) : AdRepository {

    override suspend fun getAvailableAds(): Int {
        return withContext(Dispatchers.IO) {
            val maxAds = getMaxAds()
            cleanUpExpiredAds()
            val history = getHistoryInternal()
            return@withContext maxAds - history.size
        }
    }

    override suspend fun getMaxAds(): Int = withContext(Dispatchers.IO) {
        return@withContext settingsRepository.settingsFlow.first().devAdLimit
    }

    override suspend fun getAdRewardRate(): Float = withContext(Dispatchers.IO) {
        return@withContext settingsRepository.settingsFlow.first().devAdRate
    }

    override suspend fun getAdExpiryHours(): Int = withContext(Dispatchers.IO) {
        return@withContext settingsRepository.settingsFlow.first().devAdExpiry
    }

    override suspend fun getCredits(): Float = withContext(Dispatchers.IO) {
        return@withContext prefs.getFloat("credits", 0.00f)
    }

    override suspend fun watchAd(): Boolean = withContext(Dispatchers.IO) {
        val maxAds = getMaxAds()
        cleanUpExpiredAds()
        val history = getHistoryInternal().toMutableSet()
        if (history.size >= maxAds) {
            return@withContext false
        }

        // Add current timestamp
        val now = System.currentTimeMillis()
        history.add(now.toString())
        prefs.edit().putStringSet("ad_history", history).apply()

        // Increment credits
        val currentCredits = prefs.getFloat("credits", 0.00f)
        val reward = settingsRepository.settingsFlow.first().devAdRate
        prefs.edit().putFloat("credits", currentCredits + reward).apply()

        return@withContext true
    }

    override suspend fun getAdHistory(): List<Long> = withContext(Dispatchers.IO) {
        cleanUpExpiredAds()
        return@withContext getHistoryInternal().map { it.toLong() }.sortedDescending()
    }

    override suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        return@withContext prefs.getBoolean("is_logged_in", false)
    }

    override suspend fun login(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        // Mock validation
        if ((username == "admin" || username == "admin@email.com") && password == "1234") {
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("username", "admin")
                .putString("email", "admin@email.com")
                .apply()
            return@withContext true
        }
        return@withContext false
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("username")
            .remove("email")
            .apply()
    }

    override suspend fun getUsername(): String? = withContext(Dispatchers.IO) {
        return@withContext prefs.getString("username", null)
    }

    override suspend fun getEmail(): String? = withContext(Dispatchers.IO) {
        return@withContext prefs.getString("email", null)
    }

    private fun getHistoryInternal(): Set<String> {
        return prefs.getStringSet("ad_history", emptySet()) ?: emptySet()
    }

    private suspend fun cleanUpExpiredAds() {
        val history = getHistoryInternal()
        val now = System.currentTimeMillis()
        val expiryHours = settingsRepository.settingsFlow.first().devAdExpiry
        val expiryMs = expiryHours * 60 * 60 * 1000L

        val validAds = history.filter {
            val timestamp = it.toLongOrNull() ?: 0L
            (now - timestamp) < expiryMs
        }.toSet()

        if (validAds.size != history.size) {
            prefs.edit().putStringSet("ad_history", validAds).apply()
        }
    }
}
