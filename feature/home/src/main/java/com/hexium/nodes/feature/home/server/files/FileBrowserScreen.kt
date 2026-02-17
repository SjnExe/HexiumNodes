package com.hexium.nodes.feature.home.server.files

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.FileData
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    serverId: String,
    onOpenFile: (String) -> Unit, // Path
    onNavigateBack: () -> Unit,
    viewModel: FileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var createDialogType by remember { mutableStateOf("folder") } // folder or file

    val uploadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadFile(it, andDecompress = false) }
    }

    val uploadZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadFile(it, andDecompress = true) }
    }

    LaunchedEffect(serverId) {
        viewModel.loadFiles(serverId)
    }

    LaunchedEffect(uiState.isActionSuccess) {
        if (uiState.isActionSuccess) {
            viewModel.resetSuccess()
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    // Intercept back press for directory navigation or selection mode
    val currentPath = uiState.currentPath
    BackHandler(enabled = (currentPath != "/" && currentPath.isNotEmpty()) || uiState.selectionMode) {
        if (uiState.selectionMode) {
            viewModel.clearSelection()
        } else {
            viewModel.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.selectionMode) {
                        Text("${uiState.selectedFiles.size} selected")
                    } else {
                        Text(currentPath.ifEmpty { "/" })
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.selectionMode) {
                            viewModel.clearSelection()
                        } else if (currentPath != "/" && currentPath.isNotEmpty()) {
                            viewModel.navigateUp()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            if (uiState.selectionMode) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.selectionMode) {
                        IconButton(onClick = { viewModel.downloadSelected() }) {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                        }
                        IconButton(onClick = { viewModel.archiveSelected() }) {
                            Icon(Icons.Default.Archive, contentDescription = "Archive")
                        }
                        IconButton(onClick = { viewModel.deleteSelected() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.selectionMode) {
                ExpandableFab(
                    onCreateFile = { showCreateDialog = true; createDialogType = "file" },
                    onCreateFolder = { showCreateDialog = true; createDialogType = "folder" },
                    onUploadFile = { showUploadDialog = true }
                )
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
                    files.sortedWith(compareBy({ it.attributes.isFile }, { it.attributes.name }))
                }

                if (sortedFiles.isEmpty()) {
                    Text("Empty Directory", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(sortedFiles) { file ->
                            FileItem(
                                file = file,
                                isSelected = uiState.selectedFiles.contains(file.attributes.name),
                                selectionMode = uiState.selectionMode,
                                onClick = {
                                    if (uiState.selectionMode) {
                                        viewModel.toggleSelection(file.attributes.name)
                                    } else {
                                        if (file.attributes.isFile) {
                                            val path = if (currentPath == "/" || currentPath.isEmpty()) file.attributes.name else "$currentPath/${file.attributes.name}"
                                            onOpenFile(path)
                                        } else {
                                            viewModel.navigateTo(file.attributes.name)
                                        }
                                    }
                                },
                                onLongClick = {
                                    viewModel.toggleSelection(file.attributes.name)
                                },
                                onDownload = { viewModel.downloadFile(file.attributes.name) },
                                onRename = { newName -> viewModel.renameFile(file.attributes.name, newName) },
                                onDelete = { viewModel.toggleSelection(file.attributes.name); viewModel.deleteSelected() },
                                onUnarchive = { viewModel.unarchiveFile(file.attributes.name) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateDialog(
            type = createDialogType,
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                if (createDialogType == "folder") viewModel.createFolder(name) else viewModel.createFile(name)
                showCreateDialog = false
            }
        )
    }

    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = { Text("Upload") },
            text = {
                Column {
                    TextButton(onClick = { showUploadDialog = false; uploadLauncher.launch("*/*") }) {
                        Text("Upload File")
                    }
                    TextButton(onClick = { showUploadDialog = false; uploadZipLauncher.launch("application/zip") }) {
                        Text("Upload & Extract Zip")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItem(
    file: FileData,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDownload: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit,
    onUnarchive: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }

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
            val date = try {
                file.attributes.modifiedAt.substring(0, 10)
            } catch (e: Exception) { "" }
            val sizeText = if (file.attributes.isFile) " | ${formatSize(file.attributes.size)}" else ""
            Text(file.attributes.mode + sizeText + " | " + date)
        },
        trailingContent = {
            if (!selectionMode) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open") },
                            onClick = { showMenu = false; onClick() }
                        )
                        if (file.attributes.isFile) {
                            DropdownMenuItem(
                                text = { Text("Download") },
                                onClick = { showMenu = false; onDownload() }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            onClick = { showMenu = false; showRenameDialog = true }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { showMenu = false; onDelete() }
                        )
                        DropdownMenuItem(
                            text = { Text("Properties") },
                            onClick = { showMenu = false; showPropertiesDialog = true }
                        )
                        if (file.attributes.name.endsWith(".zip") || file.attributes.name.endsWith(".tar.gz")) {
                            DropdownMenuItem(
                                text = { Text("Unarchive") },
                                onClick = { showMenu = false; onUnarchive() }
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    )

    if (showRenameDialog) {
        var newName by remember { mutableStateOf(file.attributes.name) }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename") },
            text = { TextField(value = newName, onValueChange = { newName = it }) },
            confirmButton = {
                Button(onClick = { showRenameDialog = false; onRename(newName) }) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showPropertiesDialog) {
        AlertDialog(
            onDismissRequest = { showPropertiesDialog = false },
            title = { Text("Properties") },
            text = {
                Column {
                    Text("Name: ${file.attributes.name}")
                    if (file.attributes.isFile) {
                        Text("Size: ${formatSize(file.attributes.size)}")
                    } else {
                        Text("Type: Directory")
                    }
                    Text("Mode: ${file.attributes.mode}")
                    Text("Created: ${file.attributes.createdAt}")
                    Text("Modified: ${file.attributes.modifiedAt}")
                    Text("MimeType: ${file.attributes.mimeType}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showPropertiesDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun ExpandableFab(
    onCreateFile: () -> Unit,
    onCreateFolder: () -> Unit,
    onUploadFile: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.End) {
        if (expanded) {
            FloatingActionButton(onClick = { expanded = false; onCreateFile() }, modifier = Modifier.padding(bottom = 16.dp)) {
                Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "New File")
            }
            FloatingActionButton(onClick = { expanded = false; onCreateFolder() }, modifier = Modifier.padding(bottom = 16.dp)) {
                Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
            }
            FloatingActionButton(onClick = { expanded = false; onUploadFile() }, modifier = Modifier.padding(bottom = 16.dp)) {
                Icon(Icons.Default.UploadFile, contentDescription = "Upload")
            }
        }
        FloatingActionButton(onClick = { expanded = !expanded }) {
            Icon(if (expanded) Icons.Default.Close else Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun CreateDialog(type: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New $type") },
        text = { TextField(value = name, onValueChange = { name = it }, label = { Text("Name") }) },
        confirmButton = {
            Button(onClick = { onConfirm(name) }) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
