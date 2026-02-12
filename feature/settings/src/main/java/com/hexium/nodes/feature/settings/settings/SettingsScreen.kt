package com.hexium.nodes.feature.settings.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.common.util.LogUtils
import com.hexium.nodes.core.ui.R
import com.hexium.nodes.data.preferences.AppTheme
import com.hexium.nodes.feature.settings.BuildConfig
import com.hexium.nodes.feature.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = androidx.compose.ui.platform.LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Pre-fetch strings to avoid using context.getString inside callbacks (Lint requirement)
    val logsCopiedMsg = stringResource(R.string.logs_copied)
    val logsSavedMsgTemplate = stringResource(R.string.logs_saved)
    val logsSaveFailedMsg = stringResource(R.string.logs_save_failed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // User Profile Section (if logged in)
            if (uiState.isLoggedIn) {
                ListItem(
                    headlineContent = { Text(uiState.username ?: stringResource(R.string.user), fontWeight = FontWeight.Bold) },
                    supportingContent = { Text(uiState.email ?: stringResource(R.string.no_email)) },
                    leadingContent = {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (uiState.username?.firstOrNull() ?: '?').toString().uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    },
                )
                HorizontalDivider()

                ListItem(
                    headlineContent = { Text(stringResource(R.string.logout), color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        viewModel.logout()
                        onLogout()
                    },
                )
                HorizontalDivider()
            }

            // Theme Selection
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp),
                color = MaterialTheme.colorScheme.primary,
            )

            // Segmented Button-like UI for Theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeOption(
                    text = stringResource(R.string.theme_system),
                    selected = uiState.themeMode == AppTheme.SYSTEM,
                    onClick = { viewModel.setThemeMode(AppTheme.SYSTEM) },
                    modifier = Modifier.weight(1f),
                )
                ThemeOption(
                    text = stringResource(R.string.theme_light),
                    selected = uiState.themeMode == AppTheme.LIGHT,
                    onClick = { viewModel.setThemeMode(AppTheme.LIGHT) },
                    modifier = Modifier.weight(1f),
                )
                ThemeOption(
                    text = stringResource(R.string.theme_dark),
                    selected = uiState.themeMode == AppTheme.DARK,
                    onClick = { viewModel.setThemeMode(AppTheme.DARK) },
                    modifier = Modifier.weight(1f),
                )
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.dynamic_colors)) },
                    trailingContent = {
                        Switch(
                            checked = uiState.useDynamicColors,
                            onCheckedChange = { viewModel.toggleDynamicColors(it) },
                        )
                    },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (BuildConfig.FLAVOR == "dev") {
                ListItem(headlineContent = { Text(stringResource(R.string.developer_options), color = MaterialTheme.colorScheme.primary) })

                // Dev Configs
                var adLimit by remember { mutableStateOf(uiState.devAdLimit.toString()) }
                var adRate by remember { mutableStateOf(uiState.devAdRate.toString()) }
                var adExpiry by remember { mutableStateOf(uiState.devAdExpiry.toString()) }

                LaunchedEffect(uiState.devAdLimit) {
                    adLimit = uiState.devAdLimit.toString()
                }
                LaunchedEffect(uiState.devAdRate) {
                    adRate = uiState.devAdRate.toString()
                }
                LaunchedEffect(uiState.devAdExpiry) {
                    adExpiry = uiState.devAdExpiry.toString()
                }

                ListItem(
                    headlineContent = { Text(stringResource(R.string.ad_limit)) },
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
                            singleLine = true,
                        )
                    },
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.ad_rate)) },
                    trailingContent = {
                        OutlinedTextField(
                            value = adRate,
                            onValueChange = {
                                adRate = it
                                it.toFloatOrNull()?.let { rate -> viewModel.updateDevAdRate(rate) }
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                        )
                    },
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.ad_expiry_hours)) },
                    trailingContent = {
                        OutlinedTextField(
                            value = adExpiry,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    adExpiry = it
                                    it.toIntOrNull()?.let { hours -> viewModel.updateDevAdExpiry(hours) }
                                }
                            },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                        )
                    },
                )

                ListItem(
                    headlineContent = { Text(stringResource(R.string.open_network_inspector)) },
                    modifier = Modifier.clickable {
                        try {
                            val intent = com.chuckerteam.chucker.api.Chucker.getLaunchIntent(context)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            LogUtils.e("SettingsScreen", "Failed to open Chucker", e)
                        }
                    },
                )

                // Log Capture Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                val logs = LogUtils.captureLogs()
                                withContext(Dispatchers.Main) {
                                    val clipData = android.content.ClipData.newPlainText("Hexium Logs", logs)
                                    clipboardManager.setClipEntry(ClipEntry(clipData))
                                    Toast.makeText(context, logsCopiedMsg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.copy_logs))
                    }

                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                val path = LogUtils.saveLogsToFile(context)
                                withContext(Dispatchers.Main) {
                                    if (path != null) {
                                        Toast.makeText(context, String.format(logsSavedMsgTemplate, path), Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, logsSaveFailedMsg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.save_logs))
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
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxHeight(),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
