package com.hexium.nodes.feature.home.server.network

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hexium.nodes.core.model.AllocationAttributes
import com.hexium.nodes.core.model.SubdomainAttributes

@Composable
fun NetworkScreen(
    serverId: String,
    viewModel: NetworkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showCreateSubdomainDialog by remember { mutableStateOf(false) }
    var showEditNoteDialog by remember { mutableStateOf<AllocationAttributes?>(null) }
    var showDeleteAllocationDialog by remember { mutableStateOf<AllocationAttributes?>(null) }
    var showDeleteSubdomainDialog by remember { mutableStateOf<SubdomainAttributes?>(null) }
    var showCreateAllocationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(serverId) {
        viewModel.loadData(serverId)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (uiState.error != null) {
                    viewModel.loadData(serverId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (uiState.selectedTab == 0) {
                FloatingActionButton(onClick = { showCreateAllocationDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "New Port")
                }
            } else {
                FloatingActionButton(onClick = { showCreateSubdomainDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "New Subdomain")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = uiState.selectedTab) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("Ports") }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Subdomains") }
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.error}")
                }
            } else {
                if (uiState.selectedTab == 0) {
                    PortsList(
                        allocations = uiState.allocations.map { it.attributes },
                        onEditNote = { showEditNoteDialog = it },
                        onDelete = { showDeleteAllocationDialog = it }
                    )
                } else {
                    SubdomainsList(
                        subdomains = uiState.subdomains.map { it.attributes },
                        onDelete = { showDeleteSubdomainDialog = it }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showCreateAllocationDialog) {
        AlertDialog(
            onDismissRequest = { showCreateAllocationDialog = false },
            title = { Text("Create New Port") },
            text = { Text("Are you sure you want to request a new port allocation?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createAllocation(serverId)
                    showCreateAllocationDialog = false
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAllocationDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditNoteDialog != null) {
        var note by remember { mutableStateOf(showEditNoteDialog?.notes ?: "") }
        AlertDialog(
            onDismissRequest = { showEditNoteDialog = null },
            title = { Text("Edit Note") },
            text = { OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }) },
            confirmButton = {
                TextButton(onClick = {
                    showEditNoteDialog?.let { viewModel.updateAllocationNote(serverId, it.id, note) }
                    showEditNoteDialog = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditNoteDialog = null }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteAllocationDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteAllocationDialog = null },
            title = { Text("Delete Port") },
            text = { Text("Are you sure you want to delete port ${showDeleteAllocationDialog?.port}?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAllocationDialog?.let { viewModel.deleteAllocation(serverId, it.id) }
                    showDeleteAllocationDialog = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllocationDialog = null }) { Text("Cancel") }
            }
        )
    }

    if (showCreateSubdomainDialog) {
        var domain by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateSubdomainDialog = false },
            title = { Text("Create Subdomain") },
            text = { OutlinedTextField(value = domain, onValueChange = { domain = it }, label = { Text("Subdomain") }) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createSubdomain(serverId, domain)
                    showCreateSubdomainDialog = false
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateSubdomainDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteSubdomainDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteSubdomainDialog = null },
            title = { Text("Delete Subdomain") },
            text = { Text("Are you sure you want to delete ${showDeleteSubdomainDialog?.domain}?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteSubdomainDialog?.let { viewModel.deleteSubdomain(serverId, it.id) }
                    showDeleteSubdomainDialog = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSubdomainDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun PortsList(
    allocations: List<AllocationAttributes>,
    onEditNote: (AllocationAttributes) -> Unit,
    onDelete: (AllocationAttributes) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(allocations) { allocation ->
            ListItem(
                headlineContent = { Text("Port: ${allocation.port}") },
                supportingContent = {
                    allocation.notes?.let { Text(it) }
                },
                leadingContent = { Icon(Icons.Default.NetworkCheck, null) },
                trailingContent = {
                    Row {
                        IconButton(onClick = { onEditNote(allocation) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Note")
                        }
                        if (!allocation.isDefault) {
                            IconButton(onClick = { onDelete(allocation) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        } else {
                             Badge { Text("Primary") }
                        }
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun SubdomainsList(
    subdomains: List<SubdomainAttributes>,
    onDelete: (SubdomainAttributes) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(subdomains) { subdomain ->
            ListItem(
                headlineContent = { Text(subdomain.domain) },
                leadingContent = { Icon(Icons.Default.Dns, null) },
                trailingContent = {
                    IconButton(onClick = { onDelete(subdomain) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
            HorizontalDivider()
        }
    }
}
