package com.ecotrack.data.repository

import android.graphics.Bitmap
import com.ecotrack.core.ml.ImageLabelingClient
import com.ecotrack.core.ml.TextRecognitionClient
import com.ecotrack.core.ml.gemini.GeminiNanoClassifier
import com.ecotrack.data.ai.CategoryLabelMapper
import com.ecotrack.data.ai.ReceiptTextParser
import com.ecotrack.data.ai.SmartSuggestionEngine
import com.ecotrack.domain.model.ProductCategory
import com.ecotrack.domain.model.ai.AiSource
import com.ecotrack.domain.model.ai.ProductPhotoInsight
import com.ecotrack.domain.model.ai.ReceiptScanResult
import com.ecotrack.domain.model.ai.SmartShoppingSuggestion
import com.ecotrack.domain.repository.AiRepository
import com.ecotrack.domain.repository.ProductCatalogRepository
import com.ecotrack.domain.repository.ProductRepository
import com.ecotrack.domain.repository.SettingsRepository
import com.ecotrack.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val productRepository: ProductRepository,
    private val shoppingRepository: ShoppingRepository,
    private val settingsRepository: SettingsRepository,
    private val imageLabelingClient: ImageLabelingClient,
    private val textRecognitionClient: TextRecognitionClient,
    private val geminiNanoClassifier: GeminiNanoClassifier,
    private val productCatalog: ProductCatalogRepository,
) : AiRepository {

    private fun buildPhotoInsight(
        name: String,
        category: ProductCategory,
        shelfDays: Int,
        confidence: Float,
        labels: List<String>,
        source: AiSource,
    ): ProductPhotoInsight {
        val catalog = productCatalog.suggestFromName(name)
        return ProductPhotoInsight(
            suggestedName = name,
            category = catalog.category,
            suggestedQuantity = catalog.quantity,
            suggestedUnit = catalog.unit,
            suggestedShelfLifeDays = shelfDays,
            suggestedExpiryDate = LocalDate.now().plusDays(shelfDays.toLong()),
            confidence = confidence,
            detectedLabels = labels,
            source = source,
        )
    }

    override suspend fun recognizeProductFromPhoto(bitmap: Bitmap): ProductPhotoInsight? {
        val settings = settingsRepository.observeSettings().first()
        if (!settings.aiPhotoRecognitionEnabled) return null

        geminiNanoClassifier.suggestProductFromPhoto(bitmap)?.let { gemini ->
            val category = ProductCategory.fromRaw(gemini.categoryHint)
            return buildPhotoInsight(
                name = gemini.productName,
                category = category,
                shelfDays = gemini.shelfLifeDays,
                confidence = 0.9f,
                labels = listOf("Gemini Nano"),
                source = AiSource.GEMINI_NANO,
            )
        }

        val labels = imageLabelingClient.analyze(bitmap)
        if (labels.isEmpty()) return null

        val labelTexts = labels.map { it.text }
        val (category, shelfDays) = CategoryLabelMapper.mapLabels(labelTexts)
        val topConfidence = labels.maxOf { it.confidence }

        return buildPhotoInsight(
            name = CategoryLabelMapper.suggestName(labelTexts),
            category = category,
            shelfDays = shelfDays,
            confidence = topConfidence,
            labels = labelTexts.take(5),
            source = AiSource.ML_KIT,
        )
    }

    override suspend fun parseReceipt(bitmap: Bitmap): ReceiptScanResult {
        val settings = settingsRepository.observeSettings().first()
        if (!settings.aiReceiptScanEnabled) {
            return ReceiptScanResult(emptyList(), "")
        }
        val rawText = textRecognitionClient.recognize(bitmap)
        val items = ReceiptTextParser.parse(rawText)
        return ReceiptScanResult(items = items, rawText = rawText)
    }

    override suspend fun getSmartShoppingSuggestions(): List<SmartShoppingSuggestion> {
        val settings = settingsRepository.observeSettings().first()
        if (!settings.aiSmartSuggestionsEnabled) return emptyList()

        val products = productRepository.observeProducts().first()
        return SmartSuggestionEngine.generate(products)
    }

    override suspend fun applySmartSuggestionsToShoppingList(suggestions: List<SmartShoppingSuggestion>) {
        suggestions.forEach { suggestion ->
            shoppingRepository.addItem("${suggestion.productName} (AI)")
        }
    }
}
