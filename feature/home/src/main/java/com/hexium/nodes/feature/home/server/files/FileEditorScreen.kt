package com.hexium.nodes.feature.home.server.files

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileEditorScreen(
    serverId: String,
    filePath: String,
    onNavigateBack: () -> Unit,
    viewModel: FileEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var content by remember { mutableStateOf("") }

    // Update content when loaded from VM
    LaunchedEffect(uiState.content) {
        if (!uiState.isLoading && uiState.content.isNotEmpty()) {
            content = uiState.content
        }
    }

    LaunchedEffect(serverId, filePath) {
        viewModel.loadFile(serverId, filePath)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(filePath.substringAfterLast('/')) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveFile(content) }) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSurface)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            } else {
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
            }
        }
    }
}
