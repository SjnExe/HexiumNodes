package com.hexium.nodes.data

import com.hexium.nodes.core.model.*
import com.hexium.nodes.data.preferences.SecurePreferencesRepository
import com.hexium.nodes.data.remote.PterodactylService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

interface ConsoleSession {
    val incoming: Flow<String>
    fun send(command: String)
    fun close()
}

interface PterodactylRepository {
    suspend fun getServers(): List<ServerData>
    suspend fun getResources(serverId: String): ResourcesAttributes
    suspend fun sendPowerSignal(serverId: String, signal: PowerSignal)
    suspend fun getWebSocketAuth(serverId: String): WebSocketAuthData
    suspend fun listFiles(serverId: String, directory: String): List<FileData>
    suspend fun getFileContent(serverId: String, filePath: String): String
    suspend fun writeFile(serverId: String, filePath: String, content: String)
    suspend fun getDownloadUrl(serverId: String, filePath: String): String
    suspend fun uploadFile(serverId: String, directory: String, fileName: String, inputStream: java.io.InputStream, mimeType: String)
    suspend fun renameFile(serverId: String, root: String, from: String, to: String)
    suspend fun deleteFiles(serverId: String, root: String, files: List<String>)
    suspend fun compressFiles(serverId: String, root: String, files: List<String>): FileData
    suspend fun decompressFile(serverId: String, root: String, file: String)
    suspend fun createFolder(serverId: String, root: String, name: String)

    suspend fun createConsoleSession(serverId: String): ConsoleSession

    fun getApiKey(): String?
    fun setApiKey(key: String)
    fun clearApiKey()
}

@Singleton
class PterodactylRepositoryImpl @Inject constructor(
    private val service: PterodactylService,
    private val securePrefs: SecurePreferencesRepository,
    @param:Named("PterodactylOkHttp") private val client: OkHttpClient,
) : PterodactylRepository {

    override suspend fun getServers(): List<ServerData> = service.getServers().data

    override suspend fun getResources(serverId: String): ResourcesAttributes = service.getResources(serverId).attributes

    override suspend fun sendPowerSignal(serverId: String, signal: PowerSignal) {
        service.sendPowerSignal(serverId, PowerRequest(signal.signal))
    }

    override suspend fun getWebSocketAuth(serverId: String): WebSocketAuthData = service.getWebSocketAuth(serverId).data

    override suspend fun listFiles(serverId: String, directory: String): List<FileData> = service.listFiles(serverId, directory).data

    override suspend fun getFileContent(serverId: String, filePath: String): String = service.getFileContent(serverId, filePath).string()

    override suspend fun writeFile(serverId: String, filePath: String, content: String) {
        val requestBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
        service.writeFile(serverId, filePath, requestBody)
    }

    override suspend fun getDownloadUrl(serverId: String, filePath: String): String = service.getDownloadUrl(serverId, filePath).attributes.url

    override suspend fun uploadFile(serverId: String, directory: String, fileName: String, inputStream: java.io.InputStream, mimeType: String) {
        val uploadUrl = service.getUploadUrl(serverId).attributes.url
        val content = inputStream.readBytes()
        val requestBody = content.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = okhttp3.MultipartBody.Part.createFormData("files", fileName, requestBody)
        service.uploadFileToUrl(uploadUrl, part, directory)
    }

    override suspend fun renameFile(serverId: String, root: String, from: String, to: String) {
        service.renameFile(serverId, RenameFileRequest(root, listOf(RenameFileEntry(from, to))))
    }

    override suspend fun deleteFiles(serverId: String, root: String, files: List<String>) {
        service.deleteFiles(serverId, DeleteFilesRequest(root, files))
    }

    override suspend fun compressFiles(serverId: String, root: String, files: List<String>): FileData {
        return service.compressFiles(serverId, CompressFilesRequest(root, files))
    }

    override suspend fun decompressFile(serverId: String, root: String, file: String) {
        service.decompressFile(serverId, DecompressFileRequest(root, file))
    }

    override suspend fun createFolder(serverId: String, root: String, name: String) {
        service.createFolder(serverId, CreateFolderRequest(root, name))
    }

    override suspend fun createConsoleSession(serverId: String): ConsoleSession {
        val auth = getWebSocketAuth(serverId)
        return PterodactylConsoleSession(client, auth.socket, auth.token, securePrefs)
    }

    override fun getApiKey(): String? = securePrefs.getApiKey()

    override fun setApiKey(key: String) {
        securePrefs.setApiKey(key)
    }

    override fun clearApiKey() {
        securePrefs.clearApiKey()
    }
}

class PterodactylConsoleSession(
    private val client: OkHttpClient,
    private val socketUrl: String,
    private val token: String,
    private val securePrefs: SecurePreferencesRepository,
) : ConsoleSession {
    private var webSocket: WebSocket? = null

    override val incoming: Flow<String> = callbackFlow {
        val cookies = securePrefs.getCookies()
        val requestBuilder = Request.Builder()
            .url(socketUrl)
            .header("User-Agent", com.hexium.nodes.core.common.Constants.USER_AGENT)
            .header("Origin", "https://panel.hexiumnodes.cloud")

        if (!cookies.isNullOrBlank()) {
            requestBuilder.header("Cookie", cookies)
        }

        val request = requestBuilder.build()
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                this@PterodactylConsoleSession.webSocket = webSocket
                webSocket.send("""{"event":"auth","args":["$token"]}""")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySend(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                close(t)
            }
        }
        val ws = client.newWebSocket(request, listener)
        awaitClose { ws.close(1000, null) }
    }

    override fun send(command: String) {
        webSocket?.send("""{"event":"send","args":["$command"]}""")
    }

    override fun close() {
        webSocket?.close(1000, null)
    }
}
