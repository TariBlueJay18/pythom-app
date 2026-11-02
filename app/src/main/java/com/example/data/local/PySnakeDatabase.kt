package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.PySnakeDao
import com.example.data.local.entity.ExerciseProgressEntity
import com.example.data.local.entity.SnakeHighScoreEntity
import com.example.data.local.entity.UserStatsEntity

@Database(
    entities = [
        ExerciseProgressEntity::class,
        SnakeHighScoreEntity::class,
        UserStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PySnakeDatabase : RoomDatabase() {
    abstract fun pySnakeDao(): PySnakeDao

    companion object {
        @Volatile
        private var INSTANCE: PySnakeDatabase? = null

        fun getDatabase(context: Context): PySnakeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PySnakeDatabase::class.java,
                    "pysnake_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
