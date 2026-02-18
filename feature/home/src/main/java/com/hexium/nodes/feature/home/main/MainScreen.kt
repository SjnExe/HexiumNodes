package com.hexium.nodes.feature.home.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.hexium.nodes.core.ui.R
import com.hexium.nodes.feature.home.AdRewardsScreen
import com.hexium.nodes.feature.home.account.AccountScreen
import com.hexium.nodes.feature.home.server.files.FileEditorScreen
import com.hexium.nodes.feature.home.server.list.ServerListScreen
import com.hexium.nodes.feature.home.server.list.CloudflareVerificationScreen
import com.hexium.nodes.feature.home.server.panel.PanelScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
) {
    val navController = rememberNavController()

    val items = listOf(
        Screen.Servers,
        Screen.Rewards,
        Screen.Account,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isPanel = currentRoute?.startsWith("server_panel") == true ||
                  currentRoute?.startsWith("server_file_editor") == true ||
                  currentRoute == "cloudflare_verification"

    Scaffold(
        topBar = {
            if (!isPanel) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings),
                            )
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (!isPanel) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Servers.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            navigation(startDestination = "server_list", route = Screen.Servers.route) {
                composable("server_list") {
                    ServerListScreen(
                        onNavigateToDashboard = { serverId ->
                            navController.navigate("server_panel/$serverId")
                        },
                        onNavigateToCloudflare = {
                            navController.navigate("cloudflare_verification")
                        }
                    )
                }
                composable("cloudflare_verification") {
                    CloudflareVerificationScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onSuccess = { navController.popBackStack() }
                    )
                }
                composable("server_panel/{serverId}") { backStackEntry ->
                    val serverId = backStackEntry.arguments?.getString("serverId") ?: return@composable
                    PanelScreen(
                        serverId = serverId,
                        onNavigateBack = { navController.popBackStack() },
                        onOpenFile = { path ->
                            val encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8.toString())
                            navController.navigate("server_file_editor/$serverId?path=$encodedPath")
                        }
                    )
                }

                composable(
                    "server_file_editor/{serverId}?path={path}",
                    arguments = listOf(navArgument("path") { type = NavType.StringType }),
                ) { backStackEntry ->
                    val serverId = backStackEntry.arguments?.getString("serverId") ?: return@composable
                    val path = backStackEntry.arguments?.getString("path") ?: ""
                    FileEditorScreen(
                        serverId = serverId,
                        filePath = path,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
            }

            composable(Screen.Rewards.route) {
                AdRewardsScreen()
            }

            composable(Screen.Account.route) {
                AccountScreen(onLogout = onLogout)
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Servers : Screen("servers_graph", "Servers", Icons.Filled.Home)
    object Rewards : Screen("rewards", "Rewards", Icons.Filled.Star)
    object Account : Screen("account", "Account", Icons.Filled.AccountCircle)
}
