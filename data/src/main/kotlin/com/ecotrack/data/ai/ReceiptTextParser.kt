package com.ecotrack.data.ai

import com.ecotrack.domain.model.ai.ReceiptLineItem

object ReceiptTextParser {

    private val priceLineRegex = Regex(
        """^(.{2,60}?)\s+(\d+[.,]\d{2})\s*$""",
        RegexOption.IGNORE_CASE,
    )
    private val qtyLineRegex = Regex(
        """^(\d+[.,]?\d*)\s*(шт|кг|г|л|мл|уп)\s+(.+)$""",
        RegexOption.IGNORE_CASE,
    )
    private val skipWords = setOf(
        "итого", "total", "сумма", "сдача", "наличные", "карта", "чек", "фн", "инн",
    )

    fun parse(rawText: String): List<ReceiptLineItem> {
        return rawText.lines()
            .map { it.trim() }
            .filter { it.length >= 3 }
            .filterNot { line -> skipWords.any { line.contains(it, ignoreCase = true) } }
            .mapNotNull { line -> parseLine(line) }
            .distinctBy { it.name.lowercase() }
            .take(30)
    }

    private fun parseLine(line: String): ReceiptLineItem? {
        priceLineRegex.find(line)?.let { match ->
            val name = match.groupValues[1].trim()
            val price = match.groupValues[2].replace(',', '.').toDoubleOrNull()
            if (name.length >= 2) return ReceiptLineItem(name = name, price = price)
        }
        qtyLineRegex.find(line)?.let { match ->
            val qty = match.groupValues[1].replace(',', '.').toDoubleOrNull()
            val unit = match.groupValues[2]
            val name = match.groupValues[3].trim()
            if (name.length >= 2) return ReceiptLineItem(name = name, quantity = qty, unit = unit)
        }
        if (line.length in 3..50 && line.any { it.isLetter() }) {
            return ReceiptLineItem(name = line)
        }
        return null
    }
}
