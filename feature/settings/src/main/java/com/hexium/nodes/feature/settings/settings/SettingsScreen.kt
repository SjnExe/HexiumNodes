package com.hexium.nodes.feature.settings.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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

    // Pre-fetch strings
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
            // User Profile Section (Redesigned)
            if (uiState.isLoggedIn) {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (uiState.username?.firstOrNull() ?: '?').toString().uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.username ?: stringResource(R.string.user),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = uiState.email ?: stringResource(R.string.no_email),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = {
                            viewModel.logout()
                            onLogout()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(R.string.logout),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }

            // Theme Selection (Chips style)
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = uiState.themeMode == AppTheme.SYSTEM,
                    onClick = { viewModel.setThemeMode(AppTheme.SYSTEM) },
                    label = { Text(stringResource(R.string.theme_system)) },
                    modifier = Modifier.weight(1f),
                )
                FilterChip(
                    selected = uiState.themeMode == AppTheme.LIGHT,
                    onClick = { viewModel.setThemeMode(AppTheme.LIGHT) },
                    label = { Text(stringResource(R.string.theme_light)) },
                    modifier = Modifier.weight(1f),
                )
                FilterChip(
                    selected = uiState.themeMode == AppTheme.DARK,
                    onClick = { viewModel.setThemeMode(AppTheme.DARK) },
                    label = { Text(stringResource(R.string.theme_dark)) },
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

                // Sync with UI State only if values differ significantly to prevent typing interference
                LaunchedEffect(uiState.devAdLimit) {
                    if (adLimit.toIntOrNull() != uiState.devAdLimit) {
                        adLimit = uiState.devAdLimit.toString()
                    }
                }
                LaunchedEffect(uiState.devAdRate) {
                    if (adRate.toDoubleOrNull() != uiState.devAdRate) {
                        adRate = uiState.devAdRate.toString()
                    }
                }
                LaunchedEffect(uiState.devAdExpiry) {
                    if (adExpiry.toIntOrNull() != uiState.devAdExpiry) {
                        adExpiry = uiState.devAdExpiry.toString()
                    }
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
                            modifier = Modifier.width(100.dp),
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
                                it.toDoubleOrNull()?.let { rate -> viewModel.updateDevAdRate(rate) }
                            },
                            modifier = Modifier.width(100.dp),
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
                            modifier = Modifier.width(100.dp),
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
