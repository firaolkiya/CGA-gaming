package com.example.fight.game.entity

sealed class GameSate{
    data object Playing:GameSate()
    data object Paused:GameSate()
    data object GameOver:GameSate()
    data object Initial:GameSate()
}