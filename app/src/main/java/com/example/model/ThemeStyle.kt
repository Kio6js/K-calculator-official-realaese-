package com.example.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonShapeStyle(val displayName: String) {
    CHAMFERED_SQUIRCLE("Aerospace Squircle"),
    ROUNDED_CAPSULE("Titanium Capsule"),
    CIRCULAR_BEZEL("Circular Dial");

    fun getShape(radius: Dp): Shape = when (this) {
        CHAMFERED_SQUIRCLE -> RoundedCornerShape(radius)
        ROUNDED_CAPSULE -> RoundedCornerShape(50)
        CIRCULAR_BEZEL -> CircleShape
    }
}

data class AccentColorOption(
    val name: String,
    val color: Color,
    val secondaryColor: Color
)

object PredefinedAccents {
    val options = listOf(
        AccentColorOption("24K Pure Gold", Color(0xFFFFD700), Color(0xFFD4AF37)),
        AccentColorOption("Champagne Gold", Color(0xFFF3E5AB), Color(0xFFE5C158)),
        AccentColorOption("Rose Gold", Color(0xFFF4C2C2), Color(0xFFC97A7E)),
        AccentColorOption("Titanium Bronze", Color(0xFFE0A96D), Color(0xFFCD7F32)),
        AccentColorOption("Platinum White", Color(0xFFF8F9FA), Color(0xFFE5E4E2)),
        AccentColorOption("Royal Emerald", Color(0xFF34D399), Color(0xFF059669)),
        AccentColorOption("Sapphire Cyan", Color(0xFF38BDF8), Color(0xFF0284C7)),
        AccentColorOption("Anodized Violet", Color(0xFFE0AAFF), Color(0xFF9D4EDD))
    )
}

