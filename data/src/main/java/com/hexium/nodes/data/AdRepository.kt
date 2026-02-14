package com.hexium.nodes.data

interface AdRepository {
    suspend fun getAvailableAds(): Int
    suspend fun getMaxAds(): Int
    suspend fun getAdRewardRate(): Double
    suspend fun getAdExpiryHours(): Int
    suspend fun getAdWatchDelaySeconds(): Long
    suspend fun getNextAdAvailableTime(): Long
    suspend fun getCredits(): Double
    suspend fun watchAd(): Boolean
    suspend fun getAdHistory(): List<Long>
    suspend fun isLoggedIn(): Boolean
    suspend fun login(username: String, password: String): Boolean
    suspend fun logout()
    suspend fun getUsername(): String?
    suspend fun getEmail(): String?
}
