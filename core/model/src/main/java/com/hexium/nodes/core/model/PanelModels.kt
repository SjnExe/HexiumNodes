package com.hexium.nodes.core.model

import com.google.gson.annotations.SerializedName

// Backups
data class BackupListResponse(
    @SerializedName("data") val data: List<BackupData>,
)

data class BackupData(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: BackupAttributes,
)

data class BackupAttributes(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("name") val name: String,
    @SerializedName("ignored_files") val ignoredFiles: List<String>,
    @SerializedName("sha256_hash") val hash: String?,
    @SerializedName("bytes") val bytes: Long,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("completed_at") val completedAt: String?,
)

// Allocations
data class AllocationListResponse(
    @SerializedName("data") val data: List<AllocationData>,
)

data class AllocationData(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: AllocationAttributes,
)

data class AllocationAttributes(
    @SerializedName("id") val id: Int,
    @SerializedName("ip") val ip: String,
    @SerializedName("ip_alias") val ipAlias: String?,
    @SerializedName("port") val port: Int,
    @SerializedName("notes") val notes: String?,
    @SerializedName("is_default") val isDefault: Boolean,
)

data class AllocationNoteRequest(
    @SerializedName("notes") val notes: String,
)

// Users
data class UserListResponse(
    @SerializedName("data") val data: List<SubUserData>,
)

data class SubUserData(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: SubUserAttributes,
)

data class SubUserAttributes(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("image") val image: String?,
    @SerializedName("2fa_enabled") val twoFactorEnabled: Boolean,
    @SerializedName("created_at") val createdAt: String,
)

// Startup
data class StartupResponse(
    @SerializedName("data") val data: List<StartupVariableData>,
)

data class StartupVariableData(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: StartupVariableAttributes,
)

data class StartupVariableAttributes(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("env_variable") val envVariable: String,
    @SerializedName("default_value") val defaultValue: String,
    @SerializedName("server_value") val serverValue: String,
    @SerializedName("is_editable") val isEditable: Boolean,
    @SerializedName("rules") val rules: String,
)

// Subdomains
data class SubdomainListResponse(
    @SerializedName("data") val data: List<SubdomainData>,
)

data class SubdomainData(
    @SerializedName("object") val objectType: String,
    @SerializedName("attributes") val attributes: SubdomainAttributes,
)

data class SubdomainAttributes(
    @SerializedName("id") val id: Int,
    @SerializedName("domain") val domain: String,
    @SerializedName("created_at") val createdAt: String,
)

data class CreateSubdomainRequest(
    @SerializedName("domain") val domain: String,
)
