package com.ecotrack.data.di

import com.ecotrack.data.catalog.ProductCatalogRepositoryImpl
import com.ecotrack.domain.repository.ProductCatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {
    @Binds
    @Singleton
    abstract fun bindProductCatalogRepository(impl: ProductCatalogRepositoryImpl): ProductCatalogRepository
}
