package com.ecotrack.domain.usecase.ai

import com.ecotrack.domain.model.ai.SmartShoppingSuggestion
import com.ecotrack.domain.repository.AiRepository
import javax.inject.Inject

class ApplySmartSuggestionsUseCase @Inject constructor(
    private val aiRepository: AiRepository,
) {
    suspend operator fun invoke(suggestions: List<SmartShoppingSuggestion>) {
        aiRepository.applySmartSuggestionsToShoppingList(suggestions)
    }
}
