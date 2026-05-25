package com.ecotrack.domain.usecase.dashboard

import com.ecotrack.domain.model.DashboardSummary
import com.ecotrack.domain.repository.ProductRepository
import com.ecotrack.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<DashboardSummary> = combine(
        productRepository.observeProducts(),
        productRepository.observeExpiringSoon(7),
        productRepository.observeUsageStats(),
        settingsRepository.observeSettings(),
    ) { products, expiring, stats, settings ->
        DashboardSummary(
            userName = settings.userName,
            totalProducts = products.size,
            expiringSoon = expiring,
            usedCount = stats.first,
            wastedCount = stats.second,
        )
    }
}
