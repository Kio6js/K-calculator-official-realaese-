package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculationHistory
import com.example.model.ThemeStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    historyList: List<CalculationHistory>,
    themeStyle: ThemeStyle,
    isDarkMode: Boolean = true,
    customAccent: Color? = null,
    onSelectExpression: (String) -> Unit,
    onSelectResult: (String) -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var showConfirmClear by remember { mutableStateOf(false) }
    val accent = customAccent ?: themeStyle.getDefaultAccent(isDarkMode)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkMode) themeStyle.getBackgroundColors(true).first() else Color(0xFFF8F9FA),
        scrimColor = Color.Black.copy(alpha = 0.6f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = themeStyle.getTextSecondary(isDarkMode).copy(alpha = 0.5f)
            )
        },
        modifier = Modifier.testTag("history_bottom_sheet")
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
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Calculation Tape",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeStyle.getTextPrimary(isDarkMode)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = { showConfirmClear = true },
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear History",
                                tint = Color(0xFFFF5252).copy(alpha = 0.8f)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = themeStyle.getTextSecondary(isDarkMode)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (historyList.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = themeStyle.getTextSecondary(isDarkMode).copy(alpha = 0.35f),
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "No calculations yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeStyle.getTextSecondary(isDarkMode)
                    )
                    Text(
                        text = "Results and equations will be saved to your tape as you calculate.",
                        fontSize = 13.sp,
                        color = themeStyle.getTextSecondary(isDarkMode).copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        HistoryItemCard(
                            item = item,
                            themeStyle = themeStyle,
                            isDarkMode = isDarkMode,
                            accent = accent,
                            onUseExpression = {
                                onSelectExpression(item.expression)
                                onDismiss()
                            },
                            onUseResult = {
                                onSelectResult(item.result)
                                onDismiss()
                            },
                            onCopy = {
                                clipboardManager.setText(AnnotatedString("${item.expression} = ${item.result}"))
                            }
                        )
                    }
                }
            }
        }
    }

    if (showConfirmClear) {
        AlertDialog(
            onDismissRequest = { showConfirmClear = false },
            title = { Text("Clear All History?") },
            text = { Text("All tape calculations will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showConfirmClear = false
                    }
                ) {
                    Text("Clear All", color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClear = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: CalculationHistory,
    themeStyle: ThemeStyle,
    isDarkMode: Boolean,
    accent: Color,
    onUseExpression: () -> Unit,
    onUseResult: () -> Unit,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isDarkMode) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        if (isDarkMode) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f),
                        accent.copy(alpha = 0.35f)
                    )
                ),
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
            .testTag("history_item_${item.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.formattedDate} • ${item.formattedTime}",
                    fontSize = 11.sp,
                    color = themeStyle.getTextSecondary(isDarkMode).copy(alpha = 0.6f)
                )

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy calculation",
                        tint = themeStyle.getTextSecondary(isDarkMode).copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Expression
            Text(
                text = item.expression,
                fontSize = 16.sp,
                color = themeStyle.getTextSecondary(isDarkMode),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUseExpression() }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Result
            Text(
                text = "= ${item.result}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = themeStyle.getTextPrimary(isDarkMode),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUseResult() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = accent.copy(alpha = if (isDarkMode) 0.18f else 0.14f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { onUseResult() }
                ) {
                    Text(
                        text = "Use Result",
                        color = accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { onUseExpression() }
                ) {
                    Text(
                        text = "Edit Equation",
                        color = themeStyle.getTextSecondary(isDarkMode),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
