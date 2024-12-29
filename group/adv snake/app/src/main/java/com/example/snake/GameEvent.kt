package com.example.snake

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

sealed class GameEvent {
    data object Start:GameEvent()
    data object Pause:GameEvent()
    data object Resume:GameEvent()
    data object Reset:GameEvent()
    data class ChangeDirection(val direction: ScreenDirection):GameEvent()
}