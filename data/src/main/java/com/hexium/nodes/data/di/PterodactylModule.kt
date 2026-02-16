package com.hexium.nodes.data.di

import com.hexium.nodes.data.preferences.SecurePreferencesRepository
import com.hexium.nodes.data.remote.PterodactylService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PterodactylModule {

    @Provides
    @Singleton
    @Named("PterodactylOkHttp")
    fun providePterodactylOkHttpClient(
        securePrefs: SecurePreferencesRepository,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val apiKey = securePrefs.getApiKey()
            val request = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")

            if (!apiKey.isNullOrBlank()) {
                request.header("Authorization", "Bearer $apiKey")
            }

            chain.proceed(request.build())
        }
        .build()

    @Provides
    @Singleton
    fun providePterodactylService(
        @Named("PterodactylOkHttp") client: OkHttpClient,
    ): PterodactylService = Retrofit.Builder()
        .baseUrl("https://panel.hexiumnodes.cloud/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PterodactylService::class.java)
}
