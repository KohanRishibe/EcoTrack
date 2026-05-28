package com.ecotrack.data.seed

import com.ecotrack.core.database.EcoTrackDatabase
import com.ecotrack.core.database.entity.ProductEntity
import com.ecotrack.core.database.entity.ShoppingItemEntity
import com.ecotrack.core.database.model.ProductCategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor() {

    fun seedIfEmpty(database: EcoTrackDatabase) {
        runBlocking {
            val products = database.productDao().observeAll().first()
            if (products.isNotEmpty()) return@runBlocking

            val today = LocalDate.now()
            val demoProducts = listOf(
                ProductEntity(
                    name = "Молоко 2.5%",
                    category = ProductCategoryEntity.DAIRY,
                    expiryDate = today.plusDays(2),
                    quantity = 1.0,
                    unit = "л",
                ),
                ProductEntity(
                    name = "Помидоры",
                    category = ProductCategoryEntity.VEGETABLES,
                    expiryDate = today.plusDays(5),
                    quantity = 500.0,
                    unit = "г",
                ),
                ProductEntity(
                    name = "Куриная грудка",
                    category = ProductCategoryEntity.MEAT,
                    expiryDate = today.plusDays(1),
                    quantity = 400.0,
                    unit = "г",
                    usedCount = 2,
                ),
                ProductEntity(
                    name = "Яблоки",
                    category = ProductCategoryEntity.FRUITS,
                    expiryDate = today.plusDays(10),
                    quantity = 6.0,
                    unit = "шт",
                ),
            )
            demoProducts.forEach { database.productDao().insert(it) }

            val templates = listOf(
                Triple("Молоко", ProductCategoryEntity.DAIRY, 1.0 to "л"),
                Triple("Хлеб", ProductCategoryEntity.BAKERY, 1.0 to "шт"),
                Triple("Яйца", ProductCategoryEntity.DAIRY, 10.0 to "шт"),
                Triple("Сыр", ProductCategoryEntity.DAIRY, 200.0 to "г"),
                Triple("Бананы", ProductCategoryEntity.FRUITS, 6.0 to "шт"),
            )
            templates.forEachIndexed { index, (name, category, qtyUnit) ->
                database.shoppingItemDao().insert(
                    ShoppingItemEntity(
                        name = name,
                        category = category,
                        quantity = qtyUnit.first,
                        unit = qtyUnit.second,
                        isTemplate = true,
                        sortOrder = index,
                    ),
                )
            }
        }
    }
}
