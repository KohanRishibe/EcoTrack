package com.ecotrack.data.ai

import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.core.common.quantity.QuantityUnitType
import com.ecotrack.domain.model.Product
import com.ecotrack.domain.model.ai.SmartShoppingSuggestion
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.max

object SmartSuggestionEngine {

    fun generate(products: List<Product>, today: LocalDate = LocalDate.now()): List<SmartShoppingSuggestion> {
        return products.mapNotNull { product -> analyze(product, today) }
            .sortedBy { it.predictedRunOutDate }
            .take(5)
    }

    private fun analyze(product: Product, today: LocalDate): SmartShoppingSuggestion? {
        val createdDate = product.createdAt.atZone(ZoneId.systemDefault()).toLocalDate()
        val daysOwned = max(1, ChronoUnit.DAYS.between(createdDate, today))

        val step = ProductQuantity.consumeStep(product.quantity, product.unit)
        val dailyUsageAmount = if (product.usedCount > 0) {
            product.usedCount * step / daysOwned
        } else {
            0.0
        }
        val portionsLeft = ProductQuantity.portionsRemaining(product.quantity, product.unit)
        if (dailyUsageAmount <= step * 0.05 && portionsLeft > 2) {
            return null
        }

        val effectiveDailyUsage = if (dailyUsageAmount > 0) {
            dailyUsageAmount
        } else {
            estimateDailyUsageAmount(product)
        }
        val daysUntilEmpty = ceil(product.quantity / effectiveDailyUsage).toLong()
        val runOutDate = today.plusDays(daysUntilEmpty.coerceAtMost(60))

        if (daysUntilEmpty > 5) return null

        val reason = when {
            daysUntilEmpty <= 1 -> "Скорее всего закончится завтра"
            daysUntilEmpty <= 3 -> "Осталось примерно на $daysUntilEmpty дн."
            else -> "Рекомендуем пополнить запас"
        }

        return SmartShoppingSuggestion(
            productId = product.id,
            productName = product.name,
            reason = reason,
            predictedRunOutDate = runOutDate,
            suggestedQuantity = ProductQuantity.formatQuantity(product.quantity, product.unit),
        )
    }

    private fun estimateDailyUsageAmount(product: Product): Double = when (
        ProductQuantity.unitType(product.unit)
    ) {
        QuantityUnitType.DISCRETE -> when (product.category) {
            com.ecotrack.domain.model.ProductCategory.DAIRY -> 0.3
            com.ecotrack.domain.model.ProductCategory.VEGETABLES -> 0.4
            com.ecotrack.domain.model.ProductCategory.MEAT -> 0.25
            com.ecotrack.domain.model.ProductCategory.FRUITS -> 0.35
            com.ecotrack.domain.model.ProductCategory.BAKERY -> 0.5
            else -> 0.2
        }
        QuantityUnitType.WEIGHT -> when (product.category) {
            com.ecotrack.domain.model.ProductCategory.MEAT -> 80.0
            com.ecotrack.domain.model.ProductCategory.DAIRY -> 50.0
            com.ecotrack.domain.model.ProductCategory.VEGETABLES -> 60.0
            com.ecotrack.domain.model.ProductCategory.FRUITS -> 70.0
            com.ecotrack.domain.model.ProductCategory.BAKERY -> 40.0
            else -> 40.0
        }
        QuantityUnitType.VOLUME -> 200.0
    }
}
