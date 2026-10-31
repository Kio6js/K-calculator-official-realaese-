package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ButtonShapeStyle
import com.example.model.ThemeStyle
import com.example.util.HapticHelper
import com.example.util.HapticType

enum class SciTab(val title: String) {
    TRIG("Trig & Hyp"),
    ALGEBRA("Algebra & Power"),
    CONSTANTS("Logs & Const")
}

@Composable
fun KeypadLayout(
    themeStyle: ThemeStyle,
    isDarkMode: Boolean = true,
    customAccent: Color? = null,
    shapeStyle: ButtonShapeStyle = ButtonShapeStyle.CIRCULAR_BEZEL,
    isScientificExpanded: Boolean,
    isDegreeMode: Boolean,
    hapticsEnabled: Boolean,
    onToggleScientific: () -> Unit,
    onToggleDegreeMode: () -> Unit,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEquals: () -> Unit,
    onSmartParenthesis: () -> Unit,
    onFunction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    var selectedSciTab by remember { mutableStateOf(SciTab.TRIG) }
    var isInverseMode by remember { mutableStateOf(false) }
    val accent = customAccent ?: themeStyle.getDefaultAccent(isDarkMode)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Advanced Scientist Mode Tray (Animated with Spring)
        AnimatedVisibility(
            visible = isScientificExpanded,
            enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.8f)) + fadeIn(),
            exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.8f)) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scientist Category Sub-Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SciTab.values().forEach { tab ->
                            val isSelected = tab == selectedSciTab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) accent.copy(alpha = if (isDarkMode) 0.25f else 0.18f)
                                        else (if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f))
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accent else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        HapticHelper.perform(context, view, HapticType.TOGGLE_MODE, hapticsEnabled)
                                        selectedSciTab = tab
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) accent else (if (isDarkMode) Color.White.copy(alpha = 0.7f) else Color(0xFF555B66))
                                )
                            }
                        }
                    }

                    // DEG / RAD Quick Toggle in Sci Bar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent.copy(alpha = 0.2f))
                            .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable {
                                HapticHelper.perform(context, view, HapticType.TOGGLE_MODE, hapticsEnabled)
                                onToggleDegreeMode()
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("btn_toggle_deg_rad")
                    ) {
                        Text(
                            text = if (isDegreeMode) "DEG" else "RAD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accent
                        )
                    }
                }

                // Scientist Keys Content based on Tab
                when (selectedSciTab) {
                    SciTab.TRIG -> {
                        // Trig Row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val sinText = if (isInverseMode) "asin" else "sin"
                            val cosText = if (isInverseMode) "acos" else "cos"
                            val tanText = if (isInverseMode) "atan" else "tan"

                            SciButton(sinText, themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("$sinText(") }
                            SciButton(cosText, themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("$cosText(") }
                            SciButton(tanText, themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("$tanText(") }
                            SciButton(
                                text = if (isInverseMode) "INV*" else "INV",
                                themeStyle = themeStyle,
                                isDarkMode = isDarkMode,
                                customAccent = customAccent,
                                hapticsEnabled = hapticsEnabled,
                                isHighlighted = isInverseMode
                            ) { isInverseMode = !isInverseMode }
                        }
                        // Trig Row 2: Hyperbolics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SciButton("sinh", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("sinh(") }
                            SciButton("cosh", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("cosh(") }
                            SciButton("tanh", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("tanh(") }
                            SciButton("|x|", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("abs(") }
                        }
                    }
                    SciTab.ALGEBRA -> {
                        // Algebra Row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SciButton("√", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("√(") }
                            SciButton("∛", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("∛(") }
                            SciButton("x²", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onOperator("²") }
                            SciButton("xʸ", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onOperator("^") }
                        }
                        // Algebra Row 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SciButton("1/x", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("1/(") }
                            SciButton("x!", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onOperator("!") }
                            SciButton("(", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onDigit("(") }
                            SciButton(")", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onDigit(")") }
                        }
                    }
                    SciTab.CONSTANTS -> {
                        // Logs Row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SciButton("ln", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("ln(") }
                            SciButton("log", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("log(") }
                            SciButton("eˣ", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("exp(") }
                            SciButton("10ˣ", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("10^(") }
                        }
                        // Constants Row 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SciButton("π", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onDigit("π") }
                            SciButton("e", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onDigit("e") }
                            SciButton("φ (phi)", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onDigit("φ") }
                            SciButton("±", themeStyle, isDarkMode, customAccent, hapticsEnabled) { onFunction("-") }
                        }
                    }
                }
            }
        }

        // 4x5 Circular Grid (matching reference screenshot)
        // Row 1: AC, ( ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShinyCircularButton(
                text = "AC",
                type = ButtonType.FUNCTION,
                themeStyle = themeStyle,
                isDarkMode = isDarkMode,
                customAccent = customAccent,
                onClick = onClear,
                hapticsEnabled = hapticsEnabled,
                fontSize = 24.sp,
                testTag = "btn_ac",
                modifier = Modifier.weight(1f)
            )
            ShinyCircularButton(
                text = "( )",
                type = ButtonType.FUNCTION,
                themeStyle = themeStyle,
                isDarkMode = isDarkMode,
                customAccent = customAccent,
                onClick = onSmartParenthesis,
                hapticsEnabled = hapticsEnabled,
                fontSize = 22.sp,
                testTag = "btn_parentheses",
                modifier = Modifier.weight(1f)
            )
            ShinyCircularButton(
                text = "%",
                type = ButtonType.FUNCTION,
                themeStyle = themeStyle,
                isDarkMode = isDarkMode,
                customAccent = customAccent,
                onClick = { onOperator("%") },
                hapticsEnabled = hapticsEnabled,
                fontSize = 26.sp,
                testTag = "btn_percent",
                modifier = Modifier.weight(1f)
            )
            ShinyCircularButton(
                text = "÷",
                type = ButtonType.OPERATOR,
                themeStyle = themeStyle,
                isDarkMode = isDarkMode,
                customAccent = customAccent,
                onClick = { onOperator("÷") },
                hapticsEnabled = hapticsEnabled,
                fontSize = 32.sp,
                testTag = "btn_divide",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShinyCircularButton(text = "7", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("7") }, hapticsEnabled = hapticsEnabled, testTag = "btn_7", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "8", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("8") }, hapticsEnabled = hapticsEnabled, testTag = "btn_8", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "9", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("9") }, hapticsEnabled = hapticsEnabled, testTag = "btn_9", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "×", type = ButtonType.OPERATOR, themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onOperator("×") }, hapticsEnabled = hapticsEnabled, fontSize = 32.sp, testTag = "btn_multiply", modifier = Modifier.weight(1f))
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShinyCircularButton(text = "4", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("4") }, hapticsEnabled = hapticsEnabled, testTag = "btn_4", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "5", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("5") }, hapticsEnabled = hapticsEnabled, testTag = "btn_5", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "6", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("6") }, hapticsEnabled = hapticsEnabled, testTag = "btn_6", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "−", type = ButtonType.OPERATOR, themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onOperator("-") }, hapticsEnabled = hapticsEnabled, fontSize = 32.sp, testTag = "btn_subtract", modifier = Modifier.weight(1f))
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShinyCircularButton(text = "1", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("1") }, hapticsEnabled = hapticsEnabled, testTag = "btn_1", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "2", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("2") }, hapticsEnabled = hapticsEnabled, testTag = "btn_2", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "3", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("3") }, hapticsEnabled = hapticsEnabled, testTag = "btn_3", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = "+", type = ButtonType.OPERATOR, themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onOperator("+") }, hapticsEnabled = hapticsEnabled, fontSize = 32.sp, testTag = "btn_add", modifier = Modifier.weight(1f))
        }

        // Row 5: 0, ., ⌫, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ShinyCircularButton(text = "0", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit("0") }, hapticsEnabled = hapticsEnabled, testTag = "btn_0", modifier = Modifier.weight(1f))
            ShinyCircularButton(text = ".", themeStyle = themeStyle, isDarkMode = isDarkMode, customAccent = customAccent, onClick = { onDigit(".") }, hapticsEnabled = hapticsEnabled, fontSize = 32.sp, testTag = "btn_dot", modifier = Modifier.weight(1f))
            ShinyCircularButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                themeStyle = themeStyle,
                isDarkMode = isDarkMode,
                customAccent = customAccent,
                onClick = onBackspace,
                hapticsEnabled = hapticsEnabled,
                testTag = "btn_backspace",
                modifier = Modifier.weight(1f)
            )
            ShinyCircularButton(
                text = "=",
                type = ButtonType.EQUALS,
                themeStyle = themeStyle,
                isDarkMode = isDarkMode,
                customAccent = customAccent,
                onClick = onEquals,
                hapticsEnabled = hapticsEnabled,
                fontSize = 34.sp,
                testTag = "btn_equals",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RowScope.SciButton(
    text: String,
    themeStyle: ThemeStyle,
    isDarkMode: Boolean,
    customAccent: Color?,
    hapticsEnabled: Boolean,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    ShinyCircularButton(
        text = text,
        type = if (isHighlighted) ButtonType.ACCENT else ButtonType.FUNCTION,
        themeStyle = themeStyle,
        isDarkMode = isDarkMode,
        customAccent = customAccent,
        onClick = onClick,
        hapticsEnabled = hapticsEnabled,
        fontSize = 14.sp,
        testTag = "btn_sci_$text",
        modifier = Modifier.weight(1f)
    )
}
