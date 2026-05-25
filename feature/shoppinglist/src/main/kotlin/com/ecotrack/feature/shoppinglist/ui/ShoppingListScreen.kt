package com.ecotrack.feature.shoppinglist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
            emptyMessage = "Корзина пуста — добавьте товары для похода в магазин",
            isEmpty = { it.items.isEmpty() && it.templates.isEmpty() },
            loading = { EcoShimmerList() },
        ) { content ->
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text("Сбор корзины", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Отметьте купленное на главном экране — товары попадут в запасы.",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = state.newItemText,
                    onValueChange = onNewItemTextChange,
                    label = { Text("Добавить в корзину") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                )
                AssistChip(
                    onClick = onAddQuickItem,
                    label = { Text("Добавить") },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .ecoTouchTarget(),
                )
                if (content.templates.isNotEmpty()) {
                    Text(
                        "Частые покупки",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        content.templates.forEach { template ->
                            AssistChip(
                                onClick = { onAddFromTemplate(template) },
                                label = { Text(template.name) },
                                modifier = Modifier.ecoTouchTarget(),
                            )
                        }
                    }
                }
                Text(
                    "В корзине (${content.items.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(content.items, key = { it.id }) { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .ecoTouchTarget(),
                        ) {
                            Text(
                                item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                onClick = { onRemoveFromCart(item) },
                                modifier = Modifier.ecoTouchTarget(),
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Убрать из корзины",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
