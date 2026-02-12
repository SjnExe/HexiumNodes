package com.hexium.nodes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hexium.nodes.core.ui.theme.HexiumNodesTheme
import com.hexium.nodes.data.preferences.AppTheme
import com.hexium.nodes.feature.auth.login.LoginScreen
import com.hexium.nodes.feature.auth.splash.SplashScreen
import com.hexium.nodes.feature.home.HomeScreen
import com.hexium.nodes.feature.home.HomeViewModel
import com.hexium.nodes.feature.settings.settings.SettingsScreen
import com.hexium.nodes.feature.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()

            val isDarkTheme = when (settingsState.themeMode) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
            }

            HexiumNodesTheme(
                darkTheme = isDarkTheme,
                dynamicColor = settingsState.useDynamicColors,
            ) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                },
                            )
                        }
                        composable("home") {
                            val mainViewModel: HomeViewModel = hiltViewModel()
                            HomeScreen(
                                viewModel = mainViewModel,
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                },
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
