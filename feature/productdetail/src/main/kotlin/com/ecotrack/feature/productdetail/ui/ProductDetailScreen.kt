package com.ecotrack.feature.productdetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.ecotrack.core.ui.components.EcoResourceContent
import com.ecotrack.core.ui.components.EcoShimmerList
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
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
        ProductDetailScreenContent(
            state = state,
            onBack = onBack,
            onUsed = viewModel::onUsed,
            onWasted = viewModel::onWasted,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun ProductDetailScreenContent(
    state: ProductDetailScreenUiState,
    onBack: () -> Unit,
    onUsed: () -> Unit,
    onWasted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EcoResourceContent(
        resource = state.content,
        onRetry = onBack,
        loading = { EcoShimmerList(itemCount = 2) },
        modifier = modifier.fillMaxSize(),
    ) { product ->
        ProductDetailBody(
            product = product,
            onUsed = onUsed,
            onWasted = onWasted,
            onBack = onBack,
        )
    }
}

@Composable
private fun ProductDetailBody(
    product: ProductDetailUi,
    onUsed: () -> Unit,
    onWasted: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = "Изображение продукта ${product.name}",
                        modifier = Modifier.size(120.dp),
                    )
                }
                Text(product.name, style = MaterialTheme.typography.headlineMedium)
                Text(product.categoryLabel, style = MaterialTheme.typography.bodyLarge)
                Text("Годен до: ${product.expiryLabel}")
                Text("Количество: ${product.quantityLabel}")
            }
        }
        Card(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Статистика", style = MaterialTheme.typography.titleMedium)
                Text("С этой партии — использовано: ${product.usedCount}")
                Text("С этой партии — выброшено: ${product.wastedCount}")
                Text(
                    "Общая статистика — на главной (журнал расхода)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = if (product.canConsume) {
                "За одно нажатие списывается ${product.consumeStepLabel}. При нуле продукт удаляется из запасов."
            } else {
                "Запас исчерпан"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onUsed,
                enabled = product.canConsume,
                modifier = Modifier
                    .weight(1f)
                    .ecoTouchTarget(),
            ) {
                Text("Использован")
            }
            OutlinedButton(
                onClick = onWasted,
                enabled = product.canConsume,
                modifier = Modifier
                    .weight(1f)
                    .ecoTouchTarget(),
            ) {
                Text("Выброшен")
            }
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .ecoTouchTarget(),
        ) {
            Text("Назад")
        }
    }
}
