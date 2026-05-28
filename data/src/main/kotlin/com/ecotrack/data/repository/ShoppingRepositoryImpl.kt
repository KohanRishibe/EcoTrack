package com.ecotrack.data.repository

import com.ecotrack.core.database.dao.ProductDao
import com.ecotrack.core.database.dao.ShoppingItemDao
import com.ecotrack.core.database.entity.ShoppingItemEntity
import com.ecotrack.core.database.model.ProductCategoryEntity
import com.ecotrack.data.mapper.toDomain
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.data.shopping.ProductDefaultsResolver
import com.ecotrack.domain.model.ShoppingItem
import com.ecotrack.domain.repository.ProductCatalogRepository
import com.ecotrack.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingRepositoryImpl @Inject constructor(
    private val shoppingItemDao: ShoppingItemDao,
    private val productDao: ProductDao,
    private val productCatalog: ProductCatalogRepository,
) : ShoppingRepository {

    override fun observeItems(): Flow<List<ShoppingItem>> =
        shoppingItemDao.observeActiveItems().map { list -> list.map { it.toDomain() } }

    override fun observeTemplates(): Flow<List<ShoppingItem>> =
        shoppingItemDao.observeTemplates().map { list -> list.map { it.toDomain() } }

    override suspend fun addItem(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return

        val existing = findExistingProduct(trimmed)
        val defaults = ProductDefaultsResolver.resolve(trimmed, productCatalog, existing)
        val nextOrder = shoppingItemDao.getMaxSortOrder() + 1

        shoppingItemDao.insert(
            ShoppingItemEntity(
                name = defaults.displayName,
                category = defaults.category.toCategoryEntity(),
                quantity = defaults.quantity,
                unit = defaults.unit,
                isChecked = false,
                isTemplate = false,
                sortOrder = nextOrder,
            ),
        )
    }

    override suspend fun addFromTemplate(templateId: Long) {
        val template = shoppingItemDao.observeTemplates().first()
            .find { it.id == templateId } ?: return
        val nextOrder = shoppingItemDao.getMaxSortOrder() + 1
        shoppingItemDao.insert(
            template.copy(
                id = 0,
                isTemplate = false,
                isChecked = false,
                sortOrder = nextOrder,
            ),
        )
    }

    override suspend fun getItem(id: Long): ShoppingItem? =
        shoppingItemDao.getById(id)?.toDomain()

    override suspend fun deleteItem(id: Long) {
        shoppingItemDao.deleteById(id)
    }

    private suspend fun findExistingProduct(name: String) =
        productDao.observeAll().first()
            .map { it.toDomain() }
            .find { ProductDefaultsResolver.normalizeName(it.name) == ProductDefaultsResolver.normalizeName(name) }
}

private fun ProductCategory.toCategoryEntity(): ProductCategoryEntity = when (this) {
    ProductCategory.DAIRY -> ProductCategoryEntity.DAIRY
    ProductCategory.VEGETABLES -> ProductCategoryEntity.VEGETABLES
    ProductCategory.MEAT -> ProductCategoryEntity.MEAT
    ProductCategory.FRUITS -> ProductCategoryEntity.FRUITS
    ProductCategory.BAKERY -> ProductCategoryEntity.BAKERY
    ProductCategory.BEVERAGES -> ProductCategoryEntity.BEVERAGES
    ProductCategory.FROZEN -> ProductCategoryEntity.FROZEN
    ProductCategory.OTHER -> ProductCategoryEntity.OTHER
}
