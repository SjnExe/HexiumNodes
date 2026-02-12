package com.hexium.nodes.ui.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexium.nodes.BuildConfig
import com.hexium.nodes.data.preferences.AppTheme
import com.hexium.nodes.ui.viewmodel.SettingsViewModel
import com.hexium.nodes.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // User Profile Section (if logged in)
            if (uiState.isLoggedIn) {
                ListItem(
                    headlineContent = { Text(uiState.username ?: "User", fontWeight = FontWeight.Bold) },
                    supportingContent = { Text(uiState.email ?: "No Email") },
                    leadingContent = {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (uiState.username?.firstOrNull() ?: '?').toString().uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                )
                HorizontalDivider()
            }

            // Theme Selection
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            // Segmented Button-like UI for Theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeOption(
                    text = "System",
                    selected = uiState.themeMode == AppTheme.SYSTEM,
                    onClick = { viewModel.setThemeMode(AppTheme.SYSTEM) },
                    modifier = Modifier.weight(1f)
                )
                ThemeOption(
                    text = "Light",
                    selected = uiState.themeMode == AppTheme.LIGHT,
                    onClick = { viewModel.setThemeMode(AppTheme.LIGHT) },
                    modifier = Modifier.weight(1f)
                )
                ThemeOption(
                    text = "Dark",
                    selected = uiState.themeMode == AppTheme.DARK,
                    onClick = { viewModel.setThemeMode(AppTheme.DARK) },
                    modifier = Modifier.weight(1f)
                )
            }

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

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Logout Button (Only if logged in)
            if (uiState.isLoggedIn) {
                ListItem(
                    headlineContent = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        viewModel.logout()
                        onLogout()
                    }
                )
                HorizontalDivider()
            }

            if (BuildConfig.FLAVOR == "dev") {
                ListItem(headlineContent = { Text("Developer Options", color = MaterialTheme.colorScheme.primary) })

                // Dev Configs
                var adLimit by remember { mutableStateOf(uiState.devAdLimit.toString()) }
                var adRate by remember { mutableStateOf(uiState.devAdRate.toString()) }

                ListItem(
                    headlineContent = { Text("Ad Limit") },
                    trailingContent = {
                        OutlinedTextField(
                            value = adLimit,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    adLimit = it
                                    it.toIntOrNull()?.let { limit -> viewModel.updateDevAdLimit(limit) }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text("Ad Rate") },
                    trailingContent = {
                         OutlinedTextField(
                            value = adRate,
                            onValueChange = {
                                adRate = it
                                it.toFloatOrNull()?.let { rate -> viewModel.updateDevAdRate(rate) }
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }
                )


                ListItem(
                    headlineContent = { Text("Open Network Inspector") },
                    modifier = Modifier.clickable {
                        try {
                            val chuckerClass = Class.forName("com.chuckerteam.chucker.api.Chucker")
                            val method = chuckerClass.getMethod("getLaunchIntent", android.content.Context::class.java)
                            val intent = method.invoke(null, context) as android.content.Intent
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )

                // Log Capture Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                val logs = LogUtils.captureLogs()
                                withContext(Dispatchers.Main) {
                                    clipboardManager.setText(AnnotatedString(logs))
                                    Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Copy Logs")
                    }

                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                val path = LogUtils.saveLogsToFile(context)
                                withContext(Dispatchers.Main) {
                                    if (path != null) {
                                        Toast.makeText(context, "Logs saved to $path", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Failed to save logs", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Logs")
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxHeight()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
