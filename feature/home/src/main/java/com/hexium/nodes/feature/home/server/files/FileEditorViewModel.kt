package com.hexium.nodes.feature.home.server.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.data.PterodactylRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val content: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

@HiltViewModel
class FileEditorViewModel @Inject constructor(
    private val repository: PterodactylRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var serverId: String? = null
    private var filePath: String? = null

    fun loadFile(serverId: String, path: String) {
        if (this.serverId == serverId && this.filePath == path && _uiState.value.content.isNotEmpty()) return
        this.serverId = serverId
        this.filePath = path

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val content = repository.getFileContent(serverId, path)
                _uiState.value = _uiState.value.copy(content = content, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun saveFile(content: String) {
        val id = serverId ?: return
        val path = filePath ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, isSuccess = false)
            try {
                repository.writeFile(id, path, content)
                _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "Save failed: ${e.message}")
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
