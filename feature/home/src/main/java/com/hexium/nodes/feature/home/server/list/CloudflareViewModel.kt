package com.hexium.nodes.feature.home.server.list

import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import com.hexium.nodes.data.preferences.SecurePreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CloudflareViewModel @Inject constructor(
    private val securePrefs: SecurePreferencesRepository
) : ViewModel() {

    fun saveCookies(cookies: String) {
        securePrefs.setCookies(cookies)
    }
}
