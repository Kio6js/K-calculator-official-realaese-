package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.model.ThemeStyle
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiquidGlassBackground(
    themeStyle: ThemeStyle,
    isDarkMode: Boolean = true,
    customAccent: Color? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundAtmosphere")

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animProgress"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val reusedPath = remember { Path() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Draw base gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = themeStyle.getBackgroundColors(isDarkMode),
                startY = 0f,
                endY = height
            )
        )

        // 2. Brushed titanium texture lines
        if (themeStyle == ThemeStyle.GOLD_TITANIUM || themeStyle == ThemeStyle.BLACK_TITANIUM) {
            drawTitaniumBrushedTexture(width, height, isDarkMode)
        }

        // 3. Theme-specific atmospheric canvas layers
        when (themeStyle) {
            ThemeStyle.GOLD_TITANIUM -> drawGoldTitaniumAtmosphere(width, height, animProgress, pulseScale, isDarkMode, customAccent)
            ThemeStyle.BLACK_TITANIUM -> drawBlackTitaniumAtmosphere(width, height, animProgress, isDarkMode, customAccent)
            ThemeStyle.LIQUID_GLASS -> drawLiquidGlassAtmosphere(width, height, animProgress, pulseScale, isDarkMode, customAccent, reusedPath)
            ThemeStyle.RUST_INDUSTRIAL -> drawRustAtmosphere(width, height, animProgress)
            ThemeStyle.FLUTTER_GLOW -> drawFlutterAtmosphere(width, height, animProgress, pulseScale, isDarkMode, customAccent, reusedPath)
            ThemeStyle.EMERALD_GOLD -> drawEmeraldAtmosphere(width, height, animProgress, pulseScale, isDarkMode, customAccent)
        }
    }
}

private fun DrawScope.drawTitaniumBrushedTexture(width: Float, height: Float, isDarkMode: Boolean) {
    val lineColor = if (isDarkMode) Color.White.copy(alpha = 0.02f) else Color.Black.copy(alpha = 0.015f)
    var y = 0f
    while (y < height) {
        drawLine(
            color = lineColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += 8f
    }
}

private fun DrawScope.drawGoldTitaniumAtmosphere(
    width: Float,
    height: Float,
    progress: Float,
    pulse: Float,
    isDarkMode: Boolean,
    customAccent: Color?
) {
    val time = progress * 6.2831855f
    val goldColor = customAccent ?: (if (isDarkMode) Color(0xFFFFD700) else Color(0xFFD4AF37))

    // 24K Gold Specular Core Highlight
    val goldX = width * (0.35f + 0.12f * sin(time))
    val goldY = height * (0.24f + 0.10f * cos(time))
    val goldRadius = width * 0.48f * pulse

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                goldColor.copy(alpha = if (isDarkMode) 0.18f else 0.12f),
                goldColor.copy(alpha = if (isDarkMode) 0.06f else 0.03f),
                Color.Transparent
            ),
            center = Offset(goldX, goldY),
            radius = goldRadius
        ),
        radius = goldRadius,
        center = Offset(goldX, goldY)
    )

    // Secondary Titanium Sheen Bottom Right
    val sheenX = width * (0.75f + 0.1f * cos(time * 0.8f))
    val sheenY = height * (0.74f + 0.08f * sin(time * 0.8f))
    val sheenRadius = width * 0.5f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                if (isDarkMode) Color(0x1A8B9CB2) else Color(0x12B8C5D6),
                Color.Transparent
            ),
            center = Offset(sheenX, sheenY),
            radius = sheenRadius
        ),
        radius = sheenRadius,
        center = Offset(sheenX, sheenY)
    )

    // Floating gold motes
    val moteCount = 10
    for (i in 0 until moteCount) {
        val moteProgress = (progress + (i.toFloat() / moteCount)) % 1f
        val mx = width * (0.15f + 0.7f * ((i * 0.33f) % 1f)) + sin(time + i) * 16f
        val my = height * (1f - moteProgress)
        val alpha = (sin(moteProgress * 3.14159f) * 0.55f).coerceIn(0f, 1f)

        drawCircle(
            color = goldColor.copy(alpha = alpha),
            radius = 2.2f + (i % 3) * 1f,
            center = Offset(mx, my)
        )
    }
}

private fun DrawScope.drawBlackTitaniumAtmosphere(
    width: Float,
    height: Float,
    progress: Float,
    isDarkMode: Boolean,
    customAccent: Color?
) {
    val roseColor = customAccent ?: Color(0xFFE0A996)
    val time = progress * 6.2831855f

    val centerX = width * (0.5f + 0.1f * sin(time))
    val centerY = height * (0.35f + 0.08f * cos(time))
    val radius = width * 0.55f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                roseColor.copy(alpha = if (isDarkMode) 0.16f else 0.10f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )
}

