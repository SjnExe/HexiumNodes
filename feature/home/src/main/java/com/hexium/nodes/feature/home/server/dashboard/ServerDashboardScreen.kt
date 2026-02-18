package com.hexium.nodes.feature.home.server.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.PowerSignal

@Composable
fun ServerDashboardScreen(
    serverId: String,
    viewModel: ServerDashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serverId) {
        viewModel.loadServer(serverId)
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
        } else {
            val server = uiState.server
            val resources = uiState.resources

            if (server != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Header Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = server.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            val status = resources?.currentState ?: "Connecting..."
                            AssistChip(
                                onClick = {},
                                label = { Text(status.uppercase()) },
                                leadingIcon = {
                                    if (status.equals("running", ignoreCase = true)) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    } else {
                                        Icon(Icons.Default.Stop, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    }
                                },
                            )
                        }
                    }

                    // Power Controls
                    Text("Power Controls", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilledTonalButton(
                            onClick = { viewModel.sendPowerSignal(PowerSignal.START) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start")
                        }
                        FilledTonalButton(
                            onClick = { viewModel.sendPowerSignal(PowerSignal.RESTART) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restart")
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = { viewModel.sendPowerSignal(PowerSignal.STOP) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stop")
                        }
                        Button(
                            onClick = { viewModel.sendPowerSignal(PowerSignal.KILL) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kill")
                        }
                    }

                    // Resources
                    if (resources != null) {
                        Text("Live Resources", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                        val memoryLimit = if (server.limits.memory == 0L) "∞" else "${server.limits.memory} MB"
                        val diskLimit = if (server.limits.disk == 0L) "∞" else "${server.limits.disk} MB"
                        val cpuLimit = if (server.limits.cpu == 0L) "∞" else "${server.limits.cpu}%"

                        ResourceCard(
                            icon = Icons.Default.Memory,
                            label = "Memory",
                            value = "${resources.resources.memoryBytes / 1024 / 1024} MB",
                            limit = memoryLimit,
                            progress = if (server.limits.memory > 0) (resources.resources.memoryBytes / 1024 / 1024).toFloat() / server.limits.memory else 0f,
                        )
                        ResourceCard(
                            icon = Icons.Default.Speed,
                            label = "CPU",
                            value = "${String.format("%.2f", resources.resources.cpuAbsolute)}%",
                            limit = cpuLimit,
                            progress = if (server.limits.cpu > 0) resources.resources.cpuAbsolute.toFloat() / server.limits.cpu else 0f,
                        )
                        ResourceCard(
                            icon = Icons.Default.SdStorage,
                            label = "Disk",
                            value = "${resources.resources.diskBytes / 1024 / 1024} MB",
                            limit = diskLimit,
                            progress = if (server.limits.disk > 0) (resources.resources.diskBytes / 1024 / 1024).toFloat() / server.limits.disk else 0f,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceCard(icon: ImageVector, label: String, value: String, limit: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text("$value / $limit", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
