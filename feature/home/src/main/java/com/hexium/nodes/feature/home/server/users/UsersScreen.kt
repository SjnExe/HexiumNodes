package com.hexium.nodes.feature.home.server.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hexium.nodes.core.model.SubUserAttributes

@Composable
fun UsersScreen(
    serverId: String,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serverId) {
        viewModel.loadUsers(serverId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(uiState.users) { user ->
                    UserItem(user.attributes)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun UserItem(user: SubUserAttributes) {
    ListItem(
        headlineContent = { Text(user.username) },
        supportingContent = { Text(user.email) },
        leadingContent = {
            Icon(Icons.Default.Person, contentDescription = null)
        },
        trailingContent = {
            if (user.twoFactorEnabled) {
                Badge { Text("2FA") }
            }
        }
    )
}
