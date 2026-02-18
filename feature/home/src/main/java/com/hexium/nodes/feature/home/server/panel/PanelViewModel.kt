package com.hexium.nodes.feature.home.server.panel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class PanelScreenType {
    DASHBOARD,
    CONSOLE,
    FILES,

    // Add others as needed, placeholders for now
    SETTINGS,
    ACTIVITY,
    PLAYERS,
    DATABASES,
    BACKUPS,
    NETWORK,
    PLUGINS,
    SUBDOMAINS,
    IMPORTER,
    SCHEDULES,
    USERS,
    STARTUP,
    VERSIONS,
    PROPERTIES,
}

@HiltViewModel
class PanelViewModel @Inject constructor() : ViewModel() {
    private val _currentScreen = MutableStateFlow(PanelScreenType.DASHBOARD)
    val currentScreen: StateFlow<PanelScreenType> = _currentScreen.asStateFlow()

    fun navigateTo(screen: PanelScreenType) {
        _currentScreen.value = screen
    }
}
