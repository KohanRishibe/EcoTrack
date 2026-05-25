package com.ecotrack.feature.dashboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.ecotrack.core.ui.components.EcoResourceContent
import com.ecotrack.core.ui.components.EcoShimmerDashboard
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.domain.model.ShoppingItem
import com.ecotrack.domain.model.ai.SmartShoppingSuggestion
import com.ecotrack.feature.dashboard.ui.components.UsageDonutChart

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
                emptyMessage = "Добавьте продукты в запасы",
                isEmpty = { it.totalProducts == 0 },
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
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically { it / 2 },
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = content.greeting,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
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
                    Text(
                        text = "В запасах ${content.totalProducts} продуктов",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TextButton(onClick = onOpenPhotoRecognize) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null)
                            Text("По фото", modifier = Modifier.padding(start = 4.dp))
                        }
                        TextButton(onClick = onOpenReceiptScan) {
                            Icon(Icons.Default.Receipt, contentDescription = null)
                            Text("Чек", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }

        if (cartItems.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Корзина покупок")
                        Text(
                            "Корзина (${cartItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    Text(
                        "Отметьте купленное — товар попадёт в запасы и исчезнет из корзины.",
                        style = MaterialTheme.typography.bodySmall,
                    )
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
                            Text(
                                item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    }
                }
            }
        }

        if (smartSuggestions.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Умные подсказки")
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Использовано / выброшено", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                UsageDonutChart(
                    used = content.usedCount,
                    wasted = content.wastedCount,
                    modifier = Modifier.size(160.dp),
                    contentDescription = "Диаграмма: использовано ${content.usedCount}, выброшено ${content.wastedCount}",
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text("Использовано: ${content.usedCount}")
                    Text("Выброшено: ${content.wastedCount}")
                }
            }
        }
    }
}
