package com.ecotrack.feature.addproduct.ui.scan.mapper

import com.ecotrack.domain.model.Product
import com.ecotrack.feature.addproduct.ui.scan.ScannedProductCardUi
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun Product.toScannedProductCardUi(barcode: String): ScannedProductCardUi = ScannedProductCardUi(
    productId = id,
    barcode = barcode,
    name = name,
    categoryLabel = category.displayName,
    expiryLabel = expiryDate.format(dateFormatter),
    quantityLabel = "$quantity $unit",
)

// Suppress unused import fix - remove BarcodeScanViewModel import from ViewModel file
