package com.hexium.nodes.data.di

import com.hexium.nodes.data.remote.ConfigService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://localhost/") // Base URL is ignored/overridden by @Url
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideConfigService(retrofit: Retrofit): ConfigService {
        return retrofit.create(ConfigService::class.java)
    }
}
