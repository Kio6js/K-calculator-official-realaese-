package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeStyle
import com.example.util.HapticHelper
import com.example.util.HapticType

enum class ButtonType {
    NUMBER,
    OPERATOR,
    EQUALS,
    FUNCTION,
    ACCENT
}

@Composable
fun ShinyCircularButton(
    text: String? = null,
    icon: ImageVector? = null,
    type: ButtonType = ButtonType.NUMBER,
    themeStyle: ThemeStyle,
    isDarkMode: Boolean = true,
    customAccent: Color? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp,
    testTag: String = "btn_${text ?: "icon"}",
    hapticsEnabled: Boolean = true
) {
    val context = LocalContext.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // High performance spring physics
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = 500f
        ),
        label = "ButtonScale"
    )

    val shape = CircleShape
    val accent = customAccent ?: themeStyle.getDefaultAccent(isDarkMode)

    val (bgBrush, borderStroke, textColor) = when (type) {
        ButtonType.NUMBER -> {
            if (isDarkMode) {
                Triple(
                    Brush.verticalGradient(
                        listOf(
                            if (isPressed) Color(0xFF4C525D) else Color(0xFF383C44),
                            Color(0xFF282B32),
                            Color(0xFF202328)
                        )
                    ),
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color(0xFF4A505C),
                                Color.Black.copy(alpha = 0.4f)
                            )
                        )
                    ),
                    Color(0xFFF4F6F9)
                )
            } else {
                Triple(
                    Brush.verticalGradient(
                        listOf(
                            if (isPressed) Color(0xFFECEFF4) else Color(0xFFE2E6EC),
                            Color(0xFFCFD5DE),
                            Color(0xFFC3C9D2)
                        )
                    ),
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.9f),
                                Color(0xFFB0B8C5),
                                Color(0xFF9AA3B2)
                            )
                        )
                    ),
                    Color(0xFF14171A)
                )
            }
        }
        ButtonType.OPERATOR -> {
            if (isDarkMode) {
                Triple(
                    Brush.verticalGradient(
                        listOf(
                            if (isPressed) Color(0xFF343A45) else Color(0xFF262A32),
                            Color(0xFF1B1E24),
                            Color(0xFF13151A)
                        )
                    ),
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                accent.copy(alpha = 0.75f),
                                Color(0xFF333842),
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    ),
                    accent
                )
            } else {
                Triple(
                    Brush.verticalGradient(
                        listOf(
                            if (isPressed) Color(0xFF555C66) else Color(0xFF454B54),
                            Color(0xFF353940),
                            Color(0xFF2B2E34)
                        )
                    ),
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color(0xFF555B66),
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    ),
                    Color.White
                )
            }
        }
        ButtonType.EQUALS -> {
            Triple(
                Brush.verticalGradient(
                    listOf(
                        if (isPressed) Color(0xFFFFF2A8) else Color(0xFFFFDF70),
                        accent,
                        Color(0xFFB8860B)
                    )
                ),
                BorderStroke(
                    1.6.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color(0xFFFFD700),
                            Color(0xFF8B6508)
                        )
                    )
                ),
                Color(0xFF1A1202)
            )
        }
        ButtonType.FUNCTION -> {
            if (isDarkMode) {
                Triple(
                    Brush.verticalGradient(
                        listOf(
                            if (isPressed) Color(0xFF3E434E) else Color(0xFF30343D),
                            Color(0xFF23262D),
                            Color(0xFF1A1C22)
                        )
                    ),
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color(0xFF444A56),
                                Color.Black.copy(alpha = 0.4f)
                            )
                        )
                    ),
                    Color(0xFFE5E9F0)
                )
            } else {
                Triple(
                    Brush.verticalGradient(
                        listOf(
                            if (isPressed) Color(0xFF5A616D) else Color(0xFF484E58),
                            Color(0xFF383C44),
                            Color(0xFF2C3036)
                        )
                    ),
                    BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color(0xFF555B66),
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    ),
                    Color.White
                )
            }
        }
        ButtonType.ACCENT -> {
            Triple(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = if (isPressed) 0.5f else 0.35f),
                        accent.copy(alpha = 0.18f)
                    )
                ),
                BorderStroke(1.2.dp, accent),
                accent
            )
        }
    }

    val hapticType = when {
        text == "AC" -> HapticType.CLEAR_ALL
        type == ButtonType.EQUALS -> HapticType.CONFIRM_EQUALS
        type == ButtonType.OPERATOR -> HapticType.OPERATOR_TAP
        else -> HapticType.KEY_TAP
    }

    Box(
        modifier = modifier
            .scale(scale)
            .aspectRatio(1f)
            .defaultMinSize(minWidth = 54.dp, minHeight = 54.dp)
            .shadow(
                elevation = if (type == ButtonType.EQUALS) (if (isPressed) 4.dp else 12.dp) else (if (isPressed) 1.dp else 5.dp),
                shape = shape,
                ambientColor = if (type == ButtonType.EQUALS) accent else Color.Black,
                spotColor = if (type == ButtonType.EQUALS) accent else Color.Black
            )
            .clip(shape)
            .background(bgBrush)
            .border(borderStroke, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                HapticHelper.perform(context, view, hapticType, hapticsEnabled)
                onClick()
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Top Specular Highlight Arc (shiny reflection dome)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.42f)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (isPressed) 0.55f else (if (isDarkMode) 0.25f else 0.5f)),
                            Color.Transparent
                        )
                    )
                )
        )

        if (text != null) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = if (type == ButtonType.EQUALS || type == ButtonType.OPERATOR) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = testTag,
                tint = textColor,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
