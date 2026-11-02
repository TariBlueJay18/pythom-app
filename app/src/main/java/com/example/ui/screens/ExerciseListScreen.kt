package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.data.local.entity.ExerciseProgressEntity
import com.example.data.local.entity.UserStatsEntity
import com.example.data.model.Exercise
import com.example.data.model.ExerciseDifficulty
import com.example.ui.components.CompletedBadge
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.StreakBadge
import com.example.ui.components.StudyTimerBar
import com.example.ui.components.XpBadge
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
fun ExerciseListScreen(
    exercises: List<Exercise>,
    progressList: List<ExerciseProgressEntity>,
    userStats: UserStatsEntity,
    studySeconds: Long,
    selectedDifficulty: ExerciseDifficulty?,
    searchQuery: String,
    onDifficultySelected: (ExerciseDifficulty?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onExerciseClick: (String) -> Unit,
    onTakeBreakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedIds = progressList.filter { it.isCompleted }.map { it.exerciseId }.toSet()
    val totalExercises = exercises.size
    val completedCount = completedIds.size
    val completionPercentage = if (totalExercises > 0) (completedCount.toFloat() / totalExercises) else 0f

    val filteredExercises = exercises.filter { ex ->
        val matchesDifficulty = selectedDifficulty == null || ex.difficulty == selectedDifficulty
        val matchesSearch = searchQuery.isBlank() ||
                ex.title.contains(searchQuery, ignoreCase = true) ||
                ex.category.contains(searchQuery, ignoreCase = true) ||
                ex.description.contains(searchQuery, ignoreCase = true)
        matchesDifficulty && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Header Banner
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PythonBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "PySnake",
                            tint = PythonBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PySnake Lab",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Code. Solve. Take Breaks.",
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StreakBadge(streak = userStats.currentStreak)
                    Spacer(modifier = Modifier.width(8.dp))
                    XpBadge(xp = userStats.totalXp)
                }
            }
        }

        // Study Timer & Arcade Break Trigger
        item {
            StudyTimerBar(
                studySeconds = studySeconds,
                onTakeBreakClick = onTakeBreakClick
            )
        }

        // Completion Progress Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Learning Progress",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$completedCount of $totalExercises Completed",
                            color = PythonBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { completionPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PythonYellow,
                        trackColor = SurfaceVariantDark
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search topics, functions, algorithms...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedBorderColor = PythonBlue,
                    unfocusedBorderColor = SurfaceBorderDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exercise_search_input")
            )
        }

        // Difficulty Filters
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedDifficulty == null,
                        onClick = { onDifficultySelected(null) },
                        label = { Text("All (${exercises.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PythonBlue,
                            selectedLabelColor = BackgroundDark,
                            containerColor = SurfaceDark,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedDifficulty == null,
                            borderColor = SurfaceBorderDark
                        ),
                        modifier = Modifier.testTag("filter_all")
                    )
                }

                items(ExerciseDifficulty.values()) { diff ->
                    val isSelected = selectedDifficulty == diff
                    val count = exercises.count { it.difficulty == diff }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onDifficultySelected(if (isSelected) null else diff) },
                        label = { Text("${diff.label} ($count)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (diff) {
                                ExerciseDifficulty.BEGINNER -> SnakeGreen
                                ExerciseDifficulty.INTERMEDIATE -> PythonYellow
                                ExerciseDifficulty.ADVANCED -> Color(0xFFA855F7)
                            },
                            selectedLabelColor = BackgroundDark,
                            containerColor = SurfaceDark,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = SurfaceBorderDark
                        ),
                        modifier = Modifier.testTag("filter_${diff.name.lowercase()}")
                    )
                }
            }
        }

        // Exercise Items List
        items(filteredExercises) { exercise ->
            val isCompleted = completedIds.contains(exercise.id)

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCompleted) SnakeGreen.copy(alpha = 0.4f) else SurfaceBorderDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExerciseClick(exercise.id) }
                    .testTag("exercise_item_${exercise.id}")
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DifficultyBadge(difficulty = exercise.difficulty)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = exercise.category,
                                color = TextMuted,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (isCompleted) {
                            CompletedBadge()
                        } else {
                            Text(
                                text = "+${exercise.difficulty.xpReward} XP",
                                color = PythonBlue,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = exercise.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = exercise.description.lines().firstOrNull { it.isNotBlank() } ?: "",
                        color = TextSecondary,
                        fontSize = 12.5.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "def ${exercise.functionName}(...)",
                            color = PythonYellow,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isCompleted) "Review" else "Solve",
                                color = if (isCompleted) TextSecondary else SnakeGreen,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = if (isCompleted) TextMuted else SnakeGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
