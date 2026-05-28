package com.ecotrack.feature.productdetail.domain

import com.ecotrack.core.common.quantity.ProductQuantity
import com.ecotrack.domain.model.Product
import com.ecotrack.feature.productdetail.ui.ProductDetailUi
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun Product.toDetailUi(): ProductDetailUi = ProductDetailUi(
    id = id,
    name = name,
    categoryLabel = category.displayName,
    expiryLabel = expiryDate.format(formatter),
    quantityLabel = ProductQuantity.formatQuantity(quantity, unit),
    consumeStepLabel = ProductQuantity.consumeStepLabel(quantity, unit),
    usedCount = usedCount,
    wastedCount = wastedCount,
    canConsume = quantity > 0,
    imageUrl = imageUrl,
)
