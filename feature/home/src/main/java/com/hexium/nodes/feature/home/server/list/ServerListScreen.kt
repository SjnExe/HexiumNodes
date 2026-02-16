package com.hexium.nodes.feature.home.server.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.ServerData

@Composable
fun ServerListScreen(
    onNavigateToDashboard: (String) -> Unit,
    onNavigateToCloudflare: () -> Unit,
    viewModel: ServerListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is ServerListUiState.Success && state.servers.size == 1 && viewModel.shouldAutoNavigate()) {
            viewModel.onAutoNavigate()
            onNavigateToDashboard(state.servers.first().attributes.identifier)
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (val state = uiState) {
            is ServerListUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ServerListUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadServers() }) {
                        Text("Retry")
                    }
                    if (state.message.contains("403")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNavigateToCloudflare) {
                            Text("Verify Cloudflare")
                        }
                    }
                }
            }

            is ServerListUiState.Success -> {
                // If multiple servers OR single server but already navigated (back pressed)
                if (state.servers.size > 1 || (state.servers.size == 1 && !viewModel.shouldAutoNavigate())) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.servers) { server ->
                            ServerCard(server = server, onClick = {
                                onNavigateToDashboard(server.attributes.identifier)
                            })
                        }
                    }
                } else if (state.servers.isEmpty()) {
                    Text("No servers found.", modifier = Modifier.align(Alignment.Center))
                } else {
                    // Single server case, pending navigation
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun ServerCard(server: ServerData, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = server.attributes.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Node: ${server.attributes.node}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "IP: ${server.attributes.sftpDetails.ip}:${server.attributes.sftpDetails.port}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
