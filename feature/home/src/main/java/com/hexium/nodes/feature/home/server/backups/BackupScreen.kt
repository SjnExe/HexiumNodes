package com.hexium.nodes.feature.home.server.backups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hexium.nodes.core.model.BackupAttributes
import com.hexium.nodes.feature.home.server.files.formatSize

@Composable
fun BackupScreen(
    serverId: String,
    viewModel: BackupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(serverId) {
        viewModel.loadBackups(serverId)
    }

    LaunchedEffect(uiState.downloadUrl) {
        uiState.downloadUrl?.let { url ->
            uriHandler.openUri(url)
            viewModel.clearDownloadUrl()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (uiState.error != null) {
                    viewModel.loadBackups(serverId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                        BackupItem(
                            backup = backup.attributes,
                            onDownload = { viewModel.downloadBackup(serverId, backup.attributes.uuid) },
                            onDelete = { viewModel.deleteBackup(serverId, backup.attributes.uuid) }
                        )
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
fun BackupItem(
    backup: BackupAttributes,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
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
        },
        trailingContent = {
            Row {
                IconButton(onClick = onDownload) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    )
}
