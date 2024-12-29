package com.example.fight.game

import androidx.compose.ui.geometry.Size
import com.example.fight.game.entity.Direction
import com.example.fight.game.entity.GameSate
import com.example.fight.game.entity.Helicopter
import com.example.fight.game.entity.Level
import com.example.fight.game.entity.Position

data class GameScreenState (
    val pageSize:Size = Size(600f,600f),
    val live:Int=5,
    val killed:Int=0,
    val level:Level=Level.Low,
    val helicopters:List<Helicopter> = listOf(Helicopter(Position(0,200), direction = Direction.RIGHT)),
    val playerPosition: Position = Position(500,500),
    val rocketsPosition: List<Position> = listOf(),
    val bombs:List<Position> = listOf(),
    val gameSate: GameSate =GameSate.Initial,
    val playerDirection:Direction = Direction.RIGHT
)







