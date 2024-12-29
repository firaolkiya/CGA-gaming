package com.example.snake

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel:ViewModel() {

    private var _state = MutableStateFlow(GameScreenState())
    val state:StateFlow<GameScreenState> = _state.asStateFlow()
    val lastUpdated = mutableStateOf(true)



    fun setSize(size:Size){
        _state.value = _state.value.copy(
            boardSize = size
        )
    }

    @RequiresApi(35)
    fun onEvent(event:GameEvent){
        when(event){
            is GameEvent.ChangeDirection ->{
                if(!lastUpdated.value){
                    return
                }
                lastUpdated.value=false
                viewModelScope.launch {
                    when(event.direction){
                        ScreenDirection.LEFT,ScreenDirection.RIGHT -> {
                            if(_state.value.direction == ScreenDirection.UP||_state.value.direction == ScreenDirection.DOWN){
                                _state.value = _state.value.copy(direction = event.direction)
                                delay(_state.value.gameDelay)
                            }


                        }
                        ScreenDirection.UP ,ScreenDirection.DOWN -> {
                            if(_state.value.direction == ScreenDirection.LEFT||_state.value.direction == ScreenDirection.RIGHT){
                                _state.value = _state.value.copy(direction = event.direction)
                                delay(_state.value.gameDelay)
                            }

                        }
                    }
                    lastUpdated.value=true

                }
            }
            GameEvent.Pause -> _state.update { it.copy(gameState = GameState.Paused) }
            GameEvent.Resume -> onEvent(GameEvent.Start)

            GameEvent.Start -> {
                _state.value = _state.value.copy(
                    gameState = GameState.GameRunning
                )
                viewModelScope.launch {

                    while (_state.value.gameState is GameState.GameRunning){
                        delay(
                            when{
                                _state.value.snakeBody.size<5->120L
                                _state.value.snakeBody.size<10->110L
                                _state.value.snakeBody.size<20->100L
                                else -> {80L}
                            }
                        )
                        _state.value =  updateSnake(_state.value)
                    }
                }
            }

            GameEvent.Reset -> _state.value = GameScreenState()
        }

    }

    @RequiresApi(35)
    fun updateSnake(currentState:GameScreenState):GameScreenState{
        if(currentState.gameState is GameState.GameOver || currentState.gameState is GameState.Paused){
            return currentState
        }
        val size = currentState.boardSize
        val gridSize = 30
        val totalGridX = ( size.width/gridSize).toInt()
        val totalGridY = ( size.height/gridSize).toInt()



            val head= currentState.snakeBody.first()
            val newHead= when(currentState.direction){
                ScreenDirection.LEFT -> Position(head.x-gridSize,head.y)
                ScreenDirection.UP ->  Position(head.x,head.y-gridSize)
                ScreenDirection.RIGHT ->  Position(head.x+gridSize,head.y)
                ScreenDirection.DOWN ->  Position(head.x,head.y+gridSize)
            }

//                check if game over
//                we are sure that each head radius is 17
            val touchItSelf = newHead in currentState.snakeBody
            val outOfBound = head.x<15 ||head.x>=size.width-20 || head.y<15||head.y>size.height-15

            if (touchItSelf||outOfBound){

                return currentState.copy(
                    gameState = GameState.GameOver
                )
            }
            val newSnake = currentState.snakeBody.toMutableList()
            newSnake.add(0,newHead)

//                check if the snake is at the point
            if (newHead.x==currentState.pointPosition.x && newHead.y==currentState.pointPosition.y){
                val nextX = Random.nextInt(from = 0, until = totalGridX)*gridSize
                val nextY = Random.nextInt(from = 0, until = totalGridY)*gridSize
                return currentState.copy(
                    snakeBody =newSnake,
                    pointPosition = Position(nextX+15,nextY+15)
                )

            }
            else{
                newSnake.removeLast()
              return currentState.copy(
                    snakeBody = newSnake
                )

            }




            }




    }
