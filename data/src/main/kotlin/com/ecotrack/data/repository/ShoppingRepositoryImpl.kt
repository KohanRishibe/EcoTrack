package com.ecotrack.data.repository

import com.ecotrack.core.database.dao.ShoppingItemDao
import com.ecotrack.core.database.entity.ShoppingItemEntity
import com.ecotrack.data.mapper.toDomain
import com.ecotrack.domain.model.ShoppingItem
import com.ecotrack.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingRepositoryImpl @Inject constructor(
    private val shoppingItemDao: ShoppingItemDao,
) : ShoppingRepository {

    override fun observeItems(): Flow<List<ShoppingItem>> =
        shoppingItemDao.observeActiveItems().map { list -> list.map { it.toDomain() } }

    override fun observeTemplates(): Flow<List<ShoppingItem>> =
        shoppingItemDao.observeTemplates().map { list -> list.map { it.toDomain() } }

    override suspend fun addItem(name: String) {
        val nextOrder = shoppingItemDao.getMaxSortOrder() + 1
        shoppingItemDao.insert(
            ShoppingItemEntity(
                name = name.trim(),
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
}
