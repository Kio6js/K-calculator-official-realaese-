package com.example.logic

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

sealed class EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult()
    data class Error(val message: String) : EvalResult()
}

class ExpressionEvaluator(private val isDegreeMode: Boolean = true) {

    companion object {
        private const val PHI = 1.618033988749895
    }

    fun evaluate(expression: String): EvalResult {
        if (expression.isBlank()) {
            return EvalResult.Success(0.0, "0")
        }

        return try {
            val sanitized = sanitize(expression)
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) {
                return EvalResult.Success(0.0, "0")
            }
            val parser = Parser(tokens, isDegreeMode)
            val value = parser.parseExpression()
            if (value.isNaN() || value.isInfinite()) {
                EvalResult.Error("Undefined")
            } else {
                EvalResult.Success(value, formatResult(value))
            }
        } catch (e: Exception) {
            EvalResult.Error(e.message ?: "Invalid syntax")
        }
    }

    private fun sanitize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace("π", "PI")
            .replace("φ", "PHI")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace(" ", "")
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val n = expr.length

        while (i < n) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < n && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                    continue
                }
                c in listOf('+', '-', '*', '/', '^', '%', '(', ')', '!') -> {
                    tokens.add(c.toString())
                    i++
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < n && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                else -> i++
            }
        }

        // Implicit multiplication insertion
        val enriched = mutableListOf<String>()
        for (idx in tokens.indices) {
            val current = tokens[idx]
            enriched.add(current)
            if (idx < tokens.size - 1) {
                val next = tokens[idx + 1]
                val currentIsNumberOrConst = isNumber(current) || current == "PI" || current == "PHI" || current == "e" || current == ")" || current == "!"
                val nextIsNumberOrConstOrFunc = isNumber(next) || next == "PI" || next == "PHI" || next == "e" || next == "(" || isFunction(next)

                if (currentIsNumberOrConst && nextIsNumberOrConstOrFunc && current != "(" && next != ")" && next != "!" && next != "^" && next != "+" && next != "-" && next != "*" && next != "/" && next != "%") {
                    enriched.add("*")
                }
            }
        }

        return enriched
    }

    private fun isNumber(s: String): Boolean = s.toDoubleOrNull() != null
    private fun isFunction(s: String): Boolean = s in listOf(
        "sin", "cos", "tan", "asin", "acos", "atan",
        "sinh", "cosh", "tanh",
        "log", "ln", "sqrt", "cbrt", "abs", "exp"
    )

    private fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        val bd = try {
            BigDecimal(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (_: Exception) {
            BigDecimal(value)
        }

        val symbols = DecimalFormatSymbols(Locale.US)
        val df = if (abs(value) >= 1e11 || (abs(value) > 0 && abs(value) < 1e-6)) {
            DecimalFormat("0.######E0", symbols)
        } else {
            DecimalFormat("#,##0.########", symbols)
        }

        return df.format(bd)
    }

    private class Parser(private val tokens: List<String>, private val isDegreeMode: Boolean) {
        private var pos = 0

        private fun peek(): String? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(): String = tokens[pos++]

        fun parseExpression(): Double = parseAddSubtract()

        private fun parseAddSubtract(): Double {
            var result = parseMultiplyDivide()
            while (peek() == "+" || peek() == "-") {
                val op = consume()
                val next = parseMultiplyDivide()
                result = if (op == "+") result + next else result - next
            }
            return result
        }

        private fun parseMultiplyDivide(): Double {
            var result = parsePower()
            while (peek() == "*" || peek() == "/" || peek() == "%") {
                val op = consume()
                val next = parsePower()
                result = when (op) {
                    "*" -> result * next
                    "/" -> {
                        if (next == 0.0) throw ArithmeticException("Cannot divide by 0")
                        result / next
                    }
                    "%" -> result * (next / 100.0)
                    else -> result
                }
            }
            return result
        }

        private fun parsePower(): Double {
            var result = parseUnary()
            while (peek() == "^") {
                consume()
                val exponent = parseUnary()
                result = result.pow(exponent)
            }
            return result
        }

        private fun parseUnary(): Double {
            if (peek() == "+") {
                consume()
                return parseUnary()
            }
            if (peek() == "-") {
                consume()
                return -parseUnary()
            }
            return parseFactorial()
        }

        private fun parseFactorial(): Double {
            var result = parsePrimary()
            while (peek() == "!") {
                consume()
                result = factorial(result)
            }
            return result
        }

        private fun parsePrimary(): Double {
            val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")

            when {
                token == "(" -> {
                    consume()
                    val result = parseExpression()
                    if (peek() == ")") {
                        consume()
                    }
                    return result
                }
                token == "PI" -> {
                    consume()
                    return Math.PI
                }
                token == "PHI" -> {
                    consume()
                    return PHI
                }
                token == "e" -> {
                    consume()
                    return Math.E
                }
                isFunction(token) -> {
                    val func = consume()
                    val arg = if (peek() == "(") {
                        consume()
                        val res = parseExpression()
                        if (peek() == ")") consume()
                        res
                    } else {
                        parsePrimary()
                    }

                    return evaluateFunction(func, arg)
                }
                else -> {
                    val num = token.toDoubleOrNull()
                    if (num != null) {
                        consume()
                        return num
                    }
                    throw IllegalArgumentException("Unexpected token: $token")
                }
            }
        }

        private fun isFunction(s: String): Boolean = s in listOf(
            "sin", "cos", "tan", "asin", "acos", "atan",
            "sinh", "cosh", "tanh",
            "log", "ln", "sqrt", "cbrt", "abs", "exp"
        )

        private fun evaluateFunction(func: String, arg: Double): Double {
            return when (func) {
                "sin" -> {
                    val rad = if (isDegreeMode) Math.toRadians(arg) else arg
                    sin(rad)
                }
                "cos" -> {
                    val rad = if (isDegreeMode) Math.toRadians(arg) else arg
                    if (isDegreeMode && (abs(arg % 180) == 90.0)) 0.0 else cos(rad)
                }
                "tan" -> {
                    val rad = if (isDegreeMode) Math.toRadians(arg) else arg
                    if (isDegreeMode && (abs(arg % 180) == 90.0)) throw ArithmeticException("Undefined (tan 90°)")
                    tan(rad)
                }
                "asin" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: asin [-1, 1]")
                    val rad = asin(arg)
                    if (isDegreeMode) Math.toDegrees(rad) else rad
                }
                "acos" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: acos [-1, 1]")
                    val rad = acos(arg)
                    if (isDegreeMode) Math.toDegrees(rad) else rad
                }
                "atan" -> {
                    val rad = atan(arg)
                    if (isDegreeMode) Math.toDegrees(rad) else rad
                }
                "sinh" -> sinh(arg)
                "cosh" -> cosh(arg)
                "tanh" -> tanh(arg)
                "log" -> {
                    if (arg <= 0.0) throw ArithmeticException("Domain error: log(x) requires x > 0")
                    log10(arg)
                }
                "ln" -> {
                    if (arg <= 0.0) throw ArithmeticException("Domain error: ln(x) requires x > 0")
                    ln(arg)
                }
                "sqrt" -> {
                    if (arg < 0.0) throw ArithmeticException("Domain error: sqrt(x) requires x >= 0")
                    sqrt(arg)
                }
                "cbrt" -> cbrt(arg)
                "abs" -> abs(arg)
                "exp" -> exp(arg)
                else -> arg
            }
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n.roundToInt().toDouble() != n) {
                throw ArithmeticException("Factorial is only defined for non-negative integers")
            }
            val intN = n.roundToInt()
            if (intN > 170) throw ArithmeticException("Overflow: number too large")
            var acc = 1.0
            for (i in 2..intN) {
                acc *= i
            }
            return acc
        }
    }
}
