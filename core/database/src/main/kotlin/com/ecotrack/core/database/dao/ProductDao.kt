package com.ecotrack.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ecotrack.core.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY category, expiryDate ASC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun observeById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE expiryDate <= :maxDate ORDER BY expiryDate ASC")
    fun observeExpiringSoon(maxDate: LocalDate): Flow<List<ProductEntity>>

    @Query("SELECT SUM(usedCount) FROM products")
    fun observeTotalUsed(): Flow<Int?>

    @Query("SELECT SUM(wastedCount) FROM products")
    fun observeTotalWasted(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long)
}
