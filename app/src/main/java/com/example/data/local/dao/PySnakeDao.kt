package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ExerciseProgressEntity
import com.example.data.local.entity.SnakeHighScoreEntity
import com.example.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PySnakeDao {

    // Exercise Progress
    @Query("SELECT * FROM exercise_progress")
    fun getAllProgress(): Flow<List<ExerciseProgressEntity>>

    @Query("SELECT * FROM exercise_progress WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getProgressForExercise(exerciseId: String): ExerciseProgressEntity?

    @Query("SELECT * FROM exercise_progress WHERE exerciseId = :exerciseId LIMIT 1")
    fun observeProgressForExercise(exerciseId: String): Flow<ExerciseProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ExerciseProgressEntity)

    // Snake High Scores
    @Query("SELECT * FROM snake_high_scores ORDER BY score DESC, playedAt DESC LIMIT 20")
    fun getTopSnakeHighScores(): Flow<List<SnakeHighScoreEntity>>

    @Query("SELECT MAX(score) FROM snake_high_scores")
    fun getBestSnakeScore(): Flow<Int?>

    @Insert
    suspend fun insertSnakeScore(score: SnakeHighScoreEntity): Long

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun observeUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStats(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserStats(stats: UserStatsEntity)
}
