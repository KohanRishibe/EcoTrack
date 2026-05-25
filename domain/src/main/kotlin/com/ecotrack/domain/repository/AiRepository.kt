package com.ecotrack.domain.repository

import android.graphics.Bitmap
import com.ecotrack.domain.model.ai.ProductPhotoInsight
import com.ecotrack.domain.model.ai.ReceiptScanResult
import com.ecotrack.domain.model.ai.SmartShoppingSuggestion

interface AiRepository {
    suspend fun recognizeProductFromPhoto(bitmap: Bitmap): ProductPhotoInsight?
    suspend fun parseReceipt(bitmap: Bitmap): ReceiptScanResult
    suspend fun getSmartShoppingSuggestions(): List<SmartShoppingSuggestion>
    suspend fun applySmartSuggestionsToShoppingList(suggestions: List<SmartShoppingSuggestion>)
}
