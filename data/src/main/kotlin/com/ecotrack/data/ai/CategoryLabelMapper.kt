package com.ecotrack.data.ai

import com.ecotrack.domain.model.ProductCategory

object CategoryLabelMapper {

    private val keywordToCategory = listOf(
        listOf("milk", "cheese", "yogurt", "dairy", "молоко", "сыр", "йогурт", "творог") to ProductCategory.DAIRY,
        listOf("vegetable", "tomato", "carrot", "cucumber", "овощ", "помидор", "морков", "огурец", "салат") to ProductCategory.VEGETABLES,
        listOf("meat", "chicken", "beef", "pork", "мясо", "куриц", "говядин", "свинин") to ProductCategory.MEAT,
        listOf("fruit", "apple", "banana", "orange", "фрукт", "яблок", "банан", "апельсин") to ProductCategory.FRUITS,
        listOf("bread", "bakery", "bun", "хлеб", "выпечк", "булк") to ProductCategory.BAKERY,
        listOf("drink", "juice", "water", "beverage", "напит", "сок", "вода") to ProductCategory.BEVERAGES,
        listOf("frozen", "ice cream", "замороз", "морожен") to ProductCategory.FROZEN,
    )

    private val defaultShelfLifeDays = mapOf(
        ProductCategory.DAIRY to 7,
        ProductCategory.VEGETABLES to 5,
        ProductCategory.MEAT to 3,
        ProductCategory.FRUITS to 7,
        ProductCategory.BAKERY to 4,
        ProductCategory.BEVERAGES to 30,
        ProductCategory.FROZEN to 90,
        ProductCategory.OTHER to 7,
    )

    fun mapLabels(labels: List<String>): Pair<ProductCategory, Int> {
        val normalized = labels.map { it.lowercase() }
        for ((keywords, category) in keywordToCategory) {
            if (normalized.any { label -> keywords.any { keyword -> label.contains(keyword) } }) {
                return category to (defaultShelfLifeDays[category] ?: 7)
            }
        }
        return ProductCategory.OTHER to 7
    }

    fun shelfLifeDays(category: ProductCategory): Int =
        defaultShelfLifeDays[category] ?: 7

    fun suggestName(labels: List<String>): String =
        labels.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Продукт"
}
