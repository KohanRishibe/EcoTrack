package com.ecotrack.feature.ai.ui.photo

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.common.result.Resource
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.domain.model.ai.AiSource
import com.ecotrack.domain.model.ai.ProductPhotoInsight
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PhotoRecognizeScreen(
    onBack: () -> Unit,
    onApplyToAddProduct: (ProductPhotoInsight) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoRecognizeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var captureTrigger by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    EcoSnackbarEffect(
        message = state.snackbar,
        snackbarHostState = snackbarHostState,
        onDismiss = viewModel::dismissSnackbar,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Распознать по фото") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.ecoTouchTarget()) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
        snackbarHost = { EcoSnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (!cameraPermission.status.isGranted) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                        Text("Разрешить камеру")
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    PhotoCapturePreview(
                        onPhotoCaptured = viewModel::onPhotoCaptured,
                        captureTrigger = captureTrigger,
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (state.isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                Button(
                    onClick = { captureTrigger++ },
                    enabled = !state.isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .ecoTouchTarget(),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Сфотографировать", modifier = Modifier.padding(start = 8.dp))
                }
            }
            when (val insight = state.insight) {
                is Resource.Success -> {
                    PhotoInsightCard(
                        insight = insight.data,
                        onApply = { onApplyToAddProduct(insight.data) },
                        onRetry = {
                            viewModel.reset()
                            captureTrigger = 0
                        },
                        modifier = Modifier.padding(16.dp),
                    )
                }
                is Resource.Error -> {
                    Text(
                        insight.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun PhotoInsightCard(
    insight: ProductPhotoInsight,
    onApply: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Результат AI", style = MaterialTheme.typography.titleMedium)
            Text("Название: ${insight.suggestedName}")
            Text("Категория: ${insight.category.displayName}")
            Text(
                "Количество: ${ProductQuantity.formatQuantity(insight.suggestedQuantity, insight.suggestedUnit)}",
            )
            Text("Срок хранения: ~${insight.suggestedShelfLifeDays} дн. (до ${insight.suggestedExpiryDate})")
            Text(
                "Источник: ${if (insight.source == AiSource.GEMINI_NANO) "Gemini Nano" else "ML Kit"} · ${(insight.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
            )
            insight.detectedLabels.take(3).joinToString().let {
                Text("Метки: $it", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onApply, modifier = Modifier.fillMaxWidth()) {
                Text("Добавить в запасы")
            }
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                Text("Снять ещё раз")
            }
        }
    }
}
