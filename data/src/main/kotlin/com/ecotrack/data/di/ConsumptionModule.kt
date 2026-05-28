package com.ecotrack.data.di

import com.ecotrack.data.repository.ConsumptionRepositoryImpl
import com.ecotrack.domain.repository.ConsumptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsumptionModule {
    @Binds
    @Singleton
    abstract fun bindConsumptionRepository(impl: ConsumptionRepositoryImpl): ConsumptionRepository
}
