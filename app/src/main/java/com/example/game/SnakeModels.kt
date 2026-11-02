package com.example.game

data class Point(val x: Int, val y: Int)

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    fun isOpposite(other: Direction): Boolean {
        return (dx + other.dx == 0) && (dy + other.dy == 0)
    }
}

enum class FoodType(val points: Int, val growth: Int, val emoji: String) {
    APPLE(10, 1, "🍎"),
    GOLDEN_BUG(30, 2, "🐛"),
    BONUS_STAR(50, 1, "⭐")
}

data class FoodItem(
    val position: Point,
    val type: FoodType,
    val ticksRemaining: Int = 100 // for temporary bonus items
)

enum class GameStatus {
    READY,
    PLAYING,
    PAUSED,
    GAME_OVER
}

data class SnakeGameState(
    val snake: List<Point> = listOf(Point(10, 12), Point(10, 13), Point(10, 14)),
    val direction: Direction = Direction.UP,
    val pendingDirection: Direction = Direction.UP,
    val foods: List<FoodItem> = listOf(FoodItem(Point(10, 6), FoodType.APPLE)),
    val score: Int = 0,
    val highScore: Int = 0,
    val foodEatenCount: Int = 0,
    val status: GameStatus = GameStatus.READY,
    val speedMs: Long = 180L,
    val gridWidth: Int = 20,
    val gridHeight: Int = 22,
    val gameDurationSeconds: Int = 0,
    val isNewHighScore: Boolean = false,
    val wrapAroundWalls: Boolean = false
) {
    val length: Int get() = snake.size
}
