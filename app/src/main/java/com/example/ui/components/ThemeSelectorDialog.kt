package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccentColorOption
import com.example.model.ButtonShapeStyle
import com.example.model.PredefinedAccents
import com.example.model.ThemeStyle

@Composable
fun ThemeSelectorDialog(
    currentTheme: ThemeStyle,
    isDarkMode: Boolean,
    customAccent: Color?,
    shapeStyle: ButtonShapeStyle,
    onSelectTheme: (ThemeStyle) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectAccent: (Color?) -> Unit,
    onSelectShapeStyle: (ButtonShapeStyle) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = customAccent ?: currentTheme.getDefaultAccent(isDarkMode)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkMode) Color(0xFF16191E) else Color(0xFFF8F9FA),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.testTag("theme_selector_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = accent
                    )
                    Text(
                        text = "Titanium Studio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF111418)
                    )
                }

                // Dark / Light Mode Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f))
                        .clickable { onToggleDarkMode(!isDarkMode) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("toggle_dark_mode")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isDarkMode) "Dark" else "Light",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF1A1C20)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Predefined Themes
                Text(
                    text = "PREDEFINED FINISHES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    letterSpacing = 1.sp
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeStyle.values().forEach { theme ->
                        val isSelected = theme == currentTheme
                        ThemeOptionCard(
                            theme = theme,
                            isSelected = isSelected,
                            isDarkMode = isDarkMode,
                            onClick = { onSelectTheme(theme) }
                        )
                    }
                }

                Divider(color = if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))

                // Section 2: Custom Accent Colors
                Text(
                    text = "METALLIC & GEM ACCENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PredefinedAccents.options.forEach { option ->
                        val isChosen = customAccent == option.color
                        AccentChip(
                            option = option,
                            isSelected = isChosen,
                            onClick = {
                                if (isChosen) onSelectAccent(null) // reset to theme default
                                else onSelectAccent(option.color)
                            }
                        )
                    }
                }

                Divider(color = if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))

                // Section 3: Button Shape Style
                Text(
                    text = "KEYPAD BEZEL DESIGN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ButtonShapeStyle.values().forEach { shape ->
                        val isSelected = shape == shapeStyle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) accent.copy(alpha = 0.22f) else (if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f)),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) accent else (if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f))
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectShapeStyle(shape) }
                        ) {
                            Text(
                                text = shape.displayName.split(" ").first(),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accent else (if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f)),
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_close_theme_dialog")
            ) {
                Text(
                    text = "Apply Finish",
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun ThemeOptionCard(
    theme: ThemeStyle,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (theme) {
        ThemeStyle.GOLD_TITANIUM -> Icons.Default.Diamond
        ThemeStyle.BLACK_TITANIUM -> Icons.Default.Shield
        ThemeStyle.LIQUID_GLASS -> Icons.Default.WaterDrop
        ThemeStyle.RUST_INDUSTRIAL -> Icons.Default.Build
        ThemeStyle.FLUTTER_GLOW -> Icons.Default.AutoAwesome
        ThemeStyle.EMERALD_GOLD -> Icons.Default.Star
    }

    val accent = theme.getDefaultAccent(isDarkMode)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) accent.copy(alpha = 0.16f) else (if (isDarkMode) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.03f))
            )
            .border(
                1.2.dp,
                if (isSelected) accent else (if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.06f)),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("theme_option_${theme.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon badge with preview gradient
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(theme.getBackgroundColors(isDarkMode))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color(0xFF111418)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = theme.subtitle,
                    fontSize = 11.sp,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.65f) else Color(0xFF555B66),
                    lineHeight = 14.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AccentChip(
    option: AccentColorOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("accent_${option.name.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(option.color, option.secondaryColor)))
                .border(
                    if (isSelected) 2.5.dp else 1.dp,
                    if (isSelected) Color.White else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = option.name.split(" ").last(),
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) option.color else Color.Gray
        )
    }
}
