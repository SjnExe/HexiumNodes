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
    private val configRepository: ConfigRepository,
) : AdRepository {

    companion object {
        private const val MAX_BALANCE = 1000000.0 // Max balance limit
    }

    override suspend fun getAvailableAds(): Int {
        return withContext(Dispatchers.IO) {
            val maxAds = getMaxAds()
            cleanUpExpiredAds()
            val history = getHistoryInternal()
            return@withContext maxAds - history.size
        }
    }

    override suspend fun getMaxAds(): Int = withContext(Dispatchers.IO) {
        return@withContext settingsRepository.settingsFlow.first().cachedAdLimit
    }

    override suspend fun getAdRewardRate(): Double = withContext(Dispatchers.IO) {
        return@withContext settingsRepository.settingsFlow.first().cachedAdRate
    }

    override suspend fun getAdExpiryHours(): Int = withContext(Dispatchers.IO) {
        return@withContext settingsRepository.settingsFlow.first().cachedAdExpiry
    }

    override suspend fun getAdWatchDelaySeconds(): Long = withContext(Dispatchers.IO) {
        return@withContext settingsRepository.settingsFlow.first().cachedAdDelaySeconds
    }

    override suspend fun getNextAdAvailableTime(): Long = withContext(Dispatchers.IO) {
        val lastWatch = prefs.getLong("last_ad_watch_time", 0L)
        val delaySec = getAdWatchDelaySeconds()
        return@withContext lastWatch + (delaySec * 1000L)
    }

    override suspend fun getCredits(): Double = withContext(Dispatchers.IO) {
        val creditsStr = prefs.getString("credits_double", "0.00") ?: "0.00"
        return@withContext creditsStr.toDoubleOrNull() ?: 0.0
    }

    override suspend fun watchAd(): Boolean = withContext(Dispatchers.IO) {
        // Enforce Delay
        val now = System.currentTimeMillis()
        val nextAvailable = getNextAdAvailableTime()
        if (now < nextAvailable) {
            return@withContext false // Too soon
        }

        val maxAds = getMaxAds()
        cleanUpExpiredAds()
        val history = getHistoryInternal().toMutableSet()
        if (history.size >= maxAds) {
            return@withContext false
        }

        // Check Max Balance
        val currentCredits = getCredits()
        if (currentCredits >= MAX_BALANCE) {
            return@withContext false
        }

        val reward = getAdRewardRate()
        if (reward <= 0) {
            return@withContext false // Refuse negative or zero rewards
        }

        // Add current timestamp
        history.add(now.toString())
        prefs.edit()
            .putStringSet("ad_history", history)
            .putLong("last_ad_watch_time", now) // Update last watch time
            .apply()

        // Increment credits
        val newCredits = currentCredits + reward
        prefs.edit().putString("credits_double", newCredits.toString()).apply()

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
        // Fetch remote config for validation
        val config = configRepository.fetchConfig()

        // Fallback to hardcoded users if fetch fails or for local dev convenience
        val testUsers = config?.testUsers ?: listOf(
            com.hexium.nodes.data.model.TestUser("testuser", "password123"),
            com.hexium.nodes.data.model.TestUser("admin", "1234"),
        )

        val validUser = testUsers.find {
            (it.username == username) && it.password == password
        }

        if (validUser != null) {
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("username", validUser.username)
                .putString("email", validUser.username) // Mock email as username
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

    private fun getHistoryInternal(): Set<String> = prefs.getStringSet("ad_history", emptySet()) ?: emptySet()

    private suspend fun cleanUpExpiredAds() {
        val history = getHistoryInternal()
        val now = System.currentTimeMillis()
        val expiryHours = getAdExpiryHours()
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
