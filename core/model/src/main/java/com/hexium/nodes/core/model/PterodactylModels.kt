package com.hexium.nodes.core.model

import com.google.gson.annotations.SerializedName

data class ServerResponse(
    @SerializedName("data") val data: List<ServerData>,
    @SerializedName("meta") val meta: Meta?,
)

data class ServerData(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: ServerAttributes,
)

data class ServerAttributes(
    @SerializedName("server_owner") val serverOwner: Boolean,
    @SerializedName("identifier") val identifier: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("node") val node: String,
    @SerializedName("sftp_details") val sftpDetails: SftpDetails,
    @SerializedName("description") val description: String,
    @SerializedName("limits") val limits: ServerLimits,
    @SerializedName("feature_limits") val featureLimits: FeatureLimits,
    // Installing, etc.
    @SerializedName("status") val status: String?,
)

data class SftpDetails(
    @SerializedName("ip") val ip: String,
    @SerializedName("port") val port: Int,
)

data class ServerLimits(
    // MB
    @SerializedName("memory") val memory: Long,
    @SerializedName("swap") val swap: Long,
    @SerializedName("disk") val disk: Long,
    @SerializedName("io") val io: Long?,
    @SerializedName("cpu") val cpu: Long,
)

data class FeatureLimits(
    @SerializedName("databases") val databases: Int,
    @SerializedName("allocations") val allocations: Int,
    @SerializedName("backups") val backups: Int,
)

data class Meta(
    @SerializedName("pagination") val pagination: Pagination,
)

data class Pagination(
    @SerializedName("total") val total: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
)

// Resources
data class ResourcesResponse(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: ResourcesAttributes,
)

data class ResourcesAttributes(
    // running, stopping, etc.
    @SerializedName("current_state") val currentState: String,
    @SerializedName("is_suspended") val isSuspended: Boolean,
    @SerializedName("resources") val resources: Resources,
)

data class Resources(
    @SerializedName("memory_bytes") val memoryBytes: Long,
    @SerializedName("cpu_absolute") val cpuAbsolute: Double,
    @SerializedName("disk_bytes") val diskBytes: Long,
    @SerializedName("network_rx_bytes") val networkRxBytes: Long,
    @SerializedName("network_tx_bytes") val networkTxBytes: Long,
    // ms
    @SerializedName("uptime") val uptime: Long,
)

// Power Signal
enum class PowerSignal(val signal: String) {
    START("start"),
    STOP("stop"),
    RESTART("restart"),
    KILL("kill"),
}

data class PowerRequest(
    @SerializedName("signal") val signal: String,
)

// Files
data class FileListResponse(
    @SerializedName("data") val data: List<FileData>,
)

data class FileData(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: FileAttributes,
)

data class FileAttributes(
    @SerializedName("name") val name: String,
    @SerializedName("mode") val mode: String,
    @SerializedName("mode_bits") val modeBits: String,
    @SerializedName("size") val size: Long,
    @SerializedName("is_file") val isFile: Boolean,
    @SerializedName("is_symlink") val isSymlink: Boolean,
    @SerializedName("mimetype") val mimeType: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("modified_at") val modifiedAt: String,
)

// Download
data class DownloadUrlResponse(
    @SerializedName("attributes") val attributes: DownloadUrlAttributes,
)

data class DownloadUrlAttributes(
    @SerializedName("url") val url: String,
)

// Upload
data class UploadUrlResponse(
    @SerializedName("attributes") val attributes: UploadUrlAttributes
)

data class UploadUrlAttributes(
    @SerializedName("url") val url: String
)

// WebSocket Auth
data class WebSocketAuthResponse(
    @SerializedName("data") val data: WebSocketAuthData,
)

data class WebSocketAuthData(
    @SerializedName("token") val token: String,
    @SerializedName("socket") val socket: String,
)