private fun DrawScope.drawLiquidGlassAtmosphere(
    width: Float,
    height: Float,
    progress: Float,
    pulse: Float,
    isDarkMode: Boolean,
    customAccent: Color?,
    path: Path
) {
    val time = progress * 6.2831855f
    val cyanColor = customAccent ?: (if (isDarkMode) Color(0xFF00F5D4) else Color(0xFF0284C7))

    val orb1X = width * (0.3f + 0.18f * sin(time))
    val orb1Y = height * (0.25f + 0.12f * cos(time))
    val orb1Radius = width * 0.42f * pulse

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                cyanColor.copy(alpha = if (isDarkMode) 0.22f else 0.15f),
                Color.Transparent
            ),
            center = Offset(orb1X, orb1Y),
            radius = orb1Radius
        ),
        radius = orb1Radius,
        center = Offset(orb1X, orb1Y)
    )

    path.reset()
    path.moveTo(0f, height * 0.90f)
    var x = 0f
    while (x <= width) {
        val y = height * 0.90f + sin(time + x * 0.015f) * 14f
        path.lineTo(x, y)
        x += 24f
    }
    path.lineTo(width, height)
    path.lineTo(0f, height)
    path.close()

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            listOf(cyanColor.copy(alpha = 0.12f), Color.Transparent),
            startY = height * 0.88f,
            endY = height
        )
    )
}

private fun DrawScope.drawRustAtmosphere(
    width: Float,
    height: Float,
    progress: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0x28E76F51),
                Color.Transparent
            ),
            center = Offset(width * 0.5f, height * 0.95f),
            radius = width * 0.75f
        ),
        radius = width * 0.75f,
        center = Offset(width * 0.5f, height * 0.95f)
    )

    val emberCount = 10
    for (i in 0 until emberCount) {
        val seed = i * 137.5f
        val emberProgress = (progress + (i.toFloat() / emberCount)) % 1f
        val emberX = (seed % width) + sin(emberProgress * 6.28f + i) * 18f
        val emberY = height * (1f - emberProgress)
        val alpha = (sin(emberProgress * 3.14159f) * 0.6f).coerceIn(0f, 1f)

        drawCircle(
            color = Color(0xFFF4A261).copy(alpha = alpha),
            radius = 2.2f + (i % 3) * 1.2f,
            center = Offset(emberX, emberY)
        )
    }
}

private fun DrawScope.drawFlutterAtmosphere(
    width: Float,
    height: Float,
    progress: Float,
    pulse: Float,
    isDarkMode: Boolean,
    customAccent: Color?,
    path: Path
) {
    val time = progress * 6.2831855f
    val pinkColor = customAccent ?: Color(0xFFFF007F)

    val centerGlowRadius = width * 0.55f * pulse
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                pinkColor.copy(alpha = if (isDarkMode) 0.22f else 0.14f),
                Color.Transparent
            ),
            center = Offset(width * 0.5f, height * 0.4f),
            radius = centerGlowRadius
        ),
        radius = centerGlowRadius,
        center = Offset(width * 0.5f, height * 0.4f)
    )

    val particleCount = 10
    for (i in 0 until particleCount) {
        val p = (progress + (i.toFloat() / particleCount)) % 1f
        val px = width * (0.15f + 0.7f * ((i * 0.37f) % 1f)) + sin(time * 2f + i) * 24f
        val py = height * (1f - p)
        val flutterWing = sin(time * 5f + i * 1.5f)
        val alpha = (sin(p * 3.14159f) * 0.65f).coerceIn(0f, 1f)

        path.reset()
        path.moveTo(px, py)
        path.cubicTo(
            px - 8f * flutterWing, py - 9f,
            px - 10f * flutterWing, py + 2f,
            px, py + 2f
        )
        path.cubicTo(
            px + 10f * flutterWing, py + 2f,
            px + 8f * flutterWing, py - 9f,
            px, py
        )
        path.close()

        drawPath(path = path, color = pinkColor.copy(alpha = alpha))
    }
}

private fun DrawScope.drawEmeraldAtmosphere(
    width: Float,
    height: Float,
    progress: Float,
    pulse: Float,
    isDarkMode: Boolean,
    customAccent: Color?
) {
    val time = progress * 6.2831855f
    val emeraldColor = customAccent ?: (if (isDarkMode) Color(0xFF10B981) else Color(0xFF059669))

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                emeraldColor.copy(alpha = if (isDarkMode) 0.18f else 0.12f),
                Color.Transparent
            ),
            center = Offset(width * 0.5f, height * 0.3f),
            radius = width * 0.5f * pulse
        ),
        radius = width * 0.5f * pulse,
        center = Offset(width * 0.5f, height * 0.3f)
    )
}
