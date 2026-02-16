package com.hexium.nodes.feature.home.server.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
                if (state.servers.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.servers) { server ->
                            ServerCard(server = server, onClick = {
                                onNavigateToDashboard(server.attributes.identifier)
                            })
                        }
                    }
                } else {
                    Text("No servers found.", modifier = Modifier.align(Alignment.Center))
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = server.attributes.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ID: ${server.attributes.identifier}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
