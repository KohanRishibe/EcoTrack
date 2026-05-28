package com.ecotrack.data.di

import android.content.Context
import androidx.room.Room
import com.ecotrack.core.database.EcoTrackDatabase
import com.ecotrack.core.database.dao.ConsumptionEventDao
import com.ecotrack.core.database.dao.ProductDao
import com.ecotrack.core.database.dao.ShoppingItemDao
import com.ecotrack.data.seed.DatabaseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        seeder: DatabaseSeeder,
    ): EcoTrackDatabase {
        val db = Room.databaseBuilder(
            context,
            EcoTrackDatabase::class.java,
            EcoTrackDatabase.DATABASE_NAME,
        ).fallbackToDestructiveMigration()
            .build()
        seeder.seedIfEmpty(db)
        return db
    }

    @Provides
    fun provideProductDao(database: EcoTrackDatabase): ProductDao =
        database.productDao()

    @Provides
    fun provideShoppingItemDao(database: EcoTrackDatabase): ShoppingItemDao =
        database.shoppingItemDao()

    @Provides
    fun provideConsumptionEventDao(database: EcoTrackDatabase): ConsumptionEventDao =
        database.consumptionEventDao()
}
