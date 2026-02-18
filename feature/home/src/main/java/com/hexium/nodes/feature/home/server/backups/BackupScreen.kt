package com.hexium.nodes.feature.home.server.backups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.BackupAttributes
import com.hexium.nodes.feature.home.server.files.formatSize

@Composable
fun BackupScreen(
    serverId: String,
    viewModel: BackupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serverId) {
        viewModel.loadBackups(serverId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
        } else {
            if (uiState.backups.isEmpty()) {
                Text("No backups found", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(uiState.backups) { backup ->
                        BackupItem(backup.attributes)
                        HorizontalDivider()
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.createBackup(serverId) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Backup")
        }
    }
}

@Composable
fun BackupItem(backup: BackupAttributes) {
    ListItem(
        headlineContent = { Text(backup.name) },
        supportingContent = {
            Column {
                Text("UUID: ${backup.uuid.take(8)}...")
                Text("Size: ${formatSize(backup.bytes)}")
                Text("Created: ${backup.createdAt.take(10)}")
            }
        },
        leadingContent = {
            Icon(Icons.Default.Backup, contentDescription = null)
        }
    )
}
