package com.example.data.repository

import com.example.data.ExerciseCatalog
import com.example.data.local.dao.PySnakeDao
import com.example.data.local.entity.ExerciseProgressEntity
import com.example.data.local.entity.SnakeHighScoreEntity
import com.example.data.local.entity.UserStatsEntity
import com.example.data.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PySnakeRepository(private val dao: PySnakeDao) {

    val allExercises: List<Exercise> = ExerciseCatalog.exercises

    val allProgress: Flow<List<ExerciseProgressEntity>> = dao.getAllProgress()

    val topSnakeScores: Flow<List<SnakeHighScoreEntity>> = dao.getTopSnakeHighScores()

    val bestSnakeScore: Flow<Int> = dao.getBestSnakeScore().map { it ?: 0 }

    val userStats: Flow<UserStatsEntity> = dao.observeUserStats().map { stats ->
        stats ?: UserStatsEntity(id = 1, currentStreak = 1, lastActiveDate = getTodayDateString())
    }

    suspend fun getExerciseProgress(exerciseId: String): ExerciseProgressEntity? {
        return dao.getProgressForExercise(exerciseId)
    }

    suspend fun saveExerciseSolution(
        exerciseId: String,
        code: String,
        passed: Boolean,
        xpReward: Int
    ) {
        val existing = dao.getProgressForExercise(exerciseId)
        val wasAlreadyCompleted = existing?.isCompleted == true
        val newProgress = ExerciseProgressEntity(
            exerciseId = exerciseId,
            isCompleted = wasAlreadyCompleted || passed,
            attemptsCount = (existing?.attemptsCount ?: 0) + 1,
            savedCode = code,
            completedAt = if (passed) (existing?.completedAt ?: System.currentTimeMillis()) else existing?.completedAt
        )
        dao.saveProgress(newProgress)

        // Award XP if completed for the first time
        if (passed && !wasAlreadyCompleted) {
            val stats = dao.getUserStats() ?: UserStatsEntity(id = 1, currentStreak = 1, lastActiveDate = getTodayDateString())
            dao.saveUserStats(
                stats.copy(
                    totalXp = stats.totalXp + xpReward,
                    currentStreak = calculateStreak(stats.lastActiveDate, stats.currentStreak),
                    lastActiveDate = getTodayDateString()
                )
            )
        }
    }

    suspend fun saveSnakeGameScore(score: Int, snakeLength: Int, durationSeconds: Int): Long {
        val entity = SnakeHighScoreEntity(
            score = score,
            snakeLength = snakeLength,
            durationSeconds = durationSeconds,
            playedAt = System.currentTimeMillis()
        )
        val insertId = dao.insertSnakeScore(entity)

        // Also add small XP for playing games (10% of score)
        if (score > 0) {
            val stats = dao.getUserStats() ?: UserStatsEntity(id = 1, currentStreak = 1, lastActiveDate = getTodayDateString())
            val bonusXp = (score / 10).coerceAtLeast(5)
            dao.saveUserStats(
                stats.copy(
                    totalXp = stats.totalXp + bonusXp,
                    lastActiveDate = getTodayDateString()
                )
            )
        }
        return insertId
    }

    suspend fun addStudySeconds(seconds: Long) {
        val stats = dao.getUserStats() ?: UserStatsEntity(id = 1, currentStreak = 1, lastActiveDate = getTodayDateString())
        dao.saveUserStats(
            stats.copy(
                totalStudySeconds = stats.totalStudySeconds + seconds,
                currentStreak = calculateStreak(stats.lastActiveDate, stats.currentStreak),
                lastActiveDate = getTodayDateString()
            )
        )
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun calculateStreak(lastDate: String, currentStreak: Int): Int {
        val today = getTodayDateString()
        if (lastDate == today) return currentStreak.coerceAtLeast(1)
        if (lastDate.isEmpty()) return 1

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            val last = sdf.parse(lastDate)?.time ?: return 1
            val now = sdf.parse(today)?.time ?: return 1
            val diffDays = (now - last) / (1000 * 60 * 60 * 24)
            if (diffDays == 1L) currentStreak + 1 else if (diffDays > 1L) 1 else currentStreak
        } catch (e: Exception) {
            1
        }
    }
}
