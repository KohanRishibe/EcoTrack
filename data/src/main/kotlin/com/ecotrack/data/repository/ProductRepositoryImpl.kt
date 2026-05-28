package com.ecotrack.data.repository

import com.ecotrack.core.database.dao.ProductDao
import com.ecotrack.core.network.api.OpenFoodFactsApi
import com.ecotrack.data.mapper.toDomain
import com.ecotrack.data.mapper.toEntity
import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.domain.model.ConsumptionEventType
import com.ecotrack.domain.model.ConsumptionRecord
import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductConsumeResult
import com.ecotrack.domain.repository.ConsumptionRepository
import com.ecotrack.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val openFoodFactsApi: OpenFoodFactsApi,
    private val consumptionRepository: ConsumptionRepository,
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeProduct(id: Long): Flow<Product?> =
        productDao.observeById(id).map { it?.toDomain() }

    override fun observeExpiringSoon(withinDays: Int): Flow<List<Product>> {
        val maxDate = LocalDate.now().plusDays(withinDays.toLong())
        return productDao.observeExpiringSoon(maxDate)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeUsageStats(): Flow<Pair<Int, Int>> = combine(
        productDao.observeTotalUsed(),
        productDao.observeTotalWasted(),
    ) { used, wasted ->
        (used ?: 0) to (wasted ?: 0)
    }

    override suspend fun addProduct(product: Product): Long =
        productDao.insert(product.toEntity())

    override suspend fun updateProduct(product: Product) {
        productDao.update(product.toEntity())
    }

    override suspend fun deleteProduct(id: Long) {
        productDao.deleteById(id)
    }

    override suspend fun markUsed(id: Long): ProductConsumeResult =
        consumeProduct(id, wasted = false)

    override suspend fun markWasted(id: Long): ProductConsumeResult =
        consumeProduct(id, wasted = true)

    private suspend fun consumeProduct(id: Long, wasted: Boolean): ProductConsumeResult {
        val entity = productDao.getById(id) ?: return ProductConsumeResult.ALREADY_EMPTY
        if (entity.quantity <= 0) return ProductConsumeResult.ALREADY_EMPTY

        val decrement = ProductQuantity.consumeStep(entity.quantity, entity.unit)
        val newQuantity = entity.quantity - decrement
        val product = entity.toDomain()

        consumptionRepository.recordConsumption(
            ConsumptionRecord(
                productName = product.name,
                category = product.category,
                amount = decrement,
                unit = product.unit,
                eventType = if (wasted) ConsumptionEventType.WASTED else ConsumptionEventType.USED,
            ),
        )

        return if (newQuantity <= 0) {
            productDao.deleteById(id)
            ProductConsumeResult.DEPLETED_AND_REMOVED
        } else {
            productDao.update(
                entity.copy(
                    quantity = newQuantity,
                    usedCount = entity.usedCount + if (wasted) 0 else 1,
                    wastedCount = entity.wastedCount + if (wasted) 1 else 0,
                ),
            )
            ProductConsumeResult.CONSUMED
        }
    }

    override suspend fun lookupBarcode(barcode: String): Product? {
        val dto = openFoodFactsApi.lookupBarcode(barcode) ?: return null
        return dto.toDomain(barcode)
    }

    override suspend fun getProductByBarcode(barcode: String): Product? =
        productDao.getByBarcode(barcode)?.toDomain()
}
