package com.hexium.nodes.feature.home.server.backups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.core.model.BackupData
import com.hexium.nodes.data.PterodactylRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BackupUiState(
    val backups: List<BackupData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repository: PterodactylRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun loadBackups(serverId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val backups = repository.getBackups(serverId)
                _uiState.value = _uiState.value.copy(backups = backups, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun createBackup(serverId: String) {
        viewModelScope.launch {
            try {
                repository.createBackup(serverId)
                loadBackups(serverId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to create backup: ${e.message}")
            }
        }
    }
}
