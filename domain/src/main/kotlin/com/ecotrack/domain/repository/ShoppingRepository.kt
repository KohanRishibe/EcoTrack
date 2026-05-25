package com.ecotrack.domain.repository

import com.ecotrack.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun observeItems(): Flow<List<ShoppingItem>>
    fun observeTemplates(): Flow<List<ShoppingItem>>
    suspend fun addItem(name: String)
    suspend fun addFromTemplate(templateId: Long)
    suspend fun getItem(id: Long): ShoppingItem?
    suspend fun deleteItem(id: Long)
}
