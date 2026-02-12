package com.hexium.nodes.data

interface AdRepository {
    suspend fun getAvailableAds(): Int
    suspend fun getMaxAds(): Int
    suspend fun getAdRewardRate(): Float
    suspend fun getAdExpiryHours(): Int
    suspend fun getCredits(): Float
    suspend fun watchAd(): Boolean
    suspend fun getAdHistory(): List<Long>
    suspend fun isLoggedIn(): Boolean
    suspend fun login(username: String, password: String): Boolean
    suspend fun logout()
    suspend fun getUsername(): String?
    suspend fun getEmail(): String?
}
