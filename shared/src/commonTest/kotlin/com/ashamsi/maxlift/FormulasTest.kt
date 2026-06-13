package com.ashamsi.maxlift

import kotlin.test.Test
import kotlin.test.assertEquals

class FormulasTest {

    @Test
    fun testLbToKg() {
        assertEquals(45.36, Formulas.convertLbToKg(100.0))
        assertEquals(0.0, Formulas.convertLbToKg(0.0))
    }

    @Test
    fun testKgToLb() {
        assertEquals(220.46, Formulas.convertKgToLb(100.0))
        assertEquals(0.0, Formulas.convertKgToLb(0.0))
    }

    @Test
    fun testBrzycki() {
        // weight * (36 / (37 - reps))
        // 100 * (36 / (37 - 1)) = 100
        assertEquals(100.0, Formulas.calculateOneRM(FormulaType.Brzycki, 100.0, 1))
        // 100 * (36 / (37 - 10)) = 133.33
        assertEquals(133.33, Formulas.calculateOneRM(FormulaType.Brzycki, 100.0, 10))
    }

    @Test
    fun testEpley() {
        // weight * (1 + reps / 30)
        // 100 * (1 + 10 / 30) = 133.33
        assertEquals(133.33, Formulas.calculateOneRM(FormulaType.Epley, 100.0, 10))
    }

    @Test
    fun testRounding() {
        assertEquals(123.46, Formulas.roundToTwoDecimals(123.4567))
        assertEquals(123.45, Formulas.roundToTwoDecimals(123.4549))
    }
}
