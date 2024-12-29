package com.example.fight.game.entity

sealed class GameEvent {
    data object StartGame:GameEvent()
    data object ResumeGame:GameEvent()
    data object Fight:GameEvent()
    data class MovePlayer(val direction: Direction):GameEvent()
}