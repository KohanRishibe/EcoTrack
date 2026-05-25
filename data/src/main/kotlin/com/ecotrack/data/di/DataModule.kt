package com.ecotrack.data.di

import com.ecotrack.data.repository.AiRepositoryImpl
import com.ecotrack.data.repository.ProductRepositoryImpl
import com.ecotrack.data.repository.SettingsRepositoryImpl
import com.ecotrack.data.repository.ShoppingRepositoryImpl
import com.ecotrack.domain.repository.AiRepository
import com.ecotrack.domain.repository.ProductRepository
import com.ecotrack.domain.repository.SettingsRepository
import com.ecotrack.domain.repository.ShoppingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindShoppingRepository(impl: ShoppingRepositoryImpl): ShoppingRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository
}
