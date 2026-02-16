package com.hexium.nodes.feature.home.server.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.PowerSignal
import com.hexium.nodes.core.model.ServerAttributes

@Composable
fun ServerDashboardScreen(
    serverId: String,
    onNavigateToConsole: () -> Unit,
    onNavigateToFiles: () -> Unit,
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
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = server.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Badge
                    val status = resources?.currentState ?: "Fetching..."
                    Text("Status: $status", style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Power Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Button(onClick = { viewModel.sendPowerSignal(PowerSignal.START) }) { Text("Start") }
                        Button(onClick = { viewModel.sendPowerSignal(PowerSignal.RESTART) }) { Text("Restart") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Button(onClick = { viewModel.sendPowerSignal(PowerSignal.STOP) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Stop") }
                        Button(onClick = { viewModel.sendPowerSignal(PowerSignal.KILL) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)) { Text("Kill") }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resources
                    if (resources != null) {
                        Text("Resources", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        ResourceItem("Memory", "${resources.resources.memoryBytes / 1024 / 1024} MB / ${server.limits.memory} MB")
                        ResourceItem("CPU", "${String.format("%.2f", resources.resources.cpuAbsolute)}% / ${server.limits.cpu}%")
                        ResourceItem("Disk", "${resources.resources.diskBytes / 1024 / 1024} MB / ${server.limits.disk} MB")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            onClick = onNavigateToConsole,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Console")
                        }
                        Button(
                            onClick = onNavigateToFiles,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Files")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}
