package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ExerciseProgressEntity
import com.example.data.local.entity.SnakeHighScoreEntity
import com.example.data.local.entity.UserStatsEntity
import com.example.data.model.Exercise
import com.example.data.model.ExerciseDifficulty
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    userStats: UserStatsEntity,
    progressList: List<ExerciseProgressEntity>,
    exercises: List<Exercise>,
    snakeScores: List<SnakeHighScoreEntity>,
    bestSnakeScore: Int,
    studySeconds: Long,
    onTakeBreakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedIds = progressList.filter { it.isCompleted }.map { it.exerciseId }.toSet()
    val completedExercises = exercises.filter { completedIds.contains(it.id) }
    val beginnerCount = completedExercises.count { it.difficulty == ExerciseDifficulty.BEGINNER }
    val intermediateCount = completedExercises.count { it.difficulty == ExerciseDifficulty.INTERMEDIATE }
    val advancedCount = completedExercises.count { it.difficulty == ExerciseDifficulty.ADVANCED }

    val totalStudyHours = (userStats.totalStudySeconds + studySeconds) / 3600
    val totalStudyMinutes = ((userStats.totalStudySeconds + studySeconds) % 3600) / 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Player Dashboard",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onTakeBreakClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SnakeGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("stats_play_snake_button")
                ) {
                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Snake", color = BackgroundDark, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Current Streak",
                        value = "${userStats.currentStreak} Days",
                        subtitle = "Daily practice",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = Color(0xFFF97316),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total XP",
                        value = "${userStats.totalXp} XP",
                        subtitle = "Earned in challenges",
                        icon = Icons.Default.Stars,
                        iconTint = PythonBlue,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Solved",
                        value = "${completedExercises.size} / ${exercises.size}",
                        subtitle = "Python exercises",
                        icon = Icons.Default.CheckCircle,
                        iconTint = SnakeGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Study Time",
                        value = "${totalStudyHours}h ${totalStudyMinutes}m",
                        subtitle = "Active focus time",
                        icon = Icons.Default.Timer,
                        iconTint = PythonYellow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Difficulty Progress Breakdown
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Mastery by Difficulty",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DifficultyProgressBar(
                        title = "Beginner",
                        completed = beginnerCount,
                        total = exercises.count { it.difficulty == ExerciseDifficulty.BEGINNER },
                        color = SnakeGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DifficultyProgressBar(
                        title = "Intermediate",
                        completed = intermediateCount,
                        total = exercises.count { it.difficulty == ExerciseDifficulty.INTERMEDIATE },
                        color = PythonYellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DifficultyProgressBar(
                        title = "Advanced",
                        completed = advancedCount,
                        total = exercises.count { it.difficulty == ExerciseDifficulty.ADVANCED },
                        color = Color(0xFFA855F7)
                    )
                }
            }
        }

        // Snake Arcade Leaderboard
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Snake Arcade High Scores",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Personal Best: $bestSnakeScore",
                    color = PythonYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        if (snakeScores.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No games recorded yet",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Take a break and set your first arcade high score!",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        } else {
            itemsIndexed(snakeScores) { index, scoreEntity ->
                val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.US).format(Date(scoreEntity.playedAt))

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (index == 0) PythonYellow.copy(alpha = 0.5f) else SurfaceBorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${index + 1}",
                                color = if (index == 0) PythonYellow else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(28.dp)
                            )
                            Column {
                                Text(
                                    text = "${scoreEntity.score} Points",
                                    color = if (index == 0) PythonYellow else SnakeGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Length: ${scoreEntity.snakeLength} • Duration: ${scoreEntity.durationSeconds}s",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = dateStr,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextMuted, fontSize = 11.sp)
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextSecondary, fontSize = 10.5.sp)
        }
    }
}

@Composable
private fun DifficultyProgressBar(
    title: String,
    completed: Int,
    total: Int,
    color: Color
) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = TextSecondary, fontSize = 12.sp)
            Text(text = "$completed / $total", color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.material3.LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = SurfaceVariantDark,
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp))
        )
    }
}
