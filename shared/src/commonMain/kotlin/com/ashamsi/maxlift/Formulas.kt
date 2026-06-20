package com.ashamsi.maxlift

import com.ashamsi.maxlift.domain.model.FormulaType
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Utility object for weightlifting formulas and unit conversions.
 */
object Formulas {
    /** Conversion factor from pounds to kilograms. */
    const val LB_TO_KG = 0.45359237
    /** Conversion factor from kilograms to pounds. */
    const val KG_TO_LB = 2.20462262

    /** Converts weight from pounds to kilograms and rounds to 2 decimal places. */
    fun convertLbToKg(lb: Double): Double = roundToTwoDecimals(lb * LB_TO_KG)
    /** Converts weight from kilograms to pounds and rounds to 2 decimal places. */
    fun convertKgToLb(kg: Double): Double = roundToTwoDecimals(kg * KG_TO_LB)

    /**
     * Calculates 1RM using the Brzycki formula.
     * 1RM = weight * (36 / (37 - reps))
     */
    fun calculateBrzycki(weight: Double, reps: Int): Double {
        if (reps >= 37) return weight // Avoid division by zero or negative
        return weight * (36.0 / (37.0 - reps))
    }

    /**
     * Calculates 1RM using the Epley formula.
     * 1RM = weight * (1 + reps / 30)
     */
    fun calculateEpley(weight: Double, reps: Int): Double {
        return weight * (1.0 + reps / 30.0)
    }

    /**
     * Calculates 1RM using the Lander formula.
     * 1RM = (100 * weight) / (101.3 - 2.67123 * reps)
     */
    fun calculateLander(weight: Double, reps: Int): Double {
        return (100.0 * weight) / (101.3 - 2.67123 * reps)
    }

    /**
     * Calculates 1RM using the Lombardi formula.
     * 1RM = weight * reps ^ 0.1
     */
    fun calculateLombardi(weight: Double, reps: Int): Double {
        return weight * reps.toDouble().pow(0.10)
    }

    /**
     * Calculates 1RM using the Mayhew formula.
     * 1RM = (100 * weight) / (52.2 + 41.9 * e^(-0.055 * reps))
     */
    fun calculateMayhew(weight: Double, reps: Int): Double {
        return (100.0 * weight) / (52.2 + 41.9 * exp(-0.055 * reps))
    }

    /**
     * Calculates 1RM using the O'Conner formula.
     * 1RM = weight * (1 + reps / 40)
     */
    fun calculateOConner(weight: Double, reps: Int): Double {
        return weight * (1.0 + reps / 40.0)
    }

    /**
     * Calculates 1RM using the Wathan formula.
     * 1RM = (100 * weight) / (48.80 + 53.8 * e^(-0.075 * reps))
     */
    fun calculateWathan(weight: Double, reps: Int): Double {
        return (100.0 * weight) / (48.80 + 53.8 * exp(-0.075 * reps))
    }

    /**
     * Calculates 1RM for a specific formula type.
     */
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

    /**
     * Calculates the mean 1RM across multiple formula types.
     */
    fun calculateMeanOneRM(types: List<FormulaType>, weight: Double, reps: Int): Double {
        if (types.isEmpty()) return 0.0
        val sum = types.sumOf { type ->
            when (type) {
                FormulaType.Brzycki -> calculateBrzycki(weight, reps)
                FormulaType.Epley -> calculateEpley(weight, reps)
                FormulaType.Lander -> calculateLander(weight, reps)
                FormulaType.Lombardi -> calculateLombardi(weight, reps)
                FormulaType.Mayhew -> calculateMayhew(weight, reps)
                FormulaType.OConner -> calculateOConner(weight, reps)
                FormulaType.Wathan -> calculateWathan(weight, reps)
            }
        }
        return roundToTwoDecimals(sum / types.size)
    }

    /**
     * Rounds a double to two decimal places.
     */
    fun roundToTwoDecimals(value: Double): Double {
        return (value * 100).roundToInt() / 100.0
    }
}
