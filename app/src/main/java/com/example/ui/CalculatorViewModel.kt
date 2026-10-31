package com.example.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.logic.EvalResult
import com.example.logic.ExpressionEvaluator
import com.example.model.ButtonShapeStyle
import com.example.model.CalculationHistory
import com.example.model.ThemeStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculatorUiState(
    val expression: String = "",
    val liveResult: String = "",
    val isError: Boolean = false,
    val isDegreeMode: Boolean = true,
    val isScientificExpanded: Boolean = false,
    val memoryValue: Double = 0.0,
    val selectedTheme: ThemeStyle = ThemeStyle.GOLD_TITANIUM,
    val isDarkMode: Boolean = true,
    val customAccent: Color? = null,
    val shapeStyle: ButtonShapeStyle = ButtonShapeStyle.CHAMFERED_SQUIRCLE,
    val hapticsEnabled: Boolean = true,
    val historyList: List<CalculationHistory> = emptyList(),
    val isHistoryOpen: Boolean = false,
    val isConverterOpen: Boolean = false,
    val isThemeDialogVisible: Boolean = false
) {
    val hasMemory: Boolean get() = memoryValue != 0.0
}

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CalculatorUiState(
            selectedTheme = ThemeStyle.GOLD_TITANIUM,
            historyList = listOf(
                CalculationHistory(
                    expression = "42 × 3.1415",
                    result = "131.943"
                ),
                CalculationHistory(
                    expression = "sin(45) + cos(45)",
                    result = "1.41421356"
                )
            )
        )
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigit(digit: String) {
        _uiState.update { current ->
            val expr = current.expression

            // Prevent multiple decimals in the same number segment
            if (digit == ".") {
                val lastNumSegment = expr.takeLastWhile { it.isDigit() || it == '.' }
                if (lastNumSegment.contains('.')) {
                    return@update current
                }
                val newExpr = if (expr.isEmpty() || !expr.last().isDigit()) "${expr}0." else "$expr."
                val live = evaluateSafely(newExpr, current.isDegreeMode)
                return@update current.copy(
                    expression = newExpr,
                    liveResult = live.first,
                    isError = live.second
                )
            }

            val newExpr = expr + digit
            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { current ->
            val expr = current.expression
            if (expr.isEmpty()) {
                if (op == "-") {
                    return@update current.copy(expression = "-")
                }
                return@update current
            }

            val operators = listOf("+", "-", "×", "÷", "^", "%")
            val lastChar = expr.last().toString()

            val newExpr = if (lastChar in operators && op in operators) {
                expr.dropLast(1) + op
            } else {
                expr + op
            }

            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second
            )
        }
    }

    fun onSmartParenthesis() {
        _uiState.update { current ->
            val expr = current.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val newChar = if (expr.isEmpty()) {
                "("
            } else {
                val lastChar = expr.last()
                if (openCount > closeCount && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e')) {
                    ")"
                } else {
                    "("
                }
            }
            val newExpr = expr + newChar
            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second
            )
        }
    }

    fun onFunction(func: String) {
        _uiState.update { current ->
            val newExpr = current.expression + func
            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second
            )
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                liveResult = "",
                isError = false
            )
        }
    }

    fun onBackspace() {
        _uiState.update { current ->
            val expr = current.expression
            if (expr.isEmpty()) return@update current

            val funcs = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log(", "ln(", "1/(")
            var newExpr = expr
            val matchedFunc = funcs.find { expr.endsWith(it) }
            if (matchedFunc != null) {
                newExpr = expr.removeSuffix(matchedFunc)
            } else {
                newExpr = expr.dropLast(1)
            }

            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second
            )
        }
    }

    fun onPlusMinus() {
        _uiState.update { current ->
            val expr = current.expression
            if (expr.isEmpty()) {
                return@update current.copy(expression = "-")
            }

            val lastOpIndex = expr.lastIndexOfAny(charArrayOf('+', '-', '×', '÷', '(', '^'))
            val newExpr = if (lastOpIndex == -1) {
                if (expr.startsWith("-")) expr.substring(1) else "-$expr"
            } else {
                val prefix = expr.substring(0, lastOpIndex + 1)
                val suffix = expr.substring(lastOpIndex + 1)
                if (suffix.startsWith("-")) {
                    prefix + suffix.substring(1)
                } else {
                    "$prefix-$suffix"
                }
            }

            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second
            )
        }
    }

    fun onEquals() {
        val current = _uiState.value
        if (current.expression.isBlank()) return

        val evaluator = ExpressionEvaluator(current.isDegreeMode)
        when (val result = evaluator.evaluate(current.expression)) {
            is EvalResult.Success -> {
                val newHistory = listOf(
                    CalculationHistory(
                        expression = current.expression,
                        result = result.formatted
                    )
                ) + current.historyList

                _uiState.update {
                    it.copy(
                        expression = result.formatted.replace(",", ""),
                        liveResult = "",
                        isError = false,
                        historyList = newHistory
                    )
                }
            }
            is EvalResult.Error -> {
                _uiState.update {
                    it.copy(
                        liveResult = result.message,
                        isError = true
                    )
                }
            }
        }
    }

    fun onToggleDegreeMode() {
        _uiState.update { current ->
            val newMode = !current.isDegreeMode
            val live = evaluateSafely(current.expression, newMode)
            current.copy(
                isDegreeMode = newMode,
                liveResult = live.first,
                isError = live.second
            )
        }
    }

    fun onToggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun onSelectTheme(theme: ThemeStyle) {
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    fun onToggleDarkMode(isDark: Boolean) {
        _uiState.update { it.copy(isDarkMode = isDark) }
    }

    fun onSelectAccent(accent: Color?) {
        _uiState.update { it.copy(customAccent = accent) }
    }

    fun onSelectShapeStyle(shape: ButtonShapeStyle) {
        _uiState.update { it.copy(shapeStyle = shape) }
    }

    fun onToggleThemeDialog(visible: Boolean) {
        _uiState.update { it.copy(isThemeDialogVisible = visible) }
    }

    fun onToggleHaptics() {
        _uiState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
    }

    fun onToggleHistory(open: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = open) }
    }

    fun onToggleConverter(open: Boolean) {
        _uiState.update { it.copy(isConverterOpen = open) }
    }

    fun onClearHistory() {
        _uiState.update { it.copy(historyList = emptyList()) }
    }

    fun onUseHistoryExpression(expr: String) {
        _uiState.update { current ->
            val live = evaluateSafely(expr, current.isDegreeMode)
            current.copy(
                expression = expr,
                liveResult = live.first,
                isError = live.second,
                isHistoryOpen = false
            )
        }
    }

    fun onUseHistoryResult(res: String) {
        _uiState.update { current ->
            val clean = res.replace(",", "")
            val newExpr = current.expression + clean
            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second,
                isHistoryOpen = false
            )
        }
    }

    fun onPasteConvertedValue(value: String) {
        _uiState.update { current ->
            val clean = value.replace(",", "")
            val newExpr = if (current.expression.isEmpty() || current.expression.last() in listOf('+', '-', '×', '÷', '^', '(')) {
                current.expression + clean
            } else {
                clean
            }
            val live = evaluateSafely(newExpr, current.isDegreeMode)
            current.copy(
                expression = newExpr,
                liveResult = live.first,
                isError = live.second,
                isConverterOpen = false
            )
        }
    }

    fun onMemoryClear() {
        _uiState.update { it.copy(memoryValue = 0.0) }
    }

    fun onMemoryRecall() {
        val mem = _uiState.value.memoryValue
        val memStr = if (mem % 1.0 == 0.0) mem.toLong().toString() else mem.toString()
        onDigit(memStr)
    }

    fun onMemoryAdd() {
        val currentVal = evaluateCurrentValue()
        _uiState.update { it.copy(memoryValue = it.memoryValue + currentVal) }
    }

    fun onMemorySubtract() {
        val currentVal = evaluateCurrentValue()
        _uiState.update { it.copy(memoryValue = it.memoryValue - currentVal) }
    }

    fun onMemoryStore() {
        val currentVal = evaluateCurrentValue()
        _uiState.update { it.copy(memoryValue = currentVal) }
    }

    private fun evaluateCurrentValue(): Double {
        val expr = _uiState.value.expression
        if (expr.isBlank()) return 0.0
        val evaluator = ExpressionEvaluator(_uiState.value.isDegreeMode)
        return when (val res = evaluator.evaluate(expr)) {
            is EvalResult.Success -> res.value
            is EvalResult.Error -> 0.0
        }
    }

    private fun evaluateSafely(expr: String, isDegree: Boolean): Pair<String, Boolean> {
        if (expr.isBlank()) return Pair("", false)
        val evaluator = ExpressionEvaluator(isDegree)
        return when (val res = evaluator.evaluate(expr)) {
            is EvalResult.Success -> Pair(res.formatted, false)
            is EvalResult.Error -> Pair("", false)
        }
    }
}
