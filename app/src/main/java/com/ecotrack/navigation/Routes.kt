package com.ecotrack.navigation

import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

@Serializable
data object InventoryRoute

@Serializable
data object ShoppingListRoute

@Serializable
data object BarcodeScanRoute

@Serializable
data class AddProductRoute(
    val barcode: String? = null,
    val suggestedName: String? = null,
    val suggestedCategory: String? = null,
    val suggestedExpiryDate: String? = null,
)

@Serializable
data class ProductDetailRoute(val productId: Long)

@Serializable
data object SettingsRoute

@Serializable
data object PhotoRecognizeRoute

@Serializable
data object ReceiptScanRoute
