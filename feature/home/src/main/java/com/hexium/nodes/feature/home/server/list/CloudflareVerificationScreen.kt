package com.hexium.nodes.feature.home.server.list

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.common.Constants
import com.hexium.nodes.data.preferences.SecurePreferencesRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudflareVerificationScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CloudflareViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        // Clear all cookies to ensure a fresh session and prevent 403 loops from stale cookies
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloudflare Verification") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            CloudflareWebView(
                url = "https://panel.hexiumnodes.cloud/",
                onCookiesDetected = { cookies ->
                    viewModel.saveCookies(cookies)
                    onSuccess()
                },
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CloudflareWebView(
    url: String,
    onCookiesDetected: (String) -> Unit,
) {
    AndroidView(
        factory = { context ->
            val webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = Constants.USER_AGENT

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val cookies = CookieManager.getInstance().getCookie(url)
                        // Ensure we have a valid cookie string and specifically look for cf_clearance if possible
                        // However, checking for non-null/non-empty is a good start after clearing.
                        if (!cookies.isNullOrBlank() && cookies.contains("cf_clearance")) {
                            // Pass all cookies; the network module will filter what it needs.
                            onCookiesDetected(cookies)
                        }
                    }
                }
                loadUrl(url)
            }
            webView
        },
        modifier = Modifier.fillMaxSize(),
    )
}
