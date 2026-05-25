package com.ecotrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ecotrack.core.database.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items WHERE isTemplate = 0 ORDER BY sortOrder ASC, id ASC")
    fun observeActiveItems(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getById(id: Long): ShoppingItemEntity?

    @Query("SELECT COALESCE(MAX(sortOrder), -1) FROM shopping_items WHERE isTemplate = 0")
    suspend fun getMaxSortOrder(): Int

    @Query("SELECT * FROM shopping_items WHERE isTemplate = 1 ORDER BY sortOrder ASC")
    fun observeTemplates(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItemEntity): Long

    @Update
    suspend fun update(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
