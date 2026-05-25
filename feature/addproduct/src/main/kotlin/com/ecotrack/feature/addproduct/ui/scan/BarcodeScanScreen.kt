package com.ecotrack.feature.addproduct.ui.scan

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ecotrack.core.ui.components.EcoSnackbarEffect
import com.ecotrack.core.ui.components.EcoSnackbarHost
import com.ecotrack.core.ui.util.ecoTouchTarget
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScanScreen(
    onBack: () -> Unit,
    onOpenAddProduct: (String) -> Unit,
    onOpenProductDetail: (Long) -> Unit,
    onManualEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BarcodeScanViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val scanFeedback = rememberScanFeedback()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BarcodeScanEvent.OpenAddProduct -> onOpenAddProduct(event.barcode)
                is BarcodeScanEvent.OpenProductDetail -> onOpenProductDetail(event.productId)
            }
        }
    }

    EcoSnackbarEffect(
        message = state.snackbar,
        snackbarHostState = snackbarHostState,
        onDismiss = viewModel::dismissSnackbar,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Сканировать штрихкод") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.ecoTouchTarget(),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onManualEntry,
                        modifier = Modifier.ecoTouchTarget(),
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Ввести вручную")
                    }
                },
            )
        },
        snackbarHost = { EcoSnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                !cameraPermission.status.isGranted -> {
                    CameraPermissionContent(
                        shouldShowRationale = cameraPermission.status.shouldShowRationale,
                        onRequestPermission = { cameraPermission.launchPermissionRequest() },
                    )
                }
                else -> {
                    BarcodeCameraPreview(
                        scanEnabled = state.isScanningActive && state.scannedProduct == null,
                        onBarcodeDetected = { barcode ->
                            scanFeedback.onScanSuccess()
                            viewModel.onBarcodeScanned(barcode)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    ScanOverlayHint(
                        modifier = Modifier.align(Alignment.Center),
                    )
                    state.scannedProduct?.let { product ->
                        ScannedProductCard(
                            product = product,
                            onOpenDetail = viewModel::onOpenProductDetail,
                            onScanAgain = viewModel::onScanAgain,
                            onDismiss = viewModel::onDismissProductCard,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanOverlayHint(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.75f)
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Наведите камеру на штрихкод",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun CameraPermissionContent(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (shouldShowRationale) {
                "Для сканирования штрихкодов нужен доступ к камере. Разрешите его в настройках или нажмите кнопку ниже."
            } else {
                "Разрешите доступ к камере для сканирования штрихкодов"
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .padding(top = 16.dp)
                .ecoTouchTarget(),
        ) {
            Text("Разрешить камеру")
        }
    }
}

@Composable
private fun ScannedProductCard(
    product: ScannedProductCardUi,
    onOpenDetail: () -> Unit,
    onScanAgain: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Продукт в запасах",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(product.name, style = MaterialTheme.typography.headlineSmall)
            Text("${product.categoryLabel} · до ${product.expiryLabel}")
            Text("Количество: ${product.quantityLabel}")
            Text(
                "Штрихкод: ${product.barcode}",
                style = MaterialTheme.typography.bodySmall,
            )
            Button(
                onClick = onOpenDetail,
                modifier = Modifier
                    .fillMaxWidth()
                    .ecoTouchTarget(),
            ) {
                Text("Открыть карточку")
            }
            OutlinedButton(
                onClick = onScanAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .ecoTouchTarget(),
            ) {
                Text("Сканировать снова")
            }
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .ecoTouchTarget(),
            ) {
                Text("Закрыть")
            }
        }
    }
}
