package com.hexium.nodes.feature.home.server.panel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hexium.nodes.feature.home.server.console.ConsoleScreen
import com.hexium.nodes.feature.home.server.dashboard.ServerDashboardScreen
import com.hexium.nodes.feature.home.server.files.FileBrowserScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelScreen(
    serverId: String,
    onNavigateBack: () -> Unit,
    onOpenFile: (String) -> Unit,
    viewModel: PanelViewModel = hiltViewModel(),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentScreen by viewModel.currentScreen.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Hexium Panel", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
                HorizontalDivider()

                // Scrollable container for many items
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                     androidx.compose.foundation.layout.Column {
                        NavigationDrawerItem(
                            label = { Text("Dashboard") },
                            icon = { Icon(Icons.Default.Dashboard, null) },
                            selected = currentScreen == PanelScreenType.DASHBOARD,
                            onClick = { viewModel.navigateTo(PanelScreenType.DASHBOARD); scope.launch { drawerState.close() } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Console") },
                            icon = { Icon(Icons.Default.Terminal, null) },
                            selected = currentScreen == PanelScreenType.CONSOLE,
                            onClick = { viewModel.navigateTo(PanelScreenType.CONSOLE); scope.launch { drawerState.close() } }
                        )
                         NavigationDrawerItem(
                            label = { Text("Files") },
                            icon = { Icon(Icons.Default.Folder, null) },
                            selected = currentScreen == PanelScreenType.FILES,
                            onClick = { viewModel.navigateTo(PanelScreenType.FILES); scope.launch { drawerState.close() } }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // Placeholders
                        listOf(
                            "Settings" to Icons.Default.Settings,
                            "Activity" to Icons.Default.History,
                            "Players" to Icons.Default.Person,
                            "Databases" to Icons.Default.Storage,
                            "Backups" to Icons.Default.Backup,
                            "Network" to Icons.Default.NetworkCheck,
                            "Plugins" to Icons.Default.Extension,
                            "Subdomains" to Icons.Default.Dns,
                            "Importer" to Icons.Default.ImportExport,
                            "Schedules" to Icons.Default.Schedule,
                            "Users" to Icons.Default.Group,
                            "Startup" to Icons.Default.PlayArrow,
                            "Versions" to Icons.Default.Update,
                            "Properties" to Icons.Default.Tune
                        ).forEach { (name, icon) ->
                             NavigationDrawerItem(
                                label = { Text(name) },
                                icon = { Icon(icon, null) },
                                selected = false,
                                onClick = { scope.launch { drawerState.close() } }
                            )
                        }
                     }
                }
            }
        },
    ) {
        if (currentScreen == PanelScreenType.FILES) {
             FileBrowserScreen(
                 serverId = serverId,
                 onOpenFile = onOpenFile,
                 onNavigateBack = { viewModel.navigateTo(PanelScreenType.DASHBOARD) }
             )
        } else {
             Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(currentScreen.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                     when (currentScreen) {
                         PanelScreenType.DASHBOARD -> ServerDashboardScreen(
                             serverId = serverId,
                             onNavigateToConsole = { viewModel.navigateTo(PanelScreenType.CONSOLE) },
                             onNavigateToFiles = { viewModel.navigateTo(PanelScreenType.FILES) }
                         )
                         PanelScreenType.CONSOLE -> ConsoleScreen(serverId = serverId)
                         else -> Text("Coming Soon", modifier = Modifier.padding(16.dp))
                     }
                }
            }
        }
    }
}
