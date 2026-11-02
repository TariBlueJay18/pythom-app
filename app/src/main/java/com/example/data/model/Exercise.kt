package com.example.data.model

enum class ExerciseDifficulty(val label: String, val xpReward: Int) {
    BEGINNER("Beginner", 50),
    INTERMEDIATE("Intermediate", 100),
    ADVANCED("Advanced", 175)
}

data class ExerciseTestCase(
    val arguments: List<Any?>,
    val expected: Any?,
    val label: String,
    val isHidden: Boolean = false
)

data class Exercise(
    val id: String,
    val title: String,
    val difficulty: ExerciseDifficulty,
    val category: String,
    val description: String,
    val functionName: String,
    val starterCode: String,
    val solutionHint: String,
    val testCases: List<ExerciseTestCase>
)

data class TestCaseResult(
    val testCase: ExerciseTestCase,
    val passed: Boolean,
    val actualOutput: Any?,
    val errorMessage: String? = null,
    val consoleLog: String = ""
)

data class ExecutionResult(
    val isSuccess: Boolean,
    val totalTests: Int,
    val passedTests: Int,
    val results: List<TestCaseResult>,
    val globalStdout: String,
    val globalError: String? = null,
    val executionTimeMs: Long = 0
)
