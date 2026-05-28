package com.ecotrack.data.shopping

import com.ecotrack.data.ai.CategoryLabelMapper
import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.repository.ProductCatalogRepository

data class ResolvedProductDefaults(
    val displayName: String,
    val category: ProductCategory,
    val quantity: Double,
    val unit: String,
    val shelfLifeDays: Int,
)

object ProductDefaultsResolver {

    private val quantityInNameRegex = Regex(
        """(\d+(?:[.,]\d+)?)\s*(кг|г|гр|л|мл|шт|уп)\b""",
        RegexOption.IGNORE_CASE,
    )

    fun resolve(
        rawName: String,
        catalog: ProductCatalogRepository,
        existingProduct: Product? = null,
    ): ResolvedProductDefaults {
        val withoutAi = rawName.replace(Regex("""\s*\(AI\)\s*$"""), "").trim()
        val parsed = parseQuantityFromName(withoutAi)
        val baseName = parsed?.cleanName ?: withoutAi

        if (existingProduct != null) {
            return ResolvedProductDefaults(
                displayName = formatDisplayName(baseName),
                category = existingProduct.category,
                quantity = parsed?.quantity ?: existingProduct.quantity,
                unit = parsed?.unit ?: existingProduct.unit,
                shelfLifeDays = CategoryLabelMapper.shelfLifeDays(existingProduct.category),
            )
        }

        val catalogSuggestion = catalog.suggestFromName(baseName)

        return ResolvedProductDefaults(
            displayName = formatDisplayName(baseName),
            category = catalogSuggestion.category,
            quantity = parsed?.quantity ?: catalogSuggestion.quantity,
            unit = parsed?.unit ?: catalogSuggestion.unit,
            shelfLifeDays = catalogSuggestion.shelfLifeDays,
        )
    }

    fun normalizeName(name: String): String =
        name.lowercase().replace(Regex("""\s+"""), " ").trim()

    private data class ParsedQuantity(
        val cleanName: String,
        val quantity: Double,
        val unit: String,
    )

    private fun parseQuantityFromName(name: String): ParsedQuantity? {
        val match = quantityInNameRegex.find(name) ?: return null
        val qty = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: return null
        val unit = normalizeUnit(match.groupValues[2])
        val cleanName = name.replace(match.value, "").replace(Regex("""\s+"""), " ").trim()
        if (cleanName.isBlank()) return null
        return ParsedQuantity(cleanName = cleanName, quantity = qty, unit = unit)
    }

    private fun normalizeUnit(raw: String): String = when (raw.lowercase()) {
        "г", "гр" -> "г"
        "кг" -> "кг"
        "л" -> "л"
        "мл" -> "мл"
        "уп" -> "уп"
        else -> "шт"
    }

    private fun formatDisplayName(name: String): String =
        name.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
