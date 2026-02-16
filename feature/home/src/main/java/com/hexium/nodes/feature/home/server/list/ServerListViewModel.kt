package com.hexium.nodes.feature.home.server.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.core.model.ServerData
import com.hexium.nodes.data.PterodactylRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ServerListUiState {
    object Loading : ServerListUiState()
    data class Success(val servers: List<ServerData>) : ServerListUiState()
    data class Error(val message: String) : ServerListUiState()
}

@HiltViewModel
class ServerListViewModel @Inject constructor(
    private val repository: PterodactylRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ServerListUiState>(ServerListUiState.Loading)
    val uiState: StateFlow<ServerListUiState> = _uiState.asStateFlow()

    init {
        loadServers()
    }

    fun loadServers() {
        viewModelScope.launch {
            _uiState.value = ServerListUiState.Loading
            try {
                // First check if API Key is set
                val apiKey = repository.getApiKey()
                if (apiKey.isNullOrBlank()) {
                    _uiState.value = ServerListUiState.Error("API Key not configured. Go to Settings > Developer Options.")
                    return@launch
                }

                val servers = repository.getServers()
                _uiState.value = ServerListUiState.Success(servers)
            } catch (e: Exception) {
                _uiState.value = ServerListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
