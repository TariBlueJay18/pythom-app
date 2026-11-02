package com.example.engine

import com.example.data.model.Exercise
import com.example.data.model.ExerciseTestCase
import com.example.data.model.ExecutionResult
import com.example.data.model.TestCaseResult

/**
 * Lightweight, safe Python execution & evaluation sandbox for Android.
 * Parses Python functions, handles control flow, loops, collections, math,
 * and string manipulation, capturing stdout and comparing outputs against test cases.
 */
object PythonEngine {

    fun executeExercise(
        exercise: Exercise,
        userCode: String
    ): ExecutionResult {
        val startTime = System.currentTimeMillis()
        val trimmedCode = userCode.trim()

        if (trimmedCode.isEmpty()) {
            return ExecutionResult(
                isSuccess = false,
                totalTests = exercise.testCases.size,
                passedTests = 0,
                results = exercise.testCases.map {
                    TestCaseResult(
                        testCase = it,
                        passed = false,
                        actualOutput = null,
                        errorMessage = "Code editor is empty. Please implement ${exercise.functionName}."
                    )
                },
                globalStdout = "",
                globalError = "IndentationError / EmptyCode: No code provided.",
                executionTimeMs = System.currentTimeMillis() - startTime
            )
        }

        val testResults = mutableListOf<TestCaseResult>()
        val globalStdoutBuilder = StringBuilder()
        var overallSuccess = true
        var fatalError: String? = null

        for (testCase in exercise.testCases) {
            val stdoutCapture = StringBuilder()
            try {
                val interpreter = SimplePythonInterpreter(stdoutCapture)
                interpreter.loadCode(trimmedCode)
                val actual = interpreter.callFunction(exercise.functionName, testCase.arguments)
                val passed = areOutputsEqual(actual, testCase.expected)

                if (!passed) {
                    overallSuccess = false
                }

                if (stdoutCapture.isNotEmpty()) {
                    globalStdoutBuilder.append("--- Output for ${testCase.label} ---\n")
                    globalStdoutBuilder.append(stdoutCapture.toString().trimEnd()).append("\n")
                }

                testResults.add(
                    TestCaseResult(
                        testCase = testCase,
                        passed = passed,
                        actualOutput = actual,
                        errorMessage = if (!passed) "Expected ${formatValue(testCase.expected)}, but got ${formatValue(actual)}" else null,
                        consoleLog = stdoutCapture.toString()
                    )
                )
            } catch (e: Exception) {
                overallSuccess = false
                fatalError = e.message ?: "Execution error"
                testResults.add(
                    TestCaseResult(
                        testCase = testCase,
                        passed = false,
                        actualOutput = null,
                        errorMessage = "${e.javaClass.simpleName}: ${e.message}",
                        consoleLog = stdoutCapture.toString()
                    )
                )
            }
        }

        val duration = System.currentTimeMillis() - startTime
        val passedCount = testResults.count { it.passed }

        return ExecutionResult(
            isSuccess = overallSuccess && passedCount == exercise.testCases.size,
            totalTests = exercise.testCases.size,
            passedTests = passedCount,
            results = testResults,
            globalStdout = globalStdoutBuilder.toString(),
            globalError = if (!overallSuccess && fatalError != null) fatalError else null,
            executionTimeMs = duration
        )
    }

    private fun areOutputsEqual(actual: Any?, expected: Any?): Boolean {
        if (actual == null && expected == null) return true
        if (actual == null || expected == null) return false

        // Normalize numbers (e.g. 5 vs 5.0, Long vs Int)
        if (actual is Number && expected is Number) {
            return actual.toDouble() == expected.toDouble()
        }

        // Normalize lists
        if (actual is List<*> && expected is List<*>) {
            if (actual.size != expected.size) return false
            for (i in actual.indices) {
                if (!areOutputsEqual(actual[i], expected[i])) return false
            }
            return true
        }

        // Normalize maps
        if (actual is Map<*, *> && expected is Map<*, *>) {
            if (actual.size != expected.size) return false
            for ((k, v) in expected) {
                if (!actual.containsKey(k) || !areOutputsEqual(actual[k], v)) return false
            }
            return true
        }

        return actual.toString().trim() == expected.toString().trim()
    }

    fun formatValue(v: Any?): String {
        return when (v) {
            null -> "None"
            is String -> "\"$v\""
            is Boolean -> if (v) "True" else "False"
            is List<*> -> "[${v.joinToString(", ") { formatValue(it) }}]"
            is Map<*, *> -> "{${v.entries.joinToString(", ") { "${formatValue(it.key)}: ${formatValue(it.value)}" }}}"
            else -> v.toString()
        }
    }
}
