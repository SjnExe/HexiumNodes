package com.hexium.nodes.feature.home.server.console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hexium.nodes.data.ConsoleSession
import com.hexium.nodes.data.PterodactylRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConsoleUiState(
    val logs: List<String> = emptyList(),
    val isConnected: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ConsoleViewModel @Inject constructor(
    private val repository: PterodactylRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsoleUiState())
    val uiState: StateFlow<ConsoleUiState> = _uiState.asStateFlow()

    private var session: ConsoleSession? = null
    private val gson = Gson()

    fun connect(serverId: String) {
        if (session != null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConnected = false, error = null)
            try {
                session = repository.createConsoleSession(serverId)
                _uiState.value = _uiState.value.copy(isConnected = true)

                session?.incoming?.collect { message ->
                    handleMessage(message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Connection failed: ${e.message}", isConnected = false)
            }
        }
    }

    private fun handleMessage(json: String) {
        try {
            val event = gson.fromJson(json, ConsoleEvent::class.java)
            when (event.event) {
                "console output", "install output", "daemon message" -> {
                    event.args?.firstOrNull()?.let { log ->
                        // Strip ANSI codes (colors, cursor movements, etc.)
                        val cleanLog = log.replace(Regex("\u001B\\[[;?0-9]*[a-zA-Z]"), "")
                        appendLog(cleanLog)
                    }
                }
                "jwt error" -> {
                    _uiState.value = _uiState.value.copy(error = "Authentication failed")
                }
            }
        } catch (e: Exception) {
            // Ignore parse errors
        }
    }

    private fun appendLog(line: String) {
        val current = _uiState.value.logs
        val newLogs = (current + line).takeLast(500) // Keep last 500
        _uiState.value = _uiState.value.copy(logs = newLogs)
    }

    fun sendCommand(command: String) {
        session?.send(command)
    }

    fun clearLogs() {
        _uiState.value = _uiState.value.copy(logs = emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        session?.close()
        session = null
    }
}

data class ConsoleEvent(
    val event: String,
    val args: List<String>?,
)
