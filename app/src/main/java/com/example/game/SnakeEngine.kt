package com.example.game

import kotlin.random.Random

object SnakeEngine {

    fun createInitialState(
        highScore: Int = 0,
        gridWidth: Int = 20,
        gridHeight: Int = 22,
        wrapAround: Boolean = false
    ): SnakeGameState {
        val startX = gridWidth / 2
        val startY = gridHeight / 2
        val initialSnake = listOf(
            Point(startX, startY),
            Point(startX, startY + 1),
            Point(startX, startY + 2)
        )
        val initialFood = generateFood(initialSnake, emptyList(), gridWidth, gridHeight, FoodType.APPLE)

        return SnakeGameState(
            snake = initialSnake,
            direction = Direction.UP,
            pendingDirection = Direction.UP,
            foods = listOfNotNull(initialFood),
            score = 0,
            highScore = highScore,
            foodEatenCount = 0,
            status = GameStatus.READY,
            speedMs = 180L,
            gridWidth = gridWidth,
            gridHeight = gridHeight,
            gameDurationSeconds = 0,
            isNewHighScore = false,
            wrapAroundWalls = wrapAround
        )
    }

    fun queueDirection(currentState: SnakeGameState, newDirection: Direction): SnakeGameState {
        // Prevent 180-degree reverse into self
        if (newDirection.isOpposite(currentState.direction)) {
            return currentState
        }
        return currentState.copy(pendingDirection = newDirection)
    }

    data class TickResult(
        val state: SnakeGameState,
        val foodEaten: FoodItem?,
        val gameOver: Boolean
    )

    fun tick(currentState: SnakeGameState): TickResult {
        if (currentState.status != GameStatus.PLAYING) {
            return TickResult(currentState, null, false)
        }

        val dir = currentState.pendingDirection
        val head = currentState.snake.first()
        var newX = head.x + dir.dx
        var newY = head.y + dir.dy

        // Handle walls
        if (currentState.wrapAroundWalls) {
            newX = (newX + currentState.gridWidth) % currentState.gridWidth
            newY = (newY + currentState.gridHeight) % currentState.gridHeight
        } else {
            if (newX < 0 || newX >= currentState.gridWidth || newY < 0 || newY >= currentState.gridHeight) {
                // Wall collision!
                return TickResult(
                    currentState.copy(status = GameStatus.GAME_OVER),
                    null,
                    true
                )
            }
        }

        val newHead = Point(newX, newY)

        // Check self-collision (excluding current tail since it will move unless eating)
        val bodyToCheck = currentState.snake.dropLast(1)
        if (bodyToCheck.contains(newHead)) {
            return TickResult(
                currentState.copy(status = GameStatus.GAME_OVER),
                null,
                true
            )
        }

        // Check food collision
        val eatenFood = currentState.foods.find { it.position == newHead }
        val newFoods = currentState.foods.filter { it.position != newHead }.toMutableList()

        val newSnake = mutableListOf(newHead)
        newSnake.addAll(currentState.snake)

        var newScore = currentState.score
        var newEatenCount = currentState.foodEatenCount

        if (eatenFood != null) {
            newScore += eatenFood.type.points
            newEatenCount++

            // If growth is 1, we don't drop the tail (net +1)
            // If growth is 2 (golden bug), we can keep duplicate or append extra
            if (eatenFood.type.growth > 1) {
                newSnake.add(currentState.snake.last())
            }

            // Spawn new apple if none left
            val hasApple = newFoods.any { it.type == FoodType.APPLE }
            if (!hasApple) {
                val newApple = generateFood(newSnake, newFoods, currentState.gridWidth, currentState.gridHeight, FoodType.APPLE)
                if (newApple != null) newFoods.add(newApple)
            }

            // Chance to spawn special bonus food (golden bug or star) every 4-5 eats
            if (newEatenCount % 4 == 0 && newFoods.size < 3) {
                val bonusType = if (Random.nextBoolean()) FoodType.GOLDEN_BUG else FoodType.BONUS_STAR
                val bonusFood = generateFood(newSnake, newFoods, currentState.gridWidth, currentState.gridHeight, bonusType)
                if (bonusFood != null) newFoods.add(bonusFood)
            }
        } else {
            // Normal move: drop tail
            newSnake.removeAt(newSnake.size - 1)
        }

        // Speed ramp up: starts at 180ms, decreases by 3ms per eaten food down to 80ms
        val newSpeed = (180L - (newEatenCount * 3L)).coerceAtLeast(80L)
        val isNewRecord = newScore > currentState.highScore

        val nextState = currentState.copy(
            snake = newSnake,
            direction = dir,
            foods = newFoods,
            score = newScore,
            highScore = if (isNewRecord) newScore else currentState.highScore,
            foodEatenCount = newEatenCount,
            speedMs = newSpeed,
            isNewHighScore = isNewRecord
        )

        return TickResult(nextState, eatenFood, false)
    }

    private fun generateFood(
        snake: List<Point>,
        existingFoods: List<FoodItem>,
        width: Int,
        height: Int,
        type: FoodType
    ): FoodItem? {
        val occupied = (snake + existingFoods.map { it.position }).toSet()
        val emptySpots = mutableListOf<Point>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                val p = Point(x, y)
                if (p !in occupied) {
                    emptySpots.add(p)
                }
            }
        }

        if (emptySpots.isEmpty()) return null
        val chosen = emptySpots[Random.nextInt(emptySpots.size)]
        return FoodItem(position = chosen, type = type)
    }
}
