package com.ecotrack.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ecotrack.core.database.converter.EcoTrackConverters
import com.ecotrack.core.database.dao.ConsumptionEventDao
import com.ecotrack.core.database.dao.ProductDao
import com.ecotrack.core.database.dao.ShoppingItemDao
import com.ecotrack.core.database.entity.ConsumptionEventEntity
import com.ecotrack.core.database.entity.ProductEntity
import com.ecotrack.core.database.entity.ShoppingItemEntity

@Database(
    entities = [
        ProductEntity::class,
        ShoppingItemEntity::class,
        ConsumptionEventEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
@TypeConverters(EcoTrackConverters::class)
abstract class EcoTrackDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun consumptionEventDao(): ConsumptionEventDao

    companion object {
        const val DATABASE_NAME = "ecotrack.db"
    }
}
