package com.hexium.nodes.feature.home.server.network

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.AllocationAttributes

@Composable
fun NetworkScreen(
    serverId: String,
    viewModel: NetworkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serverId) {
        viewModel.loadAllocations(serverId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(uiState.allocations) { allocation ->
                    AllocationItem(allocation.attributes)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun AllocationItem(allocation: AllocationAttributes) {
    ListItem(
        headlineContent = { Text("${allocation.ip}:${allocation.port}") },
        supportingContent = {
            allocation.notes?.let { notes ->
                Text(notes)
            }
        },
        leadingContent = {
            Icon(Icons.Default.NetworkCheck, contentDescription = null)
        },
        trailingContent = {
            if (allocation.isDefault) {
                Badge { Text("Primary") }
            }
        }
    )
}
