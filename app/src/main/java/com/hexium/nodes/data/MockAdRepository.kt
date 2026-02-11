package com.hexium.nodes.data

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAdRepository @Inject constructor(
    private val prefs: SharedPreferences
) : AdRepository {

    private val MAX_ADS = 50
    private val REGEN_TIME_MS = 24 * 60 * 60 * 1000L // 24 hours

    override suspend fun getAvailableAds(): Int {
        return withContext(Dispatchers.IO) {
            cleanUpExpiredAds()
            val history = getHistoryInternal()
            return@withContext MAX_ADS - history.size
        }
    }

    override suspend fun getMaxAds(): Int {
        return MAX_ADS
    }

    override suspend fun getCredits(): Float = withContext(Dispatchers.IO) {
        // Retrieve stored credits (as string to preserve precision, or long scaled by 100)
        // Using float for now as requested, formatted to 2 decimal places in UI
        return@withContext prefs.getFloat("credits", 0.00f)
    }

    override suspend fun watchAd(): Boolean = withContext(Dispatchers.IO) {
        cleanUpExpiredAds()
        val history = getHistoryInternal().toMutableSet()
        if (history.size >= MAX_ADS) {
            return@withContext false
        }

        // Add current timestamp
        val now = System.currentTimeMillis()
        history.add(now.toString())
        prefs.edit().putStringSet("ad_history", history).apply()

        // Increment credits
        val currentCredits = prefs.getFloat("credits", 0.00f)
        val reward = 1.00f // Placeholder reward
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
        if (username == "admin" && password == "1234") {
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("username", username)
                .apply()
            return@withContext true
        }
        return@withContext false
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("username")
            .apply()
    }

    override suspend fun getUsername(): String? = withContext(Dispatchers.IO) {
        return@withContext prefs.getString("username", null)
    }

    private fun getHistoryInternal(): Set<String> {
        return prefs.getStringSet("ad_history", emptySet()) ?: emptySet()
    }

    private fun cleanUpExpiredAds() {
        val history = getHistoryInternal()
        val now = System.currentTimeMillis()
        val validAds = history.filter {
            val timestamp = it.toLongOrNull() ?: 0L
            (now - timestamp) < REGEN_TIME_MS
        }.toSet()

        if (validAds.size != history.size) {
            prefs.edit().putStringSet("ad_history", validAds).apply()
        }
    }
}
