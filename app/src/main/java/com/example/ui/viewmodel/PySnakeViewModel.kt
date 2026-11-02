package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExerciseCatalog
import com.example.data.local.PySnakeDatabase
import com.example.data.local.entity.ExerciseProgressEntity
import com.example.data.local.entity.SnakeHighScoreEntity
import com.example.data.local.entity.UserStatsEntity
import com.example.data.model.Exercise
import com.example.data.model.ExerciseDifficulty
import com.example.data.model.ExecutionResult
import com.example.data.repository.PySnakeRepository
import com.example.engine.PythonEngine
import com.example.game.Direction
import com.example.game.GameStatus
import com.example.game.SnakeEngine
import com.example.game.SnakeGameState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class AppScreen {
    object ExerciseList : AppScreen()
    data class ExerciseDetail(val exerciseId: String) : AppScreen()
    object SnakeGame : AppScreen()
    object Stats : AppScreen()
}

class PySnakeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PySnakeRepository

    init {
        val db = PySnakeDatabase.getDatabase(application)
        repository = PySnakeRepository(db.pySnakeDao())
    }

    // Navigation state
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.ExerciseList)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Store return destination when break mode is triggered
    private var returnScreenAfterBreak: AppScreen = AppScreen.ExerciseList

    // Database reactive flows
    val progressList: StateFlow<List<ExerciseProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStats: StateFlow<UserStatsEntity> = repository.userStats
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserStatsEntity(id = 1, currentStreak = 1)
        )

    val snakeHighScores: StateFlow<List<SnakeHighScoreEntity>> = repository.topSnakeScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSnakeScore: StateFlow<Int> = repository.bestSnakeScore
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Exercise list UI state
    private val _selectedDifficulty = MutableStateFlow<ExerciseDifficulty?>(null)
    val selectedDifficulty: StateFlow<ExerciseDifficulty?> = _selectedDifficulty.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active Exercise Detail state
    private val _activeExercise = MutableStateFlow<Exercise?>(null)
    val activeExercise: StateFlow<Exercise?> = _activeExercise.asStateFlow()

    private val _userCode = MutableStateFlow("")
    val userCode: StateFlow<String> = _userCode.asStateFlow()

    private val _executionResult = MutableStateFlow<ExecutionResult?>(null)
    val executionResult: StateFlow<ExecutionResult?> = _executionResult.asStateFlow()

    private val _isRunningTests = MutableStateFlow(false)
    val isRunningTests: StateFlow<Boolean> = _isRunningTests.asStateFlow()

    private val _selectedDetailTab = MutableStateFlow(0) // 0: Description, 1: Code, 2: Tests/Console
    val selectedDetailTab: StateFlow<Int> = _selectedDetailTab.asStateFlow()

    private val _showBreakPromptModal = MutableStateFlow(false)
    val showBreakPromptModal: StateFlow<Boolean> = _showBreakPromptModal.asStateFlow()

    // Snake Game state
    private val _snakeGameState = MutableStateFlow(SnakeEngine.createInitialState())
    val snakeGameState: StateFlow<SnakeGameState> = _snakeGameState.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private var snakeGameLoopJob: Job? = null
    private var gameTimerJob: Job? = null

    // Study session timer (seconds)
    private val _sessionStudySeconds = MutableStateFlow(0L)
    val sessionStudySeconds: StateFlow<Long> = _sessionStudySeconds.asStateFlow()
    private val studyGoalSeconds = 20 * 60L // 20 minutes study target

    private var studyTimerJob: Job? = null

    init {
        startStudyTimer()
    }

    // --- Navigation ---
    fun navigateTo(screen: AppScreen) {
        if (screen is AppScreen.SnakeGame) {
            // Keep track of where to return
            returnScreenAfterBreak = _currentScreen.value
            // Setup initial snake state with best high score
            _snakeGameState.value = SnakeEngine.createInitialState(
                highScore = bestSnakeScore.value
            )
        }
        _currentScreen.value = screen
    }

    fun returnFromBreak() {
        pauseSnakeGame()
        _currentScreen.value = returnScreenAfterBreak
    }

    fun openExercise(exerciseId: String) {
        val exercise = ExerciseCatalog.getExerciseById(exerciseId)
        if (exercise != null) {
            _activeExercise.value = exercise
            _executionResult.value = null
            _selectedDetailTab.value = 1 // Open directly to Code Editor for action

            viewModelScope.launch {
                val saved = repository.getExerciseProgress(exerciseId)
                _userCode.value = if (!saved?.savedCode.isNullOrEmpty()) saved!!.savedCode else exercise.starterCode
            }
            navigateTo(AppScreen.ExerciseDetail(exerciseId))
        }
    }

    fun setDetailTab(index: Int) {
        _selectedDetailTab.value = index
    }

    fun setDifficultyFilter(difficulty: ExerciseDifficulty?) {
        _selectedDifficulty.value = difficulty
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCode(newCode: String) {
        _userCode.value = newCode
    }

    fun resetCodeToStarter() {
        val current = _activeExercise.value ?: return
        _userCode.value = current.starterCode
    }

    fun runExerciseTests() {
        val exercise = _activeExercise.value ?: return
        _isRunningTests.value = true

        viewModelScope.launch {
            // Short delay for UI responsiveness
            delay(150)
            val result = PythonEngine.executeExercise(exercise, _userCode.value)
            _executionResult.value = result
            _isRunningTests.value = false
            _selectedDetailTab.value = 2 // Switch to Console & Tests tab to view result

            // Save in database
            repository.saveExerciseSolution(
                exerciseId = exercise.id,
                code = _userCode.value,
                passed = result.isSuccess,
                xpReward = exercise.difficulty.xpReward
            )

            if (result.isSuccess) {
                // Show celebration & offer Snake break!
                _showBreakPromptModal.value = true
                triggerHaptic(HapticType.SUCCESS)
            }
        }
    }

    fun dismissBreakModal() {
        _showBreakPromptModal.value = false
    }

    // --- Study Timer ---
    private fun startStudyTimer() {
        studyTimerJob?.cancel()
        studyTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _sessionStudySeconds.update { it + 1 }
                // Persist study time every 60 seconds
                if (_sessionStudySeconds.value % 60 == 0L) {
                    repository.addStudySeconds(60L)
                }
            }
        }
    }

    // --- Snake Game Operations ---
    fun startSnakeGame() {
        val currentState = _snakeGameState.value
        val stateToUse = if (currentState.status == GameStatus.GAME_OVER) {
            SnakeEngine.createInitialState(
                highScore = bestSnakeScore.value,
                wrapAround = currentState.wrapAroundWalls
            )
        } else {
            currentState
        }

        _snakeGameState.value = stateToUse.copy(status = GameStatus.PLAYING)
        runGameLoop()
        runGameTimer()
    }

    fun pauseSnakeGame() {
        snakeGameLoopJob?.cancel()
        gameTimerJob?.cancel()
        if (_snakeGameState.value.status == GameStatus.PLAYING) {
            _snakeGameState.value = _snakeGameState.value.copy(status = GameStatus.PAUSED)
        }
    }

    fun togglePauseSnakeGame() {
        if (_snakeGameState.value.status == GameStatus.PLAYING) {
            pauseSnakeGame()
        } else if (_snakeGameState.value.status == GameStatus.PAUSED) {
            startSnakeGame()
        }
    }

    fun restartSnakeGame() {
        snakeGameLoopJob?.cancel()
        gameTimerJob?.cancel()
        _snakeGameState.value = SnakeEngine.createInitialState(
            highScore = bestSnakeScore.value,
            wrapAround = _snakeGameState.value.wrapAroundWalls
        )
        startSnakeGame()
    }

    fun onSnakeDirectionInput(newDirection: Direction) {
        if (_snakeGameState.value.status == GameStatus.READY) {
            startSnakeGame()
        }
        _snakeGameState.update { SnakeEngine.queueDirection(it, newDirection) }
    }

    fun toggleWrapAroundWalls() {
        _snakeGameState.update { it.copy(wrapAroundWalls = !it.wrapAroundWalls) }
    }

    fun toggleVibration() {
        _vibrationEnabled.update { !it }
    }

    private fun runGameLoop() {
        snakeGameLoopJob?.cancel()
        snakeGameLoopJob = viewModelScope.launch {
            while (isActive && _snakeGameState.value.status == GameStatus.PLAYING) {
                delay(_snakeGameState.value.speedMs)
                val tickResult = SnakeEngine.tick(_snakeGameState.value)
                _snakeGameState.value = tickResult.state

                if (tickResult.foodEaten != null) {
                    triggerHaptic(HapticType.FOOD)
                }

                if (tickResult.gameOver) {
                    gameTimerJob?.cancel()
                    triggerHaptic(HapticType.GAME_OVER)
                    // Persist game score in Room
                    val finalState = tickResult.state
                    repository.saveSnakeGameScore(
                        score = finalState.score,
                        snakeLength = finalState.length,
                        durationSeconds = finalState.gameDurationSeconds
                    )
                    break
                }
            }
        }
    }

    private fun runGameTimer() {
        gameTimerJob?.cancel()
        gameTimerJob = viewModelScope.launch {
            while (isActive && _snakeGameState.value.status == GameStatus.PLAYING) {
                delay(1000)
                _snakeGameState.update { it.copy(gameDurationSeconds = it.gameDurationSeconds + 1) }
            }
        }
    }

    // --- Haptics ---
    enum class HapticType {
        FOOD, GAME_OVER, SUCCESS
    }

    private fun triggerHaptic(type: HapticType) {
        if (!_vibrationEnabled.value) return
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    HapticType.FOOD -> vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                    HapticType.GAME_OVER -> vibrator.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 200), intArrayOf(0, 255, 0, 255), -1)
                    )
                    HapticType.SUCCESS -> vibrator.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 50, 30, 80), intArrayOf(0, 180, 0, 255), -1)
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {
            // Ignore if vibration fails
        }
    }

    override fun onCleared() {
        super.onCleared()
        snakeGameLoopJob?.cancel()
        gameTimerJob?.cancel()
        studyTimerJob?.cancel()
    }
}
