package com.hexium.nodes.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexium.nodes.BuildConfig
import com.hexium.nodes.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    // Back button logic
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            ListItem(
                headlineContent = { Text("Dark Theme") },
                trailingContent = {
                    Switch(
                        checked = uiState.isDarkTheme,
                        onCheckedChange = { viewModel.toggleDarkTheme(it) }
                    )
                }
            )

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                ListItem(
                    headlineContent = { Text("Dynamic Colors (Material You)") },
                    trailingContent = {
                        Switch(
                            checked = uiState.useDynamicColors,
                            onCheckedChange = { viewModel.toggleDynamicColors(it) }
                        )
                    }
                )
            }

            if (BuildConfig.FLAVOR == "dev") {
                HorizontalDivider()
                ListItem(headlineContent = { Text("Developer Options", color = MaterialTheme.colorScheme.primary) })

                val context = androidx.compose.ui.platform.LocalContext.current
                ListItem(
                    headlineContent = { Text("Open Network Inspector (Chucker)") },
                    modifier = Modifier.clickable {
                        try {
                            // Use reflection to avoid compile-time dependency on Chucker classes
                            // which might vary between debug and release (no-op) artifacts
                            val chuckerClass = Class.forName("com.chuckerteam.chucker.api.Chucker")
                            val method = chuckerClass.getMethod("getLaunchIntent", android.content.Context::class.java)
                            val intent = method.invoke(null, context) as android.content.Intent
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )

                var serverUrl by remember { mutableStateOf(uiState.serverUrl) }
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = {
                        serverUrl = it
                        viewModel.updateServerUrl(it)
                    },
                    label = { Text("Server URL") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    }
}
