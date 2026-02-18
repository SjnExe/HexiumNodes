package com.hexium.nodes.feature.home.server.files

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

class WebInterface(
    private val onEditorReady: () -> Unit,
) {
    @JavascriptInterface
    fun onEditorReady() {
        onEditorReady()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileEditorScreen(
    serverId: String,
    filePath: String,
    onNavigateBack: () -> Unit,
    viewModel: FileEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var webView: WebView? by remember { mutableStateOf(null) }
    var isEditorReady by remember { mutableStateOf(false) }

    LaunchedEffect(serverId, filePath) {
        viewModel.loadFile(serverId, filePath)
    }

    LaunchedEffect(uiState.content, isEditorReady) {
        if (isEditorReady && !uiState.isLoading && uiState.content.isNotEmpty()) {
            // Escape content for JS string
            val safeContent = uiState.content
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")

            webView?.post {
                webView?.evaluateJavascript("setContent(\"$safeContent\", \"$filePath\")", null)
            }
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "File saved successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filePath.substringAfterLast('/')) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        webView?.evaluateJavascript("getContent()") { value ->
                            // Value is returned as a JSON string (e.g., "content"), so strip quotes
                            val content = if (value != null && value.length >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                                value.substring(1, value.length - 1)
                                    .replace("\\n", "\n")
                                    .replace("\\\"", "\"")
                                    .replace("\\\\", "\\")
                            } else {
                                value ?: ""
                            }
                            viewModel.saveFile(content)
                        }
                    }) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSurface)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            } else {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            addJavascriptInterface(
                                WebInterface {
                                    isEditorReady = true
                                },
                                "Android",
                            )

                            webViewClient = WebViewClient()
                            loadUrl("file:///android_asset/editor.html")
                            webView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
