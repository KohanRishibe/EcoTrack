package com.ecotrack.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.core.ui.components.EcoResourceContent
import com.ecotrack.core.ui.components.EcoShimmerDashboard
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.domain.model.ShoppingItem
import com.ecotrack.domain.model.ai.SmartShoppingSuggestion
import com.ecotrack.core.design.components.EcoElevatedCard
import com.ecotrack.core.design.components.ReceiptLineItem
import com.ecotrack.core.design.components.ReceiptPaper
import com.ecotrack.core.design.components.ReceiptTotalRow
import com.ecotrack.feature.dashboard.ui.components.CategoryDistributionChart
import com.ecotrack.feature.dashboard.ui.components.DashboardStatsGrid
import com.ecotrack.feature.dashboard.ui.components.UsageOverviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onOpenSettings: () -> Unit,
    onOpenPhotoRecognize: () -> Unit,
    onOpenReceiptScan: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EcoSnackbarEffect(
        message = state.snackbar,
        snackbarHostState = snackbarHostState,
        onDismiss = viewModel::dismissSnackbar,
    )

    DashboardScreenContent(
        state = state,
        onOpenSettings = onOpenSettings,
        onOpenPhotoRecognize = onOpenPhotoRecognize,
        onOpenReceiptScan = onOpenReceiptScan,
        onRefresh = viewModel::refresh,
        onApplySmartSuggestions = viewModel::applySmartSuggestionsToShoppingList,
        onPurchaseCartItem = viewModel::purchaseCartItem,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    state: DashboardScreenUiState,
    onOpenSettings: () -> Unit,
    onOpenPhotoRecognize: () -> Unit,
    onOpenReceiptScan: () -> Unit,
    onRefresh: () -> Unit,
    onApplySmartSuggestions: () -> Unit,
    onPurchaseCartItem: (Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Scaffold(
        modifier = modifier,
        snackbarHost = { EcoSnackbarHost(snackbarHostState) },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            EcoResourceContent(
                resource = state.content,
                onRetry = onRefresh,
                loading = { EcoShimmerDashboard() },
            ) { content ->
                DashboardBody(
                    content = content,
                    cartItems = state.cartItems,
                    purchasingItemId = state.purchasingItemId,
                    smartSuggestions = state.smartSuggestions,
                    isApplyingSuggestions = state.isApplyingSuggestions,
                    onOpenSettings = onOpenSettings,
                    onOpenPhotoRecognize = onOpenPhotoRecognize,
                    onOpenReceiptScan = onOpenReceiptScan,
                    onApplySmartSuggestions = onApplySmartSuggestions,
                    onPurchaseCartItem = onPurchaseCartItem,
                )
            }
        }
    }
}

@Composable
private fun DashboardBody(
    content: DashboardContent,
    cartItems: List<ShoppingItem>,
    purchasingItemId: Long?,
    smartSuggestions: List<SmartShoppingSuggestion>,
    isApplyingSuggestions: Boolean,
    onOpenSettings: () -> Unit,
    onOpenPhotoRecognize: () -> Unit,
    onOpenReceiptScan: () -> Unit,
    onApplySmartSuggestions: () -> Unit,
    onPurchaseCartItem: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "EcoTrack",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Обзор запасов",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.ecoTouchTarget(),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Открыть настройки",
                )
            }
        }

        DashboardStatsGrid(
            totalProducts = content.totalProducts,
            totalUnits = content.totalUnits,
            expiringCount = content.expiringCount,
            expiredCount = content.expiredCount,
            cartCount = cartItems.size,
        )

        if (content.totalProducts == 0) {
            EcoElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Запасы пусты",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Добавьте продукты вручную, по фото или отметьте покупки в корзине ниже.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(onClick = onOpenPhotoRecognize) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("По фото", modifier = Modifier.padding(start = 4.dp))
            }
            TextButton(onClick = onOpenReceiptScan) {
                Icon(Icons.Default.List, contentDescription = null)
                Text("Чек", modifier = Modifier.padding(start = 4.dp))
            }
        }

        if (content.categoryStats.isNotEmpty()) {
            EcoElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Text("Запасы по категориям", style = MaterialTheme.typography.titleMedium)
                CategoryDistributionChart(
                    categories = content.categoryStats,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        UsageOverviewCard(
            used = content.usedCount,
            wasted = content.wastedCount,
            utilizationPercent = content.utilizationPercent,
        )

        if (cartItems.isNotEmpty()) {
            Text(
                "Касса — отметьте купленное",
                style = MaterialTheme.typography.titleMedium,
            )
            ReceiptPaper(
                modifier = Modifier.fillMaxWidth(),
                storeSubtitle = "К ОПЛАТЕ",
                footerLine = "✓ = куплено → в запасы",
            ) {
                cartItems.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .ecoTouchTarget(),
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = { checked ->
                                if (checked) onPurchaseCartItem(item.id)
                            },
                            enabled = purchasingItemId == null,
                        )
                        ReceiptLineItem(
                            name = item.name.uppercase(),
                            detail = item.category.displayName,
                            trailing = ProductQuantity.formatQuantity(item.quantity, item.unit),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                ReceiptTotalRow(
                    label = "К ОПЛАТЕ",
                    value = "${cartItems.size} поз.",
                    emphasized = true,
                )
            }
        }

        if (smartSuggestions.isNotEmpty()) {
            EcoElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = MaterialTheme.colorScheme.primary,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Умные подсказки",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            "Умные подсказки",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    smartSuggestions.forEach { suggestion ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(suggestion.productName, fontWeight = FontWeight.SemiBold)
                            Text(suggestion.reason, style = MaterialTheme.typography.bodySmall)
                            Text(
                                "До ${suggestion.predictedRunOutDate} · ${suggestion.suggestedQuantity}",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    Button(
                        onClick = onApplySmartSuggestions,
                        enabled = !isApplyingSuggestions,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Добавить в список покупок")
                    }
                }
            }
        }

        if (content.expiringCount > 0) {
            EcoElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                accentColor = MaterialTheme.colorScheme.error,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Предупреждение о сроке годности",
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            "Скоро истекает срок",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            content.expiringItems.take(3).joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

    }
}
