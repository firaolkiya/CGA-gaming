package com.example.snake

import androidx.compose.ui.geometry.Size


data class GameScreenState(

    val boardSize:Size=Size(400f,400f),
    val gameState:GameState= GameState.Initial,
    val lastUpdated:Long=120L,
    val gameDelay:Long=120L,
    val pointPosition:Position = Position(225,165),
    val snakeBody:List<Position> = listOf(Position(105,135)),
    val direction:ScreenDirection = ScreenDirection.RIGHT
)

sealed class GameState{
    data object GameOver:GameState()
    data object Paused:GameState()
     data object GameRunning:GameState()
    data object Initial:GameState()
}

data class Position(
    val x:Int,
    val y:Int
)

enum class ScreenDirection{
    LEFT,
    UP,
    RIGHT,
    DOWN
}