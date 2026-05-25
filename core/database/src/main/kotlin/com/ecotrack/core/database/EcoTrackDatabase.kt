package com.ecotrack.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ecotrack.core.database.converter.EcoTrackConverters
import com.ecotrack.core.database.dao.ProductDao
import com.ecotrack.core.database.dao.ShoppingItemDao
import com.ecotrack.core.database.entity.ProductEntity
import com.ecotrack.core.database.entity.ShoppingItemEntity

@Database(
    entities = [
        ProductEntity::class,
        ShoppingItemEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(EcoTrackConverters::class)
abstract class EcoTrackDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun shoppingItemDao(): ShoppingItemDao

    companion object {
        const val DATABASE_NAME = "ecotrack.db"
    }
}
