package com.hexium.nodes.feature.home.server.files

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.FileData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    serverId: String,
    onOpenFile: (String) -> Unit, // Path
    onNavigateBack: () -> Unit,
    viewModel: FileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadFile(it) }
    }

    LaunchedEffect(serverId) {
        viewModel.loadFiles(serverId)
    }

    // Intercept back press for directory navigation
    val currentPath = uiState.currentPath
    BackHandler(enabled = currentPath != "/" && currentPath.isNotEmpty()) {
        viewModel.navigateUp()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentPath.ifEmpty { "/" }) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentPath != "/" && currentPath.isNotEmpty()) {
                            viewModel.navigateUp()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { launcher.launch("*/*") }) {
                Icon(Icons.Default.Upload, contentDescription = "Upload File")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
            } else {
                val files = uiState.files

                // Sort folders first, then files
                val sortedFiles = remember(files) {
                    files.sortedWith(compareBy({ !it.attributes.isFile }, { it.attributes.name }))
                }

                if (sortedFiles.isEmpty()) {
                    Text("Empty Directory", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(sortedFiles) { file ->
                            FileItem(
                                file = file,
                                onClick = {
                                    if (file.attributes.isFile) {
                                        val path = if (currentPath == "/" || currentPath.isEmpty()) file.attributes.name else "$currentPath/${file.attributes.name}"
                                        onOpenFile(path)
                                    } else {
                                        viewModel.navigateTo(file.attributes.name)
                                    }
                                },
                                onDownload = {
                                    viewModel.downloadFile(file.attributes.name)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileItem(file: FileData, onClick: () -> Unit, onDownload: () -> Unit) {
    ListItem(
        headlineContent = { Text(file.attributes.name) },
        leadingContent = {
            Icon(
                imageVector = if (file.attributes.isFile) Icons.Default.Description else Icons.Default.Folder,
                contentDescription = null,
                tint = if (file.attributes.isFile) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
            )
        },
        supportingContent = {
            Text(file.attributes.mode + " | " + formatSize(file.attributes.size))
        },
        trailingContent = {
            if (file.attributes.isFile) {
                IconButton(onClick = onDownload) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
            }
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
