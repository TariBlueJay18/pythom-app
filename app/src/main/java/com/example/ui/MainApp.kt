package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ExerciseDetailScreen
import com.example.ui.screens.ExerciseListScreen
import com.example.ui.screens.SnakeGameScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.PythonYellow
import com.example.ui.theme.SnakeGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.PySnakeViewModel

@Composable
fun MainApp(
    viewModel: PySnakeViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val progressList by viewModel.progressList.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val snakeHighScores by viewModel.snakeHighScores.collectAsStateWithLifecycle()
    val bestSnakeScore by viewModel.bestSnakeScore.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeExercise by viewModel.activeExercise.collectAsStateWithLifecycle()
    val userCode by viewModel.userCode.collectAsStateWithLifecycle()
    val executionResult by viewModel.executionResult.collectAsStateWithLifecycle()
    val isRunningTests by viewModel.isRunningTests.collectAsStateWithLifecycle()
    val selectedDetailTab by viewModel.selectedDetailTab.collectAsStateWithLifecycle()
    val showBreakModal by viewModel.showBreakPromptModal.collectAsStateWithLifecycle()
    val snakeGameState by viewModel.snakeGameState.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val sessionStudySeconds by viewModel.sessionStudySeconds.collectAsStateWithLifecycle()

    // Handle Back Press depending on screen
    BackHandler(enabled = currentScreen !is AppScreen.ExerciseList) {
        when (currentScreen) {
            is AppScreen.ExerciseDetail -> viewModel.navigateTo(AppScreen.ExerciseList)
            is AppScreen.SnakeGame -> viewModel.returnFromBreak()
            is AppScreen.Stats -> viewModel.navigateTo(AppScreen.ExerciseList)
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            // Show bottom bar unless playing Snake arcade (to maximize immersion while gaming!)
            if (currentScreen !is AppScreen.SnakeGame) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.ExerciseList || currentScreen is AppScreen.ExerciseDetail,
                        onClick = { viewModel.navigateTo(AppScreen.ExerciseList) },
                        icon = { Icon(Icons.Default.Code, contentDescription = "Exercises") },
                        label = { Text("Exercises", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BackgroundDark,
                            selectedTextColor = PythonBlue,
                            indicatorColor = PythonBlue,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_exercises")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.SnakeGame,
                        onClick = { viewModel.navigateTo(AppScreen.SnakeGame) },
                        icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Snake Break") },
                        label = { Text("Snake Break", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BackgroundDark,
                            selectedTextColor = SnakeGreen,
                            indicatorColor = SnakeGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_snake_break")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Stats,
                        onClick = { viewModel.navigateTo(AppScreen.Stats) },
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Dashboard") },
                        label = { Text("Stats", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BackgroundDark,
                            selectedTextColor = PythonYellow,
                            indicatorColor = PythonYellow,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_stats")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.ExerciseList -> {
                    ExerciseListScreen(
                        exercises = com.example.data.ExerciseCatalog.exercises,
                        progressList = progressList,
                        userStats = userStats,
                        studySeconds = sessionStudySeconds,
                        selectedDifficulty = selectedDifficulty,
                        searchQuery = searchQuery,
                        onDifficultySelected = { viewModel.setDifficultyFilter(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onExerciseClick = { viewModel.openExercise(it) },
                        onTakeBreakClick = { viewModel.navigateTo(AppScreen.SnakeGame) }
                    )
                }

                is AppScreen.ExerciseDetail -> {
                    val ex = activeExercise
                    if (ex != null) {
                        ExerciseDetailScreen(
                            exercise = ex,
                            code = userCode,
                            executionResult = executionResult,
                            isRunning = isRunningTests,
                            selectedTab = selectedDetailTab,
                            showBreakPrompt = showBreakModal,
                            onTabSelected = { viewModel.setDetailTab(it) },
                            onCodeChange = { viewModel.updateCode(it) },
                            onResetCode = { viewModel.resetCodeToStarter() },
                            onRunCode = { viewModel.runExerciseTests() },
                            onBackClick = { viewModel.navigateTo(AppScreen.ExerciseList) },
                            onTakeBreakClick = { viewModel.navigateTo(AppScreen.SnakeGame) },
                            onDismissBreakModal = { viewModel.dismissBreakModal() }
                        )
                    }
                }

                is AppScreen.SnakeGame -> {
                    SnakeGameScreen(
                        gameState = snakeGameState,
                        highScore = bestSnakeScore,
                        vibrationEnabled = vibrationEnabled,
                        onDirectionInput = { viewModel.onSnakeDirectionInput(it) },
                        onTogglePause = { viewModel.togglePauseSnakeGame() },
                        onRestart = { viewModel.restartSnakeGame() },
                        onToggleWrapAround = { viewModel.toggleWrapAroundWalls() },
                        onToggleVibration = { viewModel.toggleVibration() },
                        onReturnToCoding = { viewModel.returnFromBreak() }
                    )
                }

                is AppScreen.Stats -> {
                    StatsScreen(
                        userStats = userStats,
                        progressList = progressList,
                        exercises = com.example.data.ExerciseCatalog.exercises,
                        snakeScores = snakeHighScores,
                        bestSnakeScore = bestSnakeScore,
                        studySeconds = sessionStudySeconds,
                        onTakeBreakClick = { viewModel.navigateTo(AppScreen.SnakeGame) }
                    )
                }
            }
        }
    }
}
