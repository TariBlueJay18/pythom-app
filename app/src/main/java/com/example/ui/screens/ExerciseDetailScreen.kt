package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.ExecutionResult
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.PythonEditorField
import com.example.ui.components.TerminalConsole
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.SnakeGreen
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    code: String,
    executionResult: ExecutionResult?,
    isRunning: Boolean,
    selectedTab: Int,
    showBreakPrompt: Boolean,
    onTabSelected: (Int) -> Unit,
    onCodeChange: (String) -> Unit,
    onResetCode: () -> Unit,
    onRunCode: () -> Unit,
    onBackClick: () -> Unit,
    onTakeBreakClick: () -> Unit,
    onDismissBreakModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHint by remember { mutableStateOf(false) }

    // Success & Break Modal
    if (showBreakPrompt) {
        AlertDialog(
            onDismissRequest = onDismissBreakModal,
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎉", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Challenge Solved!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "All test cases passed cleanly! You've earned +${exercise.difficulty.xpReward} XP.",
                        color = TextSecondary,
                        fontSize = 13.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SnakeGreen.copy(alpha = 0.15f))
                            .border(1.dp, SnakeGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = "Arcade",
                                tint = SnakeGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Time for a Snake Break!",
                                    color = SnakeGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Recharge your mind with an arcade session before tackling the next algorithm.",
                                    color = TextSecondary,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismissBreakModal()
                        onTakeBreakClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnakeGreen),
                    modifier = Modifier.testTag("modal_take_break_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Play",
                        tint = BackgroundDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play Snake Game", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismissBreakModal,
                    modifier = Modifier.testTag("modal_continue_coding_button")
                ) {
                    Text("Continue Coding", color = TextPrimary)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = exercise.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DifficultyBadge(difficulty = exercise.difficulty)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = exercise.category,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Quick Break Arcade trigger
            Button(
                onClick = onTakeBreakClick,
                colors = ButtonDefaults.buttonColors(containerColor = PythonYellow),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("detail_take_break_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "Break",
                    tint = BackgroundDark,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Break",
                    color = BackgroundDark,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Layout responsiveness: Tablet (split pane) vs Phone (tabbed)
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isTablet = maxWidth >= 700.dp

            if (isTablet) {
                // Side-by-side layout on tablets/wide screens
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Pane: Problem Description
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        ProblemDescriptionView(
                            exercise = exercise,
                            showHint = showHint,
                            onToggleHint = { showHint = !showHint }
                        )
                    }

                    // Right Pane: Editor & Terminal
                    Column(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = if (selectedTab == 0) 0 else selectedTab - 1,
                            containerColor = SurfaceDark,
                            contentColor = TextPrimary,
                            indicator = { tabPositions ->
                                val activeIdx = (if (selectedTab == 0) 0 else selectedTab - 1).coerceIn(0, tabPositions.size - 1)
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeIdx]),
                                    color = PythonBlue
                                )
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { onTabSelected(1) },
                                text = { Text("Code Editor", fontSize = 13.sp) },
                                icon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { onTabSelected(2) },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Console & Tests", fontSize = 13.sp)
                                        if (executionResult != null) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (executionResult.isSuccess) SnakeGreen else Color(0xFFEF4444))
                                            )
                                        }
                                    }
                                },
                                icon = { Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            if (selectedTab == 2) {
                                TerminalConsole(result = executionResult)
                            } else {
                                PythonEditorField(
                                    code = code,
                                    onCodeChange = onCodeChange,
                                    onResetClick = onResetCode,
                                    onRunClick = onRunCode,
                                    isRunning = isRunning
                                )
                            }
                        }
                    }
                }
            } else {
                // Phone vertical tabbed layout
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfaceDark,
                        contentColor = TextPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = PythonBlue
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { onTabSelected(0) },
                            text = { Text("Problem", fontSize = 12.5.sp) },
                            icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("tab_problem")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { onTabSelected(1) },
                            text = { Text("Code", fontSize = 12.5.sp) },
                            icon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("tab_code")
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { onTabSelected(2) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Tests", fontSize = 12.5.sp)
                                    if (executionResult != null) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(RoundedCornerShape(3.5.dp))
                                                .background(if (executionResult.isSuccess) SnakeGreen else Color(0xFFEF4444))
                                        )
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.testTag("tab_tests")
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        when (selectedTab) {
                            0 -> ProblemDescriptionView(
                                exercise = exercise,
                                showHint = showHint,
                                onToggleHint = { showHint = !showHint }
                            )
                            1 -> PythonEditorField(
                                code = code,
                                onCodeChange = onCodeChange,
                                onResetClick = onResetCode,
                                onRunClick = onRunCode,
                                isRunning = isRunning
                            )
                            2 -> TerminalConsole(result = executionResult)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProblemDescriptionView(
    exercise: Exercise,
    showHint: Boolean,
    onToggleHint: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Description Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Description",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = exercise.description,
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Function Signature preview
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Target Function",
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundDark)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "def ${exercise.functionName}(...)",
                        color = PythonYellow,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Visible Test Cases preview
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Sample Test Cases (${exercise.testCases.size} total)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                exercise.testCases.filter { !it.isHidden }.forEachIndexed { idx, tc ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BackgroundDark)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Example ${idx + 1}: ${tc.label}",
                                color = PythonBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Input: (${tc.arguments.joinToString(", ")})",
                                color = TextSecondary,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Expected Output: ${tc.expected}",
                                color = SnakeGreen,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Solution Hint Accordion
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = PythonYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Need a Hint?",
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onToggleHint,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("toggle_hint_button")
                    ) {
                        Text(
                            text = if (showHint) "Hide" else "Reveal",
                            fontSize = 11.sp,
                            color = PythonYellow
                        )
                    }
                }

                if (showHint) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PythonYellow.copy(alpha = 0.12f))
                            .border(1.dp, PythonYellow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = exercise.solutionHint,
                            color = TextPrimary,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
