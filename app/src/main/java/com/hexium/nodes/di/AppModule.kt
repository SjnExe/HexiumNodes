package com.hexium.nodes.di

import com.hexium.nodes.data.AdRepository
import com.hexium.nodes.data.MockAdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAdRepository(
        mockAdRepository: MockAdRepository
    ): AdRepository
}
