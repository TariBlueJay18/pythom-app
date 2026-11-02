package com.example

import com.example.data.ExerciseCatalog
import com.example.engine.PythonEngine
import com.example.game.Direction
import com.example.game.GameStatus
import com.example.game.Point
import com.example.game.SnakeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testExerciseCatalogNotEmpty() {
        val exercises = ExerciseCatalog.exercises
        assertTrue("Exercises list should not be empty", exercises.isNotEmpty())
        assertEquals(18, exercises.size)
        exercises.forEach { ex ->
            assertNotNull(ex.id)
            assertNotNull(ex.title)
            assertNotNull(ex.starterCode)
            assertTrue(ex.testCases.isNotEmpty())
        }
    }

    @Test
    fun testPythonEngineExecutesSumTwoNumbers() {
        val exercise = ExerciseCatalog.getExerciseById("beg_01_sum_two")
        assertNotNull(exercise)
        val code = """
            def sum_two_numbers(a, b):
                return a + b
        """.trimIndent()

        val result = PythonEngine.executeExercise(exercise!!, code)
        assertTrue("Execution should be successful", result.isSuccess)
        assertEquals(exercise.testCases.size, result.passedTests)
    }

    @Test
    fun testPythonEngineExecutesReverseString() {
        val exercise = ExerciseCatalog.getExerciseById("beg_03_reverse_string")
        assertNotNull(exercise)
        val code = """
            def reverse_string(s):
                return s[::-1]
        """.trimIndent()

        val result = PythonEngine.executeExercise(exercise!!, code)
        assertTrue("Reverse string should pass", result.isSuccess)
    }

    @Test
    fun testPythonEngineCatchesIncorrectOutput() {
        val exercise = ExerciseCatalog.getExerciseById("beg_01_sum_two")
        assertNotNull(exercise)
        val wrongCode = """
            def sum_two_numbers(a, b):
                return a - b
        """.trimIndent()

        val result = PythonEngine.executeExercise(exercise!!, wrongCode)
        assertFalse("Incorrect code should not pass", result.isSuccess)
    }

    @Test
    fun testSnakeInitialState() {
        val state = SnakeEngine.createInitialState(highScore = 150)
        assertEquals(GameStatus.READY, state.status)
        assertEquals(150, state.highScore)
        assertEquals(0, state.score)
        assertEquals(3, state.length)
        assertTrue(state.foods.isNotEmpty())
    }

    @Test
    fun testSnakeTickMovement() {
        var state = SnakeEngine.createInitialState().copy(status = GameStatus.PLAYING)
        val initialHead = state.snake.first()
        val tickResult = SnakeEngine.tick(state)

        assertFalse("Should not be game over on empty board move", tickResult.gameOver)
        assertEquals(initialHead.x, tickResult.state.snake.first().x)
        assertEquals(initialHead.y - 1, tickResult.state.snake.first().y)
        assertEquals(state.length, tickResult.state.length)
    }

    @Test
    fun testSnakePreventReverseDirection() {
        val state = SnakeEngine.createInitialState(highScore = 0).copy(
            direction = Direction.UP,
            pendingDirection = Direction.UP
        )
        val updated = SnakeEngine.queueDirection(state, Direction.DOWN)
        assertEquals(Direction.UP, updated.pendingDirection)
    }

    @Test
    fun testSnakeEatingFoodGrows() {
        val head = Point(10, 10)
        val state = SnakeEngine.createInitialState().copy(
            status = GameStatus.PLAYING,
            snake = listOf(head, Point(10, 11), Point(10, 12)),
            direction = Direction.UP,
            pendingDirection = Direction.UP,
            foods = listOf(com.example.game.FoodItem(Point(10, 9), com.example.game.FoodType.APPLE))
        )
        val tickResult = SnakeEngine.tick(state)
        assertEquals(Point(10, 9), tickResult.state.snake.first())
        assertEquals(4, tickResult.state.length)
        assertEquals(10, tickResult.state.score)
    }
}
