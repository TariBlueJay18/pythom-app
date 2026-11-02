package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExecutionResult
import com.example.engine.PythonEngine
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SnakeGreen
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TerminalTextStyle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TerminalConsole(
    result: ExecutionResult?,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(TerminalBlack)
                .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Console",
                    tint = TextMuted,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Terminal Ready",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap 'Run Code' above to execute your Python script against test cases.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(TerminalBlack)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status banner
        item {
            val isSuccess = result.isSuccess
            val statusColor = if (isSuccess) SnakeGreen else ErrorRed
            val statusBg = if (isSuccess) SnakeGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusBg)
                    .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = if (isSuccess) "Passed" else "Failed",
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSuccess) "All Test Cases Passed!" else "${result.passedTests} / ${result.totalTests} Tests Passed",
                            color = statusColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "${result.executionTimeMs} ms",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Global Error / Traceback if any
        if (result.globalError != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E1114))
                        .border(1.dp, ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Runtime Traceback:",
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.globalError,
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Stdout (print statements)
        if (result.globalStdout.isNotBlank()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Standard Output (stdout):",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = result.globalStdout.trimEnd(),
                        style = TerminalTextStyle
                    )
                }
            }
        }

        // Test Cases Breakdown
        item {
            Text(
                text = "Test Cases Results",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        itemsIndexed(result.results) { index, testResult ->
            val tc = testResult.testCase
            val passed = testResult.passed
            val cardBorder = if (passed) SnakeGreen.copy(alpha = 0.3f) else ErrorRed.copy(alpha = 0.3f)

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_case_result_$index")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = if (passed) "Pass" else "Fail",
                                tint = if (passed) SnakeGreen else ErrorRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Test Case ${index + 1}: ${tc.label}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = if (passed) "PASSED" else "FAILED",
                            color = if (passed) SnakeGreen else ErrorRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input args
                    val inputStr = tc.arguments.joinToString(", ") { PythonEngine.formatValue(it) }
                    Row {
                        Text(text = "Input: ", color = TextMuted, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "($inputStr)", color = TextPrimary, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Expected vs Actual
                    Row {
                        Text(text = "Expected: ", color = TextMuted, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                        Text(text = PythonEngine.formatValue(tc.expected), color = SnakeGreen, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row {
                        Text(text = "Output: ", color = TextMuted, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            text = PythonEngine.formatValue(testResult.actualOutput),
                            color = if (passed) SnakeGreen else ErrorRed,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (testResult.errorMessage != null && !passed) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = testResult.errorMessage,
                            color = ErrorRed,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
