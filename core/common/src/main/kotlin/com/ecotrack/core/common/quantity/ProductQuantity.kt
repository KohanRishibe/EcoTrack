package com.ecotrack.core.common.quantity

import kotlin.math.min

enum class QuantityUnitType {
    DISCRETE,
    WEIGHT,
    VOLUME,
}

object ProductQuantity {

    private val weightGramUnits = setOf("г", "гр", "gram", "grams")
    private val weightKgUnits = setOf("кг", "kg")
    private val volumeLiterUnits = setOf("л", "л.")
    private val volumeMlUnits = setOf("мл", "ml")

    fun normalizeUnit(raw: String): String = raw.trim().lowercase()

    fun unitType(unit: String): QuantityUnitType = when (normalizeUnit(unit)) {
        in weightGramUnits, in weightKgUnits -> QuantityUnitType.WEIGHT
        in volumeLiterUnits, in volumeMlUnits -> QuantityUnitType.VOLUME
        else -> QuantityUnitType.DISCRETE
    }

    /** Сколько списывается за одно нажатие «использован» / «выброшен». */
    fun consumeStep(quantity: Double, unit: String): Double {
        val normalized = normalizeUnit(unit)
        return when (unitType(normalized)) {
            QuantityUnitType.DISCRETE -> 1.0
            QuantityUnitType.WEIGHT -> when (normalized) {
                in weightKgUnits -> min(0.1, quantity)
                else -> min(50.0, quantity)
            }
            QuantityUnitType.VOLUME -> when (normalized) {
                in volumeLiterUnits -> min(0.25, quantity)
                else -> min(100.0, quantity)
            }
        }
    }

    fun portionsRemaining(quantity: Double, unit: String): Double {
        val step = consumeStep(quantity, unit)
        if (step <= 0) return 0.0
        return quantity / step
    }

    fun formatQuantity(quantity: Double, unit: String): String {
        val normalized = normalizeUnit(unit)
        val value = formatDecimal(quantity)
        val label = displayUnitLabel(normalized)
        return "$value $label"
    }

    fun consumeStepLabel(quantity: Double, unit: String): String =
        formatQuantity(consumeStep(quantity, unit), unit)

    /** Суммируем только штучные товары для сводки на главной. */
    fun contributesToDiscreteTotal(unit: String): Boolean =
        unitType(unit) == QuantityUnitType.DISCRETE

    fun formatDecimal(value: Double): String = when {
        value % 1.0 == 0.0 -> value.toInt().toString()
        else -> "%.2f".format(value).trimEnd('0').trimEnd('.')
    }

    private fun displayUnitLabel(normalized: String): String = when (normalized) {
        in weightGramUnits -> "г"
        in weightKgUnits -> "кг"
        in volumeLiterUnits -> "л"
        in volumeMlUnits -> "мл"
        "уп" -> "уп"
        "шт" -> "шт"
        else -> normalized
    }

    val commonUnits: List<String> = listOf("шт", "г", "кг", "л", "мл", "уп")
}
