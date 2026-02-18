package com.hexium.nodes.feature.home.server.startup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.StartupVariableAttributes

@Composable
fun StartupScreen(
    serverId: String,
    viewModel: StartupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serverId) {
        viewModel.loadStartup(serverId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(uiState.variables) { variable ->
                    StartupVariableItem(variable.attributes)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun StartupVariableItem(variable: StartupVariableAttributes) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(variable.name, style = MaterialTheme.typography.titleSmall)
        if (variable.description.isNotBlank()) {
            Text(variable.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = variable.serverValue,
            onValueChange = {}, // Read-only for now
            label = { Text(variable.envVariable) },
            enabled = variable.isEditable,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
