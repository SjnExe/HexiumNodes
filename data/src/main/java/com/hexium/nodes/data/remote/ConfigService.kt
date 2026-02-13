package com.hexium.nodes.data.remote

import com.hexium.nodes.data.model.RemoteConfig
import retrofit2.http.GET

interface ConfigService {
    @GET
    suspend fun getConfig(@retrofit2.http.Url url: String): RemoteConfig
}
