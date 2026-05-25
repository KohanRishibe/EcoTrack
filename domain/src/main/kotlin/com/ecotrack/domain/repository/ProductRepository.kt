package com.ecotrack.domain.repository

import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductConsumeResult
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    fun observeProduct(id: Long): Flow<Product?>
    fun observeExpiringSoon(withinDays: Int = 7): Flow<List<Product>>
    fun observeUsageStats(): Flow<Pair<Int, Int>>
    suspend fun addProduct(product: Product): Long
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Long)
    suspend fun markUsed(id: Long): ProductConsumeResult
    suspend fun markWasted(id: Long): ProductConsumeResult
    suspend fun lookupBarcode(barcode: String): Product?
    suspend fun getProductByBarcode(barcode: String): Product?
}
