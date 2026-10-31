package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeStyle
import com.example.model.UnitCategory
import com.example.model.UnitConverterData
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterSheet(
    themeStyle: ThemeStyle,
    isDarkMode: Boolean = true,
    customAccent: Color? = null,
    onPasteToCalculator: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = customAccent ?: themeStyle.getDefaultAccent(isDarkMode)

    var selectedCategory by remember { mutableStateOf(UnitCategory.LENGTH) }
    val units = remember(selectedCategory) {
        UnitConverterData.unitsByCategory[selectedCategory] ?: emptyList()
    }

    var fromUnitSymbol by remember(selectedCategory) {
        mutableStateOf(units.firstOrNull()?.symbol ?: "")
    }
    var toUnitSymbol by remember(selectedCategory) {
        mutableStateOf(units.getOrNull(1)?.symbol ?: units.firstOrNull()?.symbol ?: "")
    }
    var inputValue by remember { mutableStateOf("1") }

    val convertedValue = remember(inputValue, fromUnitSymbol, toUnitSymbol, selectedCategory) {
        val num = inputValue.toDoubleOrNull() ?: 0.0
        val res = UnitConverterData.convert(num, fromUnitSymbol, toUnitSymbol, selectedCategory)
        val df = DecimalFormat("#,##0.######")
        df.format(res)
    }

    var expandedFromDropdown by remember { mutableStateOf(false) }
    var expandedToDropdown by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkMode) themeStyle.getBackgroundColors(true).first() else Color(0xFFF8F9FA),
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = themeStyle.getTextSecondary(isDarkMode).copy(alpha = 0.5f)
            )
        },
        modifier = Modifier.testTag("converter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Converter",
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Unit Converter",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.getTextPrimary(isDarkMode)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = themeStyle.getTextSecondary(isDarkMode)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Chips (Horizontal Scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UnitCategory.values().forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = category
                        },
                        label = {
                            Text(
                                text = category.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accent.copy(alpha = if (isDarkMode) 0.25f else 0.18f),
                            selectedLabelColor = accent,
                            containerColor = if (isDarkMode) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.04f),
                            labelColor = themeStyle.getTextSecondary(isDarkMode)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) accent else (if (isDarkMode) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f))
                        ),
                        modifier = Modifier.testTag("chip_${category.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Input Value Field
            OutlinedTextField(
                value = inputValue,
                onValueChange = { newVal ->
                    if (newVal.isEmpty() || newVal.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                        inputValue = newVal
                    }
                },
                label = { Text("Value to convert", color = themeStyle.getTextSecondary(isDarkMode)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = themeStyle.getTextPrimary(isDarkMode),
                    unfocusedTextColor = themeStyle.getTextPrimary(isDarkMode),
                    focusedBorderColor = accent,
                    unfocusedBorderColor = if (isDarkMode) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f),
                    cursorColor = accent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("converter_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // From & To Unit Selection with Swap Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // From Dropdown Box
                Box(modifier = Modifier.weight(1f)) {
                    UnitSelectorCard(
                        label = "From",
                        symbol = fromUnitSymbol,
                        units = units,
                        themeStyle = themeStyle,
                        isDarkMode = isDarkMode,
                        onClick = { expandedFromDropdown = true }
                    )
                    DropdownMenu(
                        expanded = expandedFromDropdown,
                        onDismissRequest = { expandedFromDropdown = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text("${u.name} (${u.symbol})") },
                                onClick = {
                                    fromUnitSymbol = u.symbol
                                    expandedFromDropdown = false
                                }
                            )
                        }
                    }
                }

                // Swap Button
                IconButton(
                    onClick = {
                        val temp = fromUnitSymbol
                        fromUnitSymbol = toUnitSymbol
                        toUnitSymbol = temp
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                        .testTag("converter_swap_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Units",
                        tint = accent
                    )
                }

                // To Dropdown Box
                Box(modifier = Modifier.weight(1f)) {
                    UnitSelectorCard(
                        label = "To",
                        symbol = toUnitSymbol,
                        units = units,
                        themeStyle = themeStyle,
                        isDarkMode = isDarkMode,
                        onClick = { expandedToDropdown = true }
                    )
                    DropdownMenu(
                        expanded = expandedToDropdown,
                        onDismissRequest = { expandedToDropdown = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text("${u.name} (${u.symbol})") },
                                onClick = {
                                    toUnitSymbol = u.symbol
                                    expandedToDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Result Display Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                    .border(
                        1.2.dp,
                        Brush.horizontalGradient(
                            listOf(
                                if (isDarkMode) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f),
                                accent.copy(alpha = 0.5f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Result",
                        fontSize = 12.sp,
                        color = themeStyle.getTextSecondary(isDarkMode),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$convertedValue $toUnitSymbol",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.getTextPrimary(isDarkMode),
                        modifier = Modifier.testTag("converter_result_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Paste to Calculator Button
            Button(
                onClick = {
                    val rawClean = convertedValue.replace(",", "")
                    onPasteToCalculator(rawClean)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_paste_to_calc")
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = themeStyle.getEqualsText(isDarkMode),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Insert into Calculator",
                    color = themeStyle.getEqualsText(isDarkMode),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UnitSelectorCard(
    label: String,
    symbol: String,
    units: List<com.example.model.UnitItem>,
    themeStyle: ThemeStyle,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val unitName = units.find { it.symbol == symbol }?.name ?: symbol

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
            .border(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Text(text = label, fontSize = 11.sp, color = themeStyle.getTextSecondary(isDarkMode))
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$symbol ($unitName)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = themeStyle.getTextPrimary(isDarkMode),
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = themeStyle.getTextSecondary(isDarkMode)
                )
            }
        }
    }
}
