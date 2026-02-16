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
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, currentPath = path)
            try {
                // If path is root, some APIs expect empty string or "/"?
                // Pterodactyl typically uses "/" for root or empty.
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
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Download failed: ${e.message}")
            }
        }
    }

    fun uploadFile(uri: android.net.Uri) {
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
                loadFiles(serverId, currentPath)
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
