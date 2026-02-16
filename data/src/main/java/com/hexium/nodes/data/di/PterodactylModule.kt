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
        baseClient: OkHttpClient,
        securePrefs: SecurePreferencesRepository,
    ): OkHttpClient = baseClient.newBuilder()
        .addInterceptor { chain ->
            val original = chain.request()
            val apiKey = securePrefs.getApiKey()
            val request = original.newBuilder()
                .header("Accept", "application/vnd.pterodactyl.v1+json")
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Referer", "https://panel.hexiumnodes.cloud/")
                .header("Origin", "https://panel.hexiumnodes.cloud")

            if (!apiKey.isNullOrBlank()) {
                val token = if (apiKey.startsWith("Bearer ")) apiKey else "Bearer $apiKey"
                request.header("Authorization", token)
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
