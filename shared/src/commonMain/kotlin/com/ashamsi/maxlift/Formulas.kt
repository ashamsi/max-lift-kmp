package com.ashamsi.maxlift

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt

enum class FormulaType(val id: String) {
    Brzycki("brzycki"),
    Epley("epley"),
    Lander("lander"),
    Lombardi("lombardi"),
    Mayhew("mayhew"),
    OConner("oconner"),
    Wathan("wathan"),
}

object Formulas {
    const val LB_TO_KG = 0.45359237
    const val KG_TO_LB = 2.20462262

    fun convertLbToKg(lb: Double): Double = roundToTwoDecimals(lb * LB_TO_KG)
    fun convertKgToLb(kg: Double): Double = roundToTwoDecimals(kg * KG_TO_LB)

    fun calculateBrzycki(weight: Double, reps: Int): Double {
        if (reps >= 37) return weight // Avoid division by zero or negative
        return weight * (36.0 / (37.0 - reps))
    }

    fun calculateEpley(weight: Double, reps: Int): Double {
        return weight * (1.0 + reps / 30.0)
    }

    fun calculateLander(weight: Double, reps: Int): Double {
        return (100.0 * weight) / (101.3 - 2.67123 * reps)
    }

    fun calculateLombardi(weight: Double, reps: Int): Double {
        return weight * reps.toDouble().pow(0.10)
    }

    fun calculateMayhew(weight: Double, reps: Int): Double {
        return (100.0 * weight) / (52.2 + 41.9 * exp(-0.055 * reps))
    }

    fun calculateOConner(weight: Double, reps: Int): Double {
        return weight * (1.0 + reps / 40.0)
    }

    fun calculateWathan(weight: Double, reps: Int): Double {
        return (100.0 * weight) / (48.80 + 53.8 * exp(-0.075 * reps))
    }

    fun calculateOneRM(type: FormulaType, weight: Double, reps: Int): Double {
        val result = when (type) {
            FormulaType.Brzycki -> calculateBrzycki(weight, reps)
            FormulaType.Epley -> calculateEpley(weight, reps)
            FormulaType.Lander -> calculateLander(weight, reps)
            FormulaType.Lombardi -> calculateLombardi(weight, reps)
            FormulaType.Mayhew -> calculateMayhew(weight, reps)
            FormulaType.OConner -> calculateOConner(weight, reps)
            FormulaType.Wathan -> calculateWathan(weight, reps)
        }
        return roundToTwoDecimals(result)
    }

    fun roundToTwoDecimals(value: Double): Double {
        return (value * 100).roundToInt() / 100.0
    }
}