enum class ThemeStyle(
    val title: String,
    val subtitle: String,
    val iconName: String
) {
    GOLD_TITANIUM(
        title = "Gold & Titanium",
        subtitle = "Aerospace brushed titanium chassis with 24K gold trim",
        iconName = "Diamond"
    ),
    BLACK_TITANIUM(
        title = "Black Titanium & Rose",
        subtitle = "DLC matte black titanium with refined rose gold",
        iconName = "Shield"
    ),
    LIQUID_GLASS(
        title = "Liquid Glass & Sapphire",
        subtitle = "Translucent frosted sapphire glass with titanium framing",
        iconName = "WaterDrop"
    ),
    RUST_INDUSTRIAL(
        title = "Rust & Raw Titanium",
        subtitle = "Weathered iron, copper patina & burnished titanium",
        iconName = "Build"
    ),
    FLUTTER_GLOW(
        title = "Flutter Bloom",
        subtitle = "Cosmic violet, iridescent petals & spring bounce",
        iconName = "AutoAwesome"
    ),
    EMERALD_GOLD(
        title = "Emerald & Champagne",
        subtitle = "Royal deep emerald with polished champagne gold",
        iconName = "Star"
    );

    // Background gradient colors (Dark & Light modes)
    fun getBackgroundColors(isDarkMode: Boolean): List<Color> {
        return if (isDarkMode) {
            when (this) {
                GOLD_TITANIUM -> listOf(
                    Color(0xFF101216),
                    Color(0xFF181C22),
                    Color(0xFF14171C)
                )
                BLACK_TITANIUM -> listOf(
                    Color(0xFF0A0B0D),
                    Color(0xFF14161A),
                    Color(0xFF0E1013)
                )
                LIQUID_GLASS -> listOf(
                    Color(0xFF030B1E),
                    Color(0xFF0A1E3F),
                    Color(0xFF042940)
                )
                RUST_INDUSTRIAL -> listOf(
                    Color(0xFF140D0B),
                    Color(0xFF241611),
                    Color(0xFF1C130E)
                )
                FLUTTER_GLOW -> listOf(
                    Color(0xFF120324),
                    Color(0xFF26053D),
                    Color(0xFF38084A)
                )
                EMERALD_GOLD -> listOf(
                    Color(0xFF061A14),
                    Color(0xFF0A2E23),
                    Color(0xFF08221A)
                )
            }
        } else {
            // Light Mode: Satin titanium, champagne, polished silver
            when (this) {
                GOLD_TITANIUM -> listOf(
                    Color(0xFFF4F6F9),
                    Color(0xFFE5E9EE),
                    Color(0xFFD8DEE4)
                )
                BLACK_TITANIUM -> listOf(
                    Color(0xFFECEFF1),
                    Color(0xFFCFD8DC),
                    Color(0xFFB0BEC5)
                )
                LIQUID_GLASS -> listOf(
                    Color(0xFFE0F2FE),
                    Color(0xFFBAE6FD),
                    Color(0xFFE0F7FA)
                )
                RUST_INDUSTRIAL -> listOf(
                    Color(0xFFFDF0ED),
                    Color(0xFFF7D6CD),
                    Color(0xFFEED0C8)
                )
                FLUTTER_GLOW -> listOf(
                    Color(0xFFF5E6FF),
                    Color(0xFFEAD4FC),
                    Color(0xFFFBE4F0)
                )
                EMERALD_GOLD -> listOf(
                    Color(0xFFE6F4EA),
                    Color(0xFFCEEAD6),
                    Color(0xFFD4EDDA)
                )
            }
        }
    }

    // Display Card Background
    fun getDisplayBg(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            when (this) {
                GOLD_TITANIUM -> Color(0x661C2027)
                BLACK_TITANIUM -> Color(0x77121418)
                LIQUID_GLASS -> Color(0x3300254D)
                RUST_INDUSTRIAL -> Color(0x6620130E)
                FLUTTER_GLOW -> Color(0x33440B68)
                EMERALD_GOLD -> Color(0x550C3327)
            }
        } else {
            Color(0xBBFFFFFF)
        }
    }

    fun getDisplayBorder(isDarkMode: Boolean, customAccent: Color?): Color {
        val accent = customAccent ?: getDefaultAccent(isDarkMode)
        return if (isDarkMode) {
            accent.copy(alpha = 0.55f)
        } else {
            accent.copy(alpha = 0.45f)
        }
    }

    // Primary Text Color
    fun getTextPrimary(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            when (this) {
                GOLD_TITANIUM -> Color(0xFFFFF8E7)
                BLACK_TITANIUM -> Color(0xFFFDF7F4)
                LIQUID_GLASS -> Color(0xFFF0FDF4)
                RUST_INDUSTRIAL -> Color(0xFFFDF0D5)
                FLUTTER_GLOW -> Color(0xFFFFF0F5)
                EMERALD_GOLD -> Color(0xFFF0FFF4)
            }
        } else {
            Color(0xFF1A1C20)
        }
    }

    // Secondary / Preview Text Color
    fun getTextSecondary(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            when (this) {
                GOLD_TITANIUM -> Color(0xFFD4AF37)
                BLACK_TITANIUM -> Color(0xFFE0A996)
                LIQUID_GLASS -> Color(0xFF7DD3FC)
                RUST_INDUSTRIAL -> Color(0xFFE29578)
                FLUTTER_GLOW -> Color(0xFFE0AAFF)
                EMERALD_GOLD -> Color(0xFFA7F3D0)
            }
        } else {
            Color(0xFF555B66)
        }
    }

    // Default Accent Color
    fun getDefaultAccent(isDarkMode: Boolean): Color {
        return when (this) {
            GOLD_TITANIUM -> if (isDarkMode) Color(0xFFFFD700) else Color(0xFFD4AF37)
            BLACK_TITANIUM -> Color(0xFFE0A996)
            LIQUID_GLASS -> Color(0xFF00F5D4)
            RUST_INDUSTRIAL -> Color(0xFFE76F51)
            FLUTTER_GLOW -> Color(0xFFFF007F)
            EMERALD_GOLD -> if (isDarkMode) Color(0xFF34D399) else Color(0xFF059669)
        }
    }

    // Number Buttons
    fun getNumberBtnBg(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            when (this) {
                GOLD_TITANIUM -> Color(0x35282E38)
                BLACK_TITANIUM -> Color(0x351F232B)
                LIQUID_GLASS -> Color(0x28FFFFFF)
                RUST_INDUSTRIAL -> Color(0x283E2723)
                FLUTTER_GLOW -> Color(0x285A189A)
                EMERALD_GOLD -> Color(0x30134E3A)
            }
        } else {
            Color(0xEEFFFFFF)
        }
    }

    fun getNumberBtnBorder(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color(0x33B89D71)
        } else {
            Color(0x337A684C)
        }
    }

    fun getNumberBtnText(isDarkMode: Boolean): Color {
        return if (isDarkMode) Color.White else Color(0xFF1A1D20)
    }

    // Operator Buttons (+, -, ×, ÷)
    fun getOperatorBtnBg(isDarkMode: Boolean, customAccent: Color?): Color {
        val accent = customAccent ?: getDefaultAccent(isDarkMode)
        return if (isDarkMode) {
            accent.copy(alpha = 0.22f)
        } else {
            accent.copy(alpha = 0.18f)
        }
    }

    fun getOperatorBtnBorder(isDarkMode: Boolean, customAccent: Color?): Color {
        val accent = customAccent ?: getDefaultAccent(isDarkMode)
        return accent.copy(alpha = if (isDarkMode) 0.65f else 0.5f)
    }

    fun getOperatorBtnText(isDarkMode: Boolean, customAccent: Color?): Color {
        return customAccent ?: when (this) {
            GOLD_TITANIUM -> if (isDarkMode) Color(0xFFFFE082) else Color(0xFFB8860B)
            BLACK_TITANIUM -> Color(0xFFE0A996)
            LIQUID_GLASS -> Color(0xFF38BDF8)
            RUST_INDUSTRIAL -> Color(0xFF2EC4B6)
            FLUTTER_GLOW -> Color(0xFFE0AAFF)
            EMERALD_GOLD -> if (isDarkMode) Color(0xFF6EE7B7) else Color(0xFF047857)
        }
    }

    // Equals Button Brush
    fun getEqualsBrush(isDarkMode: Boolean, customAccent: Color?): Brush {
        val accent = customAccent ?: getDefaultAccent(isDarkMode)
        return when (this) {
            GOLD_TITANIUM -> Brush.linearGradient(
                listOf(
                    accent,
                    Color(0xFFF3E5AB),
                    Color(0xFFB8860B)
                )
            )
            BLACK_TITANIUM -> Brush.linearGradient(
                listOf(Color(0xFFE0A996), Color(0xFFC97A7E))
            )
            LIQUID_GLASS -> Brush.linearGradient(
                listOf(Color(0xFF00F5D4), Color(0xFF00BBF9))
            )
            RUST_INDUSTRIAL -> Brush.linearGradient(
                listOf(Color(0xFFE76F51), Color(0xFFF4A261))
            )
            FLUTTER_GLOW -> Brush.linearGradient(
                listOf(Color(0xFFFF007F), Color(0xFF7B2CBF))
            )
            EMERALD_GOLD -> Brush.linearGradient(
                listOf(Color(0xFF34D399), Color(0xFFF3E5AB))
            )
        }
    }

    fun getEqualsText(isDarkMode: Boolean): Color {
        return Color(0xFF1A1406)
    }

    // Function Buttons (AC, (), %, scientific)
    fun getFunctionBtnBg(isDarkMode: Boolean): Color {
        return if (isDarkMode) {
            Color(0x35323A46)
        } else {
            Color(0xDDE2E8F0)
        }
    }

    fun getFunctionBtnText(isDarkMode: Boolean, customAccent: Color?): Color {
        val accent = customAccent ?: getDefaultAccent(isDarkMode)
        return if (isDarkMode) {
            accent
        } else {
            Color(0xFF334155)
        }
    }

    // Particle / Aura effect color
    fun getParticleColor(isDarkMode: Boolean, customAccent: Color?): Color {
        val accent = customAccent ?: getDefaultAccent(isDarkMode)
        return accent.copy(alpha = if (isDarkMode) 0.65f else 0.4f)
    }
}
