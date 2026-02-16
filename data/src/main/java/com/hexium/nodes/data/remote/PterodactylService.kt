package com.hexium.nodes.data.remote

import com.hexium.nodes.core.model.*
import retrofit2.http.*

interface PterodactylService {
    @GET("api/client")
    suspend fun getServers(): ServerResponse

    @GET("api/client/servers/{id}/resources")
    suspend fun getResources(@Path("id") serverId: String): ResourcesResponse

    @POST("api/client/servers/{id}/power")
    suspend fun sendPowerSignal(@Path("id") serverId: String, @Body signal: PowerRequest)

    @GET("api/client/servers/{id}/websocket")
    suspend fun getWebSocketAuth(@Path("id") serverId: String): WebSocketAuthResponse

    @GET("api/client/servers/{id}/files/list")
    suspend fun listFiles(@Path("id") serverId: String, @Query("directory") directory: String): FileListResponse

    @GET("api/client/servers/{id}/files/download")
    suspend fun getDownloadUrl(@Path("id") serverId: String, @Query("file") filePath: String): DownloadUrlResponse

    @GET("api/client/servers/{id}/files/upload")
    suspend fun getUploadUrl(@Path("id") serverId: String): UploadUrlResponse

    @Multipart
    @POST
    suspend fun uploadFileToUrl(@Url url: String, @Part file: okhttp3.MultipartBody.Part, @Query("directory") directory: String? = null)

    // File content handling (Use ResponseBody for raw content)
    @GET("api/client/servers/{id}/files/contents")
    suspend fun getFileContent(@Path("id") serverId: String, @Query("file") filePath: String): okhttp3.ResponseBody

    @POST("api/client/servers/{id}/files/write")
    suspend fun writeFile(@Path("id") serverId: String, @Query("file") filePath: String, @Body content: okhttp3.RequestBody)

    @PUT("api/client/servers/{id}/files/rename")
    suspend fun renameFile(@Path("id") serverId: String, @Body request: RenameFileRequest)

    @POST("api/client/servers/{id}/files/delete")
    suspend fun deleteFiles(@Path("id") serverId: String, @Body request: DeleteFilesRequest)

    @POST("api/client/servers/{id}/files/compress")
    suspend fun compressFiles(@Path("id") serverId: String, @Body request: CompressFilesRequest): FileData

    @POST("api/client/servers/{id}/files/decompress")
    suspend fun decompressFile(@Path("id") serverId: String, @Body request: DecompressFileRequest)

    @POST("api/client/servers/{id}/files/create-folder")
    suspend fun createFolder(@Path("id") serverId: String, @Body request: CreateFolderRequest)
}
