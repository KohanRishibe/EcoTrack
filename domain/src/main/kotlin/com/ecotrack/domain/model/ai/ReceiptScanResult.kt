package com.ecotrack.domain.model.ai

data class ReceiptScanResult(
    val items: List<ReceiptLineItem>,
    val rawText: String,
)

data class ReceiptLineItem(
    val name: String,
    val quantity: Double? = null,
    val unit: String? = null,
    val price: Double? = null,
)
