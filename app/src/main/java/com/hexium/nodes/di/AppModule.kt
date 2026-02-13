package com.hexium.nodes.di

import android.content.Context
import android.content.SharedPreferences
import com.hexium.nodes.data.AdRepository
import com.hexium.nodes.data.MockAdRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAdRepository(
        mockAdRepository: MockAdRepository,
    ): AdRepository

    companion object {
        @Provides
        @Singleton
        fun provideSharedPreferences(
            @ApplicationContext context: Context,
        ): SharedPreferences {
            return context.getSharedPreferences("hexium_prefs", Context.MODE_PRIVATE)
        }
    }
}
