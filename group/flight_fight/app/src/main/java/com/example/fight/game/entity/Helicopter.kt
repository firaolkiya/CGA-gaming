package com.example.fight.game.entity

import com.example.fight.R
import kotlin.random.Random

data class Helicopter(
    val position: Position,
    val direction: Direction,
    val lastExplode:Long = 1,
    val image:Int = R.drawable.h1
)