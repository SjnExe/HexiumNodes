package com.hexium.nodes.feature.home.server.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.core.model.PowerSignal
import com.hexium.nodes.core.model.ResourcesAttributes
import com.hexium.nodes.core.model.ServerAttributes
import com.hexium.nodes.data.PterodactylRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val server: ServerAttributes? = null,
    val resources: ResourcesAttributes? = null,
    val error: String? = null,
)

@HiltViewModel
class ServerDashboardViewModel @Inject constructor(
    private val repository: PterodactylRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null
    private var serverId: String? = null

    fun loadServer(id: String) {
        if (serverId == id && _uiState.value.server != null) return
        serverId = id

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Fetch all servers and filter
                val servers = repository.getServers()
                val server = servers.find { it.attributes.identifier == id }?.attributes

                if (server != null) {
                    _uiState.value = _uiState.value.copy(server = server, isLoading = false)
                    startPolling(id)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Server not found")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun startPolling(id: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val resources = repository.getResources(id)
                    _uiState.value = _uiState.value.copy(resources = resources)
                } catch (e: Exception) {
                    // Ignore polling errors
                }
                delay(3000)
            }
        }
    }

    fun sendPowerSignal(signal: PowerSignal) {
        val id = serverId ?: return
        viewModelScope.launch {
            try {
                repository.sendPowerSignal(id, signal)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Power Action Failed: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
