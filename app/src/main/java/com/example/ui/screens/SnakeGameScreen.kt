package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.Direction
import com.example.game.FoodType
import com.example.game.GameStatus
import com.example.game.SnakeGameState
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.SnakeGreen
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.abs

@Composable
fun SnakeGameScreen(
    gameState: SnakeGameState,
    highScore: Int,
    vibrationEnabled: Boolean,
    onDirectionInput: (Direction) -> Unit,
    onTogglePause: () -> Unit,
    onRestart: () -> Unit,
    onToggleWrapAround: () -> Unit,
    onToggleVibration: () -> Unit,
    onReturnToCoding: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for bonus food & high score
    val infiniteTransition = rememberInfiniteTransition(label = "snake_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Game Over Dialog
    if (gameState.status == GameStatus.GAME_OVER) {
        AlertDialog(
            onDismissRequest = { /* stay on dialog */ },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💥", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Game Over!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (gameState.isNewHighScore) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PythonYellow.copy(alpha = 0.2f))
                                .border(1.dp, PythonYellow.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏆 NEW HIGH SCORE: ${gameState.score}!",
                                color = PythonYellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Final Score:", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "${gameState.score}", color = SnakeGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Apples / Items Eaten:", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "${gameState.foodEatenCount}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Snake Length:", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "${gameState.length}", color = PythonBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Game Duration:", color = TextSecondary, fontSize = 13.sp)
                        Text(text = "${gameState.gameDurationSeconds}s", color = TextMuted, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recharged and ready to write more clean Python code?",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onReturnToCoding,
                    colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
                    modifier = Modifier.testTag("game_over_return_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Return",
                        tint = BackgroundDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Return to Coding", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier.testTag("game_over_play_again_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Play Again",
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Again", color = TextPrimary)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Arcade Header & Return to Coding button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SnakeGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Arcade",
                        tint = SnakeGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SNAKE ARCADE",
                        color = SnakeGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Study Break Zone",
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }

            // Return to Coding button (CRITICAL requirement)
            Button(
                onClick = onReturnToCoding,
                colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("return_to_coding_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "Code",
                    tint = BackgroundDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Back to Lab",
                    color = BackgroundDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats Scoreboard Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score
                Column {
                    Text(text = "SCORE", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "${gameState.score}",
                        color = SnakeGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // High score
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "High Score",
                            tint = PythonYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "BEST", color = PythonYellow, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                    Text(
                        text = "${maxOf(highScore, gameState.score)}",
                        color = PythonYellow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Length & Items
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "LENGTH", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "${gameState.length} (🍎 ${gameState.foodEatenCount})",
                        color = PythonBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Game Board Canvas (with swipe detection)
        var totalDragX by remember { mutableFloatStateOf(0f) }
        var totalDragY by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(TerminalBlack)
                .border(2.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            totalDragX = 0f
                            totalDragY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y

                            val threshold = 35f
                            if (abs(totalDragX) > threshold || abs(totalDragY) > threshold) {
                                if (abs(totalDragX) > abs(totalDragY)) {
                                    if (totalDragX > 0) onDirectionInput(Direction.RIGHT)
                                    else onDirectionInput(Direction.LEFT)
                                } else {
                                    if (totalDragY > 0) onDirectionInput(Direction.DOWN)
                                    else onDirectionInput(Direction.UP)
                                }
                                totalDragX = 0f
                                totalDragY = 0f
                            }
                        }
                    )
                }
                .testTag("snake_canvas_box"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                val gridW = gameState.gridWidth
                val gridH = gameState.gridHeight
                val cellW = size.width / gridW
                val cellH = size.height / gridH

                // Draw subtle grid lines
                for (x in 0..gridW) {
                    drawLine(
                        color = Color(0xFF131826),
                        start = Offset(x * cellW, 0f),
                        end = Offset(x * cellW, size.height),
                        strokeWidth = 0.5f
                    )
                }
                for (y in 0..gridH) {
                    drawLine(
                        color = Color(0xFF131826),
                        start = Offset(0f, y * cellH),
                        end = Offset(size.width, y * cellH),
                        strokeWidth = 0.5f
                    )
                }

                // Draw Foods
                for (food in gameState.foods) {
                    val fx = food.position.x * cellW
                    val fy = food.position.y * cellH
                    val center = Offset(fx + cellW / 2, fy + cellH / 2)

                    when (food.type) {
                        FoodType.APPLE -> {
                            val radius = (cellW / 2.2f)
                            drawCircle(
                                color = Color(0xFFEF4444),
                                radius = radius,
                                center = center
                            )
                            // Apple highlight
                            drawCircle(
                                color = Color(0xFFFCA5A5),
                                radius = radius * 0.35f,
                                center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f)
                            )
                        }
                        FoodType.GOLDEN_BUG -> {
                            val radius = (cellW / 2.0f) * pulseScale
                            drawCircle(
                                color = PythonYellow,
                                radius = radius,
                                center = center
                            )
                            drawCircle(
                                color = Color.White,
                                radius = radius * 0.4f,
                                center = center
                            )
                        }
                        FoodType.BONUS_STAR -> {
                            val radius = (cellW / 2.0f) * pulseScale
                            drawCircle(
                                color = Color(0xFFA855F7),
                                radius = radius,
                                center = center
                            )
                            drawCircle(
                                color = Color(0xFFE9D5FF),
                                radius = radius * 0.4f,
                                center = center
                            )
                        }
                    }
                }

                // Draw Snake
                val snake = gameState.snake
                for (i in snake.indices) {
                    val seg = snake[i]
                    val sx = seg.x * cellW
                    val sy = seg.y * cellH
                    val inset = 1.5f

                    if (i == 0) {
                        // Snake Head (Python Yellow/Cyan with eyes)
                        drawRoundRect(
                            color = PythonBlue,
                            topLeft = Offset(sx + inset, sy + inset),
                            size = Size(cellW - inset * 2, cellH - inset * 2),
                            cornerRadius = CornerRadius(cellW * 0.4f, cellH * 0.4f)
                        )

                        // Eyes looking in current direction
                        val eyeRadius = cellW * 0.12f
                        val dir = gameState.direction
                        val (eye1Offset, eye2Offset) = when (dir) {
                            Direction.UP -> Pair(
                                Offset(sx + cellW * 0.3f, sy + cellH * 0.28f),
                                Offset(sx + cellW * 0.7f, sy + cellH * 0.28f)
                            )
                            Direction.DOWN -> Pair(
                                Offset(sx + cellW * 0.3f, sy + cellH * 0.72f),
                                Offset(sx + cellW * 0.7f, sy + cellH * 0.72f)
                            )
                            Direction.LEFT -> Pair(
                                Offset(sx + cellW * 0.28f, sy + cellH * 0.3f),
                                Offset(sx + cellW * 0.28f, sy + cellH * 0.7f)
                            )
                            Direction.RIGHT -> Pair(
                                Offset(sx + cellW * 0.72f, sy + cellH * 0.3f),
                                Offset(sx + cellW * 0.72f, sy + cellH * 0.7f)
                            )
                        }
                        drawCircle(color = Color.White, radius = eyeRadius, center = eye1Offset)
                        drawCircle(color = Color.White, radius = eyeRadius, center = eye2Offset)
                        drawCircle(color = Color.Black, radius = eyeRadius * 0.5f, center = eye1Offset)
                        drawCircle(color = Color.Black, radius = eyeRadius * 0.5f, center = eye2Offset)
                    } else {
                        // Body Segments: Gradient towards tail
                        val ratio = (i.toFloat() / snake.size).coerceIn(0f, 1f)
                        val bodyColor = if (i % 2 == 0) {
                            // Python dual-color snake: alternates between bright green & python blue
                            SnakeGreen.copy(alpha = 1f - (ratio * 0.35f))
                        } else {
                            Color(0xFF22D3EE).copy(alpha = 1f - (ratio * 0.35f))
                        }

                        drawRoundRect(
                            color = bodyColor,
                            topLeft = Offset(sx + inset, sy + inset),
                            size = Size(cellW - inset * 2, cellH - inset * 2),
                            cornerRadius = CornerRadius(cellW * 0.25f, cellH * 0.25f)
                        )
                    }
                }
            }

            // Overlay for Ready / Paused
            if (gameState.status == GameStatus.READY) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundDark.copy(alpha = 0.85f))
                        .border(1.dp, SnakeGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🐍 Swipe or use D-Pad to Start",
                            color = SnakeGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Eat apples to grow & speed up!",
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }
            } else if (gameState.status == GameStatus.PAUSED) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundDark.copy(alpha = 0.85f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⏸️ PAUSED",
                        color = PythonYellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Arcade 4-Way D-Pad & Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Options: Wrap mode & Vibration & Restart
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onTogglePause,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceDark),
                    modifier = Modifier.testTag("snake_pause_toggle")
                ) {
                    Icon(
                        imageVector = if (gameState.status == GameStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = PythonYellow
                    )
                }

                IconButton(
                    onClick = onRestart,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceDark),
                    modifier = Modifier.testTag("snake_restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Restart",
                        tint = TextPrimary
                    )
                }

                IconButton(
                    onClick = onToggleVibration,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (vibrationEnabled) SnakeGreen.copy(alpha = 0.2f) else SurfaceDark
                    ),
                    modifier = Modifier.testTag("snake_vibration_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Vibration",
                        tint = if (vibrationEnabled) SnakeGreen else TextMuted
                    )
                }
            }

            // Ergonomic 4-Way Arcade D-Pad
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // UP
                FilledIconButton(
                    onClick = { onDirectionInput(Direction.UP) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceVariantDark),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(48.dp)
                        .testTag("dpad_up")
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = TextPrimary, modifier = Modifier.size(28.dp))
                }

                // DOWN
                FilledIconButton(
                    onClick = { onDirectionInput(Direction.DOWN) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceVariantDark),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(48.dp)
                        .testTag("dpad_down")
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = TextPrimary, modifier = Modifier.size(28.dp))
                }

                // LEFT
                FilledIconButton(
                    onClick = { onDirectionInput(Direction.LEFT) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceVariantDark),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp)
                        .testTag("dpad_left")
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = TextPrimary, modifier = Modifier.size(28.dp))
                }

                // RIGHT
                FilledIconButton(
                    onClick = { onDirectionInput(Direction.RIGHT) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceVariantDark),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(48.dp)
                        .testTag("dpad_right")
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = TextPrimary, modifier = Modifier.size(28.dp))
                }

                // Center Action (Pause / Start)
                Surface(
                    onClick = onTogglePause,
                    shape = CircleShape,
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (gameState.status == GameStatus.PLAYING) SnakeGreen else PythonYellow)
                        )
                    }
                }
            }
        }
    }
}
