package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_progress")
data class ExerciseProgressEntity(
    @PrimaryKey
    val exerciseId: String,
    val isCompleted: Boolean = false,
    val attemptsCount: Int = 0,
    val savedCode: String = "",
    val completedAt: Long? = null
)

@Entity(tableName = "snake_high_scores")
data class SnakeHighScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Int,
    val snakeLength: Int,
    val durationSeconds: Int,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val currentStreak: Int = 1,
    val lastActiveDate: String = "",
    val totalStudySeconds: Long = 0,
    val totalXp: Int = 0
)
