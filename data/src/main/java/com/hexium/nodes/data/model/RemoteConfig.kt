package com.hexium.nodes.data.model

data class RemoteConfig(
    val devAdLimit: Int = 50,
    val devAdRate: Double = 1.0,
    val devAdExpiry: Int = 24,
    val maintenance: Boolean = false,
    val minVersion: Int = 1,
    val testUsers: List<TestUser> = emptyList()
)

data class TestUser(
    val username: String,
    val password: String
)
