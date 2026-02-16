package com.hexium.nodes.feature.home.server.files

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexium.nodes.core.model.FileData
import com.hexium.nodes.data.PterodactylRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FileUiState(
    val currentPath: String = "/",
    val files: List<FileData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectionMode: Boolean = false,
    val selectedFiles: Set<String> = emptySet(),
    val isActionSuccess: Boolean = false,
)

@HiltViewModel
class FileViewModel @Inject constructor(
    private val repository: PterodactylRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileUiState())
    val uiState: StateFlow<FileUiState> = _uiState.asStateFlow()

    private var serverId: String? = null

    fun loadFiles(serverId: String, path: String = "/") {
        this.serverId = serverId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, currentPath = path, selectionMode = false, selectedFiles = emptySet())
            try {
                val files = repository.listFiles(serverId, path)
                _uiState.value = _uiState.value.copy(files = files, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun navigateTo(folderName: String) {
        val current = _uiState.value.currentPath
        val newPath = if (current == "/") folderName else "$current/$folderName"
        loadFiles(serverId!!, newPath)
    }

    fun navigateUp() {
        val current = _uiState.value.currentPath
        if (current == "/" || current.isEmpty()) return

        val newPath = current.substringBeforeLast('/', "")
        val finalPath = if (newPath.isEmpty()) "/" else newPath
        loadFiles(serverId!!, finalPath)
    }

    fun toggleSelection(fileName: String) {
        val currentSelection = _uiState.value.selectedFiles.toMutableSet()
        if (currentSelection.contains(fileName)) {
            currentSelection.remove(fileName)
        } else {
            currentSelection.add(fileName)
        }
        val newSelectionMode = currentSelection.isNotEmpty()
        _uiState.value = _uiState.value.copy(selectedFiles = currentSelection, selectionMode = newSelectionMode)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectionMode = false, selectedFiles = emptySet())
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isActionSuccess = false, error = null)
    }

    fun createFolder(name: String) {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.createFolder(serverId, currentPath, name)
                loadFiles(serverId, currentPath)
                _uiState.value = _uiState.value.copy(isActionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to create folder: ${e.message}")
            }
        }
    }

    fun createFile(name: String) {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val filePath = if (currentPath == "/") name else "$currentPath/$name"
                repository.writeFile(serverId, filePath, "")
                loadFiles(serverId, currentPath)
                _uiState.value = _uiState.value.copy(isActionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to create file: ${e.message}")
            }
        }
    }

    fun renameFile(from: String, to: String) {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.renameFile(serverId, currentPath, from, to)
                loadFiles(serverId, currentPath)
                _uiState.value = _uiState.value.copy(isActionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to rename: ${e.message}")
            }
        }
    }

    fun deleteSelected() {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        val files = _uiState.value.selectedFiles.toList()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.deleteFiles(serverId, currentPath, files)
                loadFiles(serverId, currentPath)
                _uiState.value = _uiState.value.copy(isActionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to delete: ${e.message}")
            }
        }
    }

    fun archiveSelected() {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        val files = _uiState.value.selectedFiles.toList()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.compressFiles(serverId, currentPath, files)
                loadFiles(serverId, currentPath)
                _uiState.value = _uiState.value.copy(isActionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to archive: ${e.message}")
            }
        }
    }

    fun unarchiveFile(fileName: String) {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.decompressFile(serverId, currentPath, fileName)
                loadFiles(serverId, currentPath)
                _uiState.value = _uiState.value.copy(isActionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to unarchive: ${e.message}")
            }
        }
    }

    fun downloadFile(fileName: String) {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        val fullPath = if (currentPath == "/" || currentPath.isEmpty()) fileName else "$currentPath/$fileName"

        viewModelScope.launch {
            try {
                val url = repository.getDownloadUrl(serverId, fullPath)
                val request = android.app.DownloadManager.Request(android.net.Uri.parse(url))
                    .setTitle(fileName)
                    .setDescription("Downloading from Hexium Nodes")
                    .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
                dm.enqueue(request)
                _uiState.value = _uiState.value.copy(isActionSuccess = true) // Treat download start as success for UI feedback
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Download failed: ${e.message}")
            }
        }
    }

    fun downloadSelected() {
        // Pterodactyl doesn't support downloading multiple files directly.
        // We first archive them, then download the archive.
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath
        val files = _uiState.value.selectedFiles.toList()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val archive = repository.compressFiles(serverId, currentPath, files)
                // Trigger download of the archive
                downloadFile(archive.attributes.name)
                // Reload to show the archive
                loadFiles(serverId, currentPath)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to download selected: ${e.message}")
            }
        }
    }

    fun uploadFile(uri: android.net.Uri, andDecompress: Boolean = false) {
        val serverId = serverId ?: return
        val currentPath = _uiState.value.currentPath

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
                val fileName = getFileName(uri, contentResolver) ?: "uploaded_file"

                contentResolver.openInputStream(uri)?.use { inputStream ->
                    repository.uploadFile(serverId, currentPath, fileName, inputStream, mimeType)
                }

                if (andDecompress) {
                    repository.decompressFile(serverId, currentPath, fileName)
                }

                loadFiles(serverId, currentPath)
                _uiState.value = _uiState.value.copy(isActionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Upload failed: ${e.message}")
            }
        }
    }

    private fun getFileName(uri: android.net.Uri, contentResolver: android.content.ContentResolver): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        name = cursor.getString(index)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                name = name.substring(cut + 1)
            }
        }
        return name
    }
}
