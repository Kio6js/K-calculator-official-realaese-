package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ButtonShapeStyle
import com.example.ui.components.*
import com.example.util.HapticHelper
import com.example.util.HapticType

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val uiState by viewModel.uiState.collectAsState()
    val theme = uiState.selectedTheme
    val isDark = uiState.isDarkMode
    val accent = uiState.customAccent ?: theme.getDefaultAccent(isDark)

    var showMenu by remember { mutableStateOf(false) }

    // Chevron rotation animation
    val chevronRotation by animateFloatAsState(
        targetValue = if (uiState.isScientificExpanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.75f),
        label = "ChevronRotation"
    )

    // Handle back button when sheets or dialogs are open
    BackHandler(enabled = uiState.isHistoryOpen || uiState.isConverterOpen || uiState.isThemeDialogVisible) {
        if (uiState.isHistoryOpen) viewModel.onToggleHistory(false)
        else if (uiState.isConverterOpen) viewModel.onToggleConverter(false)
        else if (uiState.isThemeDialogVisible) viewModel.onToggleThemeDialog(false)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Atmospheric background with subtle gold aquarium shines & brushed titanium
        LiquidGlassBackground(
            themeStyle = theme,
            isDarkMode = isDark,
            customAccent = uiState.customAccent
        )

        // Main content column matching screenshot structure
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: History on Left, 3-dots Menu on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // History Button (Left clock with arrow icon)
                IconButton(
                    onClick = {
                        HapticHelper.perform(context, view, HapticType.TOGGLE_MODE, uiState.hapticsEnabled)
                        viewModel.onToggleHistory(true)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("btn_open_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF14171A),
                        modifier = Modifier.size(26.dp)
                    )
                }

                // 3-dots Overflow Menu (Right)
                Box {
                    IconButton(
                        onClick = {
                            HapticHelper.perform(context, view, HapticType.TOGGLE_MODE, uiState.hapticsEnabled)
                            showMenu = true
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("btn_overflow_menu")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF14171A),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(if (isDark) Color(0xFF1E222A) else Color(0xFFFFFFFF))
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Scientist Mode (${if (uiState.isScientificExpanded) "Active" else "Collapsed"})") },
                            leadingIcon = {
                                Icon(Icons.Default.Science, contentDescription = null, tint = accent)
                            },
                            onClick = {
                                showMenu = false
                                viewModel.onToggleScientific()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Theme Studio (Gold & Titanium)") },
                            leadingIcon = {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = accent)
                            },
                            onClick = {
                                showMenu = false
                                viewModel.onToggleThemeDialog(true)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Unit Converter") },
                            leadingIcon = {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = accent)
                            },
                            onClick = {
                                showMenu = false
                                viewModel.onToggleConverter(true)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(if (isDark) "Switch to Light Mode" else "Switch to Dark Mode") },
                            leadingIcon = {
                                Icon(
                                    if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = accent
                                )
                            },
                            onClick = {
                                showMenu = false
                                viewModel.onToggleDarkMode(!isDark)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(if (uiState.hapticsEnabled) "Disable Haptics" else "Enable Haptics") },
                            leadingIcon = {
                                Icon(
                                    if (uiState.hapticsEnabled) Icons.Default.Vibration else Icons.Default.Smartphone,
                                    contentDescription = null,
                                    tint = accent
                                )
                            },
                            onClick = {
                                showMenu = false
                                viewModel.onToggleHaptics()
                            }
                        )
                    }
                }
            }

            // Upper Display Section (Expansive, clean, scrollable expression, bold result)
            CalculatorDisplay(
                expression = uiState.expression,
                liveResult = uiState.liveResult,
                isError = uiState.isError,
                isDegreeMode = uiState.isDegreeMode,
                hasMemory = uiState.hasMemory,
                themeStyle = theme,
                isDarkMode = isDark,
                customAccent = uiState.customAccent,
                onBackspace = { viewModel.onBackspace() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Chevron Indicator for Scientist Mode with animated rotation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        HapticHelper.perform(context, view, HapticType.TOGGLE_MODE, uiState.hapticsEnabled)
                        viewModel.onToggleScientific()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .rotate(chevronRotation)
                        .testTag("btn_chevron_scientific")
                ) {
                    Icon(
                        imageVector = Icons.Default.UnfoldMore,
                        contentDescription = "Toggle Scientist Mode",
                        tint = if (uiState.isScientificExpanded) accent else (if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF14171A)),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Circular 4x5 Keypad Grid with Advanced Scientist Mode Tray
            KeypadLayout(
                themeStyle = theme,
                isDarkMode = isDark,
                customAccent = uiState.customAccent,
                shapeStyle = ButtonShapeStyle.CIRCULAR_BEZEL,
                isScientificExpanded = uiState.isScientificExpanded,
                isDegreeMode = uiState.isDegreeMode,
                hapticsEnabled = uiState.hapticsEnabled,
                onToggleScientific = { viewModel.onToggleScientific() },
                onToggleDegreeMode = { viewModel.onToggleDegreeMode() },
                onDigit = { viewModel.onDigit(it) },
                onOperator = { viewModel.onOperator(it) },
                onClear = { viewModel.onClear() },
                onBackspace = { viewModel.onBackspace() },
                onEquals = { viewModel.onEquals() },
                onSmartParenthesis = { viewModel.onSmartParenthesis() },
                onFunction = { viewModel.onFunction(it) },
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // Calculation History Sheet
        if (uiState.isHistoryOpen) {
            HistorySheet(
                historyList = uiState.historyList,
                themeStyle = theme,
                isDarkMode = isDark,
                customAccent = uiState.customAccent,
                onSelectExpression = { viewModel.onUseHistoryExpression(it) },
                onSelectResult = { viewModel.onUseHistoryResult(it) },
                onClearHistory = { viewModel.onClearHistory() },
                onDismiss = { viewModel.onToggleHistory(false) }
            )
        }

        // Unit Converter Sheet
        if (uiState.isConverterOpen) {
            ConverterSheet(
                themeStyle = theme,
                isDarkMode = isDark,
                customAccent = uiState.customAccent,
                onPasteToCalculator = { viewModel.onPasteConvertedValue(it) },
                onDismiss = { viewModel.onToggleConverter(false) }
            )
        }

        // Aesthetic Theme Selector Dialog
        if (uiState.isThemeDialogVisible) {
            ThemeSelectorDialog(
                currentTheme = theme,
                isDarkMode = isDark,
                customAccent = uiState.customAccent,
                shapeStyle = uiState.shapeStyle,
                onSelectTheme = { viewModel.onSelectTheme(it) },
                onToggleDarkMode = { viewModel.onToggleDarkMode(it) },
                onSelectAccent = { viewModel.onSelectAccent(it) },
                onSelectShapeStyle = { viewModel.onSelectShapeStyle(it) },
                onDismiss = { viewModel.onToggleThemeDialog(false) }
            )
        }
    }
}
