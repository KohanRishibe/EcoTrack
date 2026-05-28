package com.ecotrack.data.catalog

import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.data.ai.CategoryLabelMapper
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.model.ProductDefaultsSuggestion
import com.ecotrack.domain.repository.ProductCatalogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductCatalogRepositoryImpl @Inject constructor() : ProductCatalogRepository {

    private data class ProductRule(
        val keywords: List<String>,
        val category: ProductCategory,
        val quantity: Double,
        val unit: String,
    )

    private val rules: List<ProductRule> = listOf(
        // Молочное — по продукту, не «всё в литрах»
        ProductRule(listOf("молоко", "кефир", "ряжен", "простокваш"), ProductCategory.DAIRY, 1.0, "л"),
        ProductRule(listOf("сыр", "творог", "брынз", "фета", "моцарел"), ProductCategory.DAIRY, 200.0, "г"),
        ProductRule(listOf("сливк", "сметан"), ProductCategory.DAIRY, 200.0, "г"),
        ProductRule(listOf("масло"), ProductCategory.DAIRY, 180.0, "г"),
        ProductRule(listOf("йогурт", "кефир"), ProductCategory.DAIRY, 1.0, "шт"),
        ProductRule(listOf("яйц"), ProductCategory.DAIRY, 10.0, "шт"),

        ProductRule(listOf("помидор", "огурец", "морков", "картоф", "лук", "перец", "капуст", "салат", "зелень"), ProductCategory.VEGETABLES, 1.0, "кг"),
        ProductRule(listOf("гриб"), ProductCategory.VEGETABLES, 300.0, "г"),

        ProductRule(listOf("куриц", "индейк", "говядин", "свинин", "фарш", "котлет", "колбас", "сосиск"), ProductCategory.MEAT, 500.0, "г"),

        ProductRule(listOf("яблок", "банан", "апельсин", "мандарин", "груш", "виноград", "лимон", "киви"), ProductCategory.FRUITS, 1.0, "кг"),
        ProductRule(listOf("ягод", "клубник", "малин"), ProductCategory.FRUITS, 300.0, "г"),

        ProductRule(listOf("хлеб", "батон", "булк", "багет"), ProductCategory.BAKERY, 1.0, "шт"),
        ProductRule(listOf("печень", "круассан"), ProductCategory.BAKERY, 4.0, "шт"),

        ProductRule(listOf("сок", "нектар"), ProductCategory.BEVERAGES, 1.0, "л"),
        ProductRule(listOf("вода", "лимонад", "кола"), ProductCategory.BEVERAGES, 1.5, "л"),
        ProductRule(listOf("чай", "кофе"), ProductCategory.BEVERAGES, 1.0, "уп"),

        ProductRule(listOf("морожен"), ProductCategory.FROZEN, 1.0, "уп"),
        ProductRule(listOf("замороз", "пельмен", "вареник"), ProductCategory.FROZEN, 1.0, "уп"),
    )

    private val quantityInNameRegex = Regex(
        """(\d+(?:[.,]\d+)?)\s*(кг|г|гр|л|мл|шт|уп)\b""",
        RegexOption.IGNORE_CASE,
    )

    override fun suggestFromName(productName: String): ProductDefaultsSuggestion {
        val parsed = parseQuantityFromName(productName)
        val baseName = parsed?.cleanName ?: productName.trim()

        val matchedRule = rules.firstOrNull { rule ->
            val lower = baseName.lowercase()
            rule.keywords.any { keyword -> lower.contains(keyword) }
        }

        val category = matchedRule?.category ?: CategoryLabelMapper.guessCategory(baseName)
        val quantity = parsed?.quantity ?: matchedRule?.quantity
            ?: CategoryLabelMapper.defaultQuantityAndUnit(category).first
        val unit = parsed?.unit ?: matchedRule?.unit
            ?: CategoryLabelMapper.defaultQuantityAndUnit(category).second
        val shelfLife = CategoryLabelMapper.shelfLifeDays(category)
        val formattedQty = ProductQuantity.formatQuantity(quantity, unit)

        val hint = buildString {
            append("AI: ")
            append(category.displayName)
            append(" · ")
            append(formattedQty)
            if (parsed == null && matchedRule != null) {
                append(" (типично для «${formatName(baseName)}»)")
            }
        }

        return ProductDefaultsSuggestion(
            category = category,
            quantity = quantity,
            unit = unit,
            shelfLifeDays = shelfLife,
            hint = hint,
        )
    }

    private data class ParsedQuantity(
        val cleanName: String,
        val quantity: Double,
        val unit: String,
    )

    private fun parseQuantityFromName(name: String): ParsedQuantity? {
        val match = quantityInNameRegex.find(name) ?: return null
        val qty = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: return null
        val unit = when (match.groupValues[2].lowercase()) {
            "г", "гр" -> "г"
            "кг" -> "кг"
            "л" -> "л"
            "мл" -> "мл"
            "уп" -> "уп"
            else -> "шт"
        }
        val cleanName = name.replace(match.value, "").replace(Regex("""\s+"""), " ").trim()
        if (cleanName.isBlank()) return null
        return ParsedQuantity(cleanName, qty, unit)
    }

    private fun formatName(name: String): String =
        name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
