package com.ecotrack.domain.usecase.dashboard

import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.domain.model.CategoryStat
import com.ecotrack.domain.model.DashboardSummary
import com.ecotrack.domain.repository.ConsumptionRepository
import com.ecotrack.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val consumptionRepository: ConsumptionRepository,
) {
    operator fun invoke(): Flow<DashboardSummary> = combine(
        productRepository.observeProducts(),
        productRepository.observeExpiringSoon(7),
        consumptionRepository.observeUsageTotals(),
    ) { products, expiring, stats ->
        val today = LocalDate.now()
        val categoryStats = products
            .groupBy { it.category.displayName }
            .map { (name, list) -> CategoryStat(name = name, count = list.size) }
            .sortedByDescending { it.count }
            .take(5)

        DashboardSummary(
            totalProducts = products.size,
            totalUnits = products
                .filter { ProductQuantity.contributesToDiscreteTotal(it.unit) }
                .sumOf { it.quantity }
                .toInt(),
            expiringSoon = expiring,
            expiredCount = products.count { it.expiryDate.isBefore(today) },
            usedCount = stats.first,
            wastedCount = stats.second,
            categoryStats = categoryStats,
        )
    }
}
