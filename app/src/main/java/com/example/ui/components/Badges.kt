package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseDifficulty
import com.example.ui.theme.ArcadePurple
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.SnakeGreen
import com.example.ui.theme.SurfaceVariantDark

@Composable
fun DifficultyBadge(
    difficulty: ExerciseDifficulty,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (difficulty) {
        ExerciseDifficulty.BEGINNER -> Pair(SnakeGreen.copy(alpha = 0.18f), SnakeGreen)
        ExerciseDifficulty.INTERMEDIATE -> Pair(PythonYellow.copy(alpha = 0.18f), PythonYellow)
        ExerciseDifficulty.ADVANCED -> Pair(ArcadePurple.copy(alpha = 0.22f), ArcadePurple)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = difficulty.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag("difficulty_badge_${difficulty.name}")
        )
    }
}

@Composable
fun CompletedBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SnakeGreen.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Completed",
            tint = SnakeGreen,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Solved",
            color = SnakeGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StreakBadge(streak: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEA580C).copy(alpha = 0.2f))
            .border(1.dp, Color(0xFFEA580C).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Streak",
            tint = Color(0xFFF97316),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "$streak day${if (streak == 1) "" else "s"}",
            color = Color(0xFFFED7AA),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun XpBadge(xp: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PythonBlue.copy(alpha = 0.18f))
            .border(1.dp, PythonBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = "XP",
            tint = PythonBlue,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$xp XP",
            color = PythonBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
