package com.hexium.nodes.data.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    companion object {
        private const val KEY_API_KEY = "pterodactyl_api_key"
        private const val KEY_COOKIES = "pterodactyl_cookies"
    }

    fun getApiKey(): String? = sharedPreferences.getString(KEY_API_KEY, null)

    fun setApiKey(apiKey: String) {
        sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    fun clearApiKey() {
        sharedPreferences.edit().remove(KEY_API_KEY).apply()
    }

    fun getCookies(): String? = sharedPreferences.getString(KEY_COOKIES, null)

    fun setCookies(cookies: String) {
        sharedPreferences.edit().putString(KEY_COOKIES, cookies).apply()
    }

    fun clearCookies() {
        sharedPreferences.edit().remove(KEY_COOKIES).apply()
    }
}
