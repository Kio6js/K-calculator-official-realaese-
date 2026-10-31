package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeStyle
import kotlinx.coroutines.delay

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CalculatorDisplay(
    expression: String,
    liveResult: String,
    isError: Boolean,
    isDegreeMode: Boolean,
    hasMemory: Boolean,
    themeStyle: ThemeStyle,
    isDarkMode: Boolean = true,
    customAccent: Color? = null,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var showCopiedToast by remember { mutableStateOf(false) }
    val exprScrollState = rememberScrollState()

    val accent = customAccent ?: themeStyle.getDefaultAccent(isDarkMode)

    // Auto-scroll expression to end when it changes
    LaunchedEffect(expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    LaunchedEffect(showCopiedToast) {
        if (showCopiedToast) {
            delay(1500)
            showCopiedToast = false
        }
    }

    var dragDistance by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { dragDistance = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragDistance += dragAmount
                        if (kotlin.math.abs(dragDistance) > 50f) {
                            onBackspace()
                            dragDistance = 0f
                        }
                    }
                )
            }
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    val toCopy = if (liveResult.isNotEmpty() && !isError) liveResult else expression
                    if (toCopy.isNotEmpty()) {
                        clipboardManager.setText(AnnotatedString(toCopy))
                        showCopiedToast = true
                    }
                }
            )
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .testTag("calculator_display")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Optional badges row if DEG/RAD or Memory is active
            if (hasMemory || !isDegreeMode) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    if (!isDegreeMode) {
                        Surface(
                            color = accent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "RAD",
                                color = accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (hasMemory) {
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "M",
                                color = Color(0xFFF59E0B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Expression text line (scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScrollState),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = if (expression.length > 16) 28.sp else 36.sp,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.65f) else Color(0xFF555B66),
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("expression_text")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main evaluated result line
            val resultFontSize = when {
                liveResult.length > 14 -> 42.sp
                liveResult.length > 9 -> 52.sp
                else -> 64.sp
            }

            Text(
                text = if (isError) liveResult else (if (liveResult.isNotEmpty()) liveResult else ""),
                fontSize = resultFontSize,
                fontWeight = FontWeight.Light,
                color = if (isError) Color(0xFFFF5252) else (if (isDarkMode) Color.White else Color(0xFF14171A)),
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_text")
            )
        }

        // Copied tooltip overlay
        AnimatedVisibility(
            visible = showCopiedToast,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                color = if (isDarkMode) Color.Black.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, accent)
            ) {
                Text(
                    text = "Copied to clipboard!",
                    color = if (isDarkMode) Color.White else Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
