package com.ecotrack.feature.shoppinglist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.core.design.components.ReceiptDashedDivider
import com.ecotrack.core.design.components.ReceiptLineItem
import com.ecotrack.core.design.components.ReceiptPaper
import com.ecotrack.core.design.components.ReceiptTotalRow
import com.ecotrack.core.design.components.EcoPillChip
import com.ecotrack.core.design.theme.EcoReceiptFontFamily
import com.ecotrack.core.design.theme.ReceiptInkMuted
import com.ecotrack.core.ui.components.EcoResourceContent
import com.ecotrack.core.ui.components.EcoShimmerList
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.domain.model.ShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    viewModel: ShoppingListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EcoSnackbarEffect(
        message = state.snackbar,
        snackbarHostState = snackbarHostState,
        onDismiss = viewModel::dismissSnackbar,
    )

    Scaffold(
        modifier = modifier,
        snackbarHost = { EcoSnackbarHost(snackbarHostState) },
    ) { padding ->
        ShoppingListScreenContent(
            state = state,
            onRefresh = viewModel::refresh,
            onNewItemTextChange = viewModel::onNewItemTextChange,
            onAddQuickItem = viewModel::addQuickItem,
            onAddFromTemplate = viewModel::addFromTemplate,
            onRemoveFromCart = viewModel::removeFromCart,
            modifier = Modifier.padding(padding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShoppingListScreenContent(
    state: ShoppingListScreenUiState,
    onRefresh: () -> Unit,
    onNewItemTextChange: (String) -> Unit,
    onAddQuickItem: () -> Unit,
    onAddFromTemplate: (ShoppingItem) -> Unit,
    onRemoveFromCart: (ShoppingItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        EcoResourceContent(
            resource = state.content,
            onRetry = onRefresh,
            emptyMessage = "Пустой чек — добавьте позиции ниже",
            isEmpty = { it.items.isEmpty() && it.templates.isEmpty() },
            loading = { EcoShimmerList() },
        ) { content ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Корзина",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Соберите список в магазин. Купленное отмечайте на главной.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = state.newItemText,
                        onValueChange = onNewItemTextChange,
                        label = { Text("Новая позиция") },
                        placeholder = { Text("Молоко, хлеб…") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                        ),
                        singleLine = true,
                    )
                    FilledTonalButton(
                        onClick = onAddQuickItem,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Добавить в чек",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }

                if (content.templates.isNotEmpty()) {
                    Text(
                        "Частые покупки",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        content.templates.forEach { template ->
                            EcoPillChip(
                                label = template.name,
                                onClick = { onAddFromTemplate(template) },
                            )
                        }
                    }
                }

                ReceiptPaper(
                    modifier = Modifier.fillMaxWidth(),
                    storeSubtitle = "ЧЕК ПОКУПОК",
                    footerLine = "Спасибо за осознанные покупки ♻",
                ) {
                    if (content.items.isEmpty()) {
                        Text(
                            text = "— пусто —",
                            fontFamily = EcoReceiptFontFamily,
                            color = ReceiptInkMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                        )
                    } else {
                        content.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                            ) {
                                ReceiptLineItem(
                                    name = item.name.uppercase(),
                                    detail = item.category.displayName,
                                    trailing = ProductQuantity.formatQuantity(item.quantity, item.unit),
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = { onRemoveFromCart(item) },
                                    modifier = Modifier.ecoTouchTarget(),
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Убрать из корзины",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    )
                                }
                            }
                        }
                        ReceiptTotalRow(
                            label = "ПОЗИЦИЙ",
                            value = content.items.size.toString(),
                            emphasized = true,
                        )
                        ReceiptDashedDivider(
                            color = ReceiptInkMuted.copy(alpha = 0.4f),
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        Text(
                            text = "Купленное отмечайте на главном экране",
                            fontFamily = EcoReceiptFontFamily,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = ReceiptInkMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}
