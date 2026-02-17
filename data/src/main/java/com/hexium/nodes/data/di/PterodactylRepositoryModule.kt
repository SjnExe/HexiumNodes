package com.hexium.nodes.data.di

import com.hexium.nodes.data.PterodactylRepository
import com.hexium.nodes.data.PterodactylRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PterodactylRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPterodactylRepository(
        impl: PterodactylRepositoryImpl,
    ): PterodactylRepository
}
