package com.example

import com.example.logic.EvalResult
import com.example.logic.ExpressionEvaluator
import com.example.model.UnitCategory
import com.example.model.UnitConverterData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBasicArithmetic() {
        val evaluator = ExpressionEvaluator(isDegreeMode = true)
        val res = evaluator.evaluate("12 + 34 × 2 - 10 ÷ 2")
        assertTrue(res is EvalResult.Success)
        assertEquals(75.0, (res as EvalResult.Success).value, 0.0001)
    }

    @Test
    fun testParenthesesAndPrecedence() {
        val evaluator = ExpressionEvaluator(isDegreeMode = true)
        val res = evaluator.evaluate("(5 + 3) × (10 - 2)")
        assertTrue(res is EvalResult.Success)
        assertEquals(64.0, (res as EvalResult.Success).value, 0.0001)
    }

    @Test
    fun testPowersAndFactorial() {
        val evaluator = ExpressionEvaluator(isDegreeMode = true)
        val resPow = evaluator.evaluate("2^8")
        assertTrue(resPow is EvalResult.Success)
        assertEquals(256.0, (resPow as EvalResult.Success).value, 0.0001)

        val resFact = evaluator.evaluate("5!")
        assertTrue(resFact is EvalResult.Success)
        assertEquals(120.0, (resFact as EvalResult.Success).value, 0.0001)
    }

    @Test
    fun testTrigDegVsRad() {
        val evalDeg = ExpressionEvaluator(isDegreeMode = true)
        val resDeg = evalDeg.evaluate("sin(90)")
        assertTrue(resDeg is EvalResult.Success)
        assertEquals(1.0, (resDeg as EvalResult.Success).value, 0.0001)

        val evalRad = ExpressionEvaluator(isDegreeMode = false)
        val resRad = evalRad.evaluate("cos(0)")
        assertTrue(resRad is EvalResult.Success)
        assertEquals(1.0, (resRad as EvalResult.Success).value, 0.0001)
    }

    @Test
    fun testUnitConverter() {
        // 1 km to m
        val meters = UnitConverterData.convert(1.0, "km", "m", UnitCategory.LENGTH)
        assertEquals(1000.0, meters, 0.0001)

        // 100 °C to °F = 212 °F
        val fahrenheit = UnitConverterData.convert(100.0, "°C", "°F", UnitCategory.TEMPERATURE)
        assertEquals(212.0, fahrenheit, 0.0001)
    }

    @Test
    fun testScientistModeFunctions() {
        val evaluator = ExpressionEvaluator(isDegreeMode = true)

        // Cube root
        val resCbrt = evaluator.evaluate("∛(27)")
        assertTrue(resCbrt is EvalResult.Success)
        assertEquals(3.0, (resCbrt as EvalResult.Success).value, 0.0001)

        // Squared
        val resSquare = evaluator.evaluate("6²")
        assertTrue(resSquare is EvalResult.Success)
        assertEquals(36.0, (resSquare as EvalResult.Success).value, 0.0001)

        // Hyperbolic
        val resSinh = evaluator.evaluate("sinh(0)")
        assertTrue(resSinh is EvalResult.Success)
        assertEquals(0.0, (resSinh as EvalResult.Success).value, 0.0001)

        // Golden Ratio
        val resPhi = evaluator.evaluate("φ")
        assertTrue(resPhi is EvalResult.Success)
        assertEquals(1.6180339, (resPhi as EvalResult.Success).value, 0.0001)
    }
}

