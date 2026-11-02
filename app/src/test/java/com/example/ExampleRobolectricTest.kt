package com.example

import com.example.data.ExerciseCatalog
import com.example.data.model.ExerciseDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseCatalogTest {

  @Test
  fun testExerciseCategoriesAndDifficulty() {
    val beginnerList = ExerciseCatalog.exercises.filter { it.difficulty == ExerciseDifficulty.BEGINNER }
    val intermediateList = ExerciseCatalog.exercises.filter { it.difficulty == ExerciseDifficulty.INTERMEDIATE }
    val advancedList = ExerciseCatalog.exercises.filter { it.difficulty == ExerciseDifficulty.ADVANCED }

    assertTrue("Beginner exercises should exist", beginnerList.isNotEmpty())
    assertTrue("Intermediate exercises should exist", intermediateList.isNotEmpty())
    assertTrue("Advanced exercises should exist", advancedList.isNotEmpty())
    assertEquals(18, beginnerList.size + intermediateList.size + advancedList.size)
  }
}
