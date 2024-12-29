package com.example.fight.game

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fight.game.entity.Direction
import com.example.fight.game.entity.GameEvent
import com.example.fight.game.entity.GameSate
import com.example.fight.game.entity.Helicopter
import com.example.fight.game.entity.Level
import com.example.fight.game.entity.Position
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.sort
import kotlin.random.Random

class GameViewModel:ViewModel(){

    private val _state= MutableStateFlow(GameScreenState())
    val state:StateFlow<GameScreenState> = _state.asStateFlow()
    private val setPlayer = mutableStateOf(false)

    private var gameJob:Job?=null

    fun setLevel(level:Level){
        _state.value = _state.value.copy(level=level)
    }

    fun setSize(size:Size){
        if (setPlayer.value){
            return
        }
        _state.value = _state.value.copy(
            playerPosition = Position(size.width.toInt()/2,size.height.toInt()-400),
            pageSize = size
        )
        setPlayer.value=true
    }

    fun onEvent(event:GameEvent){
        when(event){
            is GameEvent.Fight -> {
                if (_state.value.gameSate != GameSate.Playing) {
                    return
                }
                viewModelScope.launch {
                    val position = _state.value.playerPosition
                    _state.update {
                        val rockets = it.rocketsPosition.toMutableList()
                        rockets.add(Position(position.x + 150, position.y))
                        it.copy(rocketsPosition = rockets)
                    }
                }
            }
            is GameEvent.MovePlayer -> {
                if(_state.value.gameSate!=GameSate.Playing){
                    return
                }
                viewModelScope.launch {
                    when (event.direction) {
                        Direction.UP, Direction.DOWN -> TODO()
                        Direction.RIGHT -> _state.update {
                            it.copy(
                                playerPosition = if (it.playerPosition.x < it.pageSize.width) Position(
                                    it.playerPosition.x + 25,
                                    it.playerPosition.y
                                ) else it.playerPosition
                            )
                        }

                        Direction.LEFT -> _state.update {
                            it.copy(
                                playerPosition = if (it.playerPosition.x > 0) Position(
                                    it.playerPosition.x - 25,
                                    it.playerPosition.y
                                ) else it.playerPosition
                            )
                        }
                    }
                }
            }
            GameEvent.ResumeGame -> {
                resetGame()
                onEvent(GameEvent.StartGame)
            }
            GameEvent.StartGame -> {
                _state.value = _state.value.copy(gameSate = GameSate.Playing)
                gameJob = viewModelScope.launch {
                    val gameDelay = when{
                        _state.value.killed<2->100L
                        _state.value.killed<5->80L
                        _state.value.killed<8->60L
                        _state.value.killed<20->50L
                        else->40L

                    }

                    while (_state.value.gameSate==GameSate.Playing){
                        _state.value=updateGame(currentState=_state.value)
                        if (_state.value.live==0){
                            _state.value = _state.value.copy(gameSate = GameSate.GameOver)
                        }
                        delay(gameDelay)
                    }
                }
            }
        }
    }




    private fun updateGame(currentState: GameScreenState): GameScreenState {

        if(currentState.gameSate!=GameSate.Playing){
            return currentState
        }

        val newHelicopters = mutableListOf<Helicopter>()
        var newBombs = _state.value.bombs.toMutableList()
        var updatedRocket = _state.value.rocketsPosition.toMutableList()
        currentState.helicopters.forEach {

            var helicopter= it.copy(
                lastExplode = it.lastExplode-1,
                position = when(it.direction){
                    Direction.UP,Direction.DOWN -> it.position
                    Direction.LEFT -> Position(it.position.x-5,it.position.y)
                    Direction.RIGHT ->  Position(it.position.x+5,it.position.y)
                }
            )

            if(helicopter.lastExplode==0L){
                newBombs.add(helicopter.position)
                helicopter = helicopter.copy(lastExplode=when(currentState.level){
                    Level.Low -> 50L
                    Level.Medium -> 35L
                    Level.High -> 20L
                })
            }
            if(inBound(helicopter.position,helicopter.direction)){
                newHelicopters.add(helicopter)
            }
            else{
//                generate new helicopter position
                helicopter = helicopter.copy(
                    position = Position(
                        x =currentState.pageSize.width.toInt()-helicopter.position.x,
                        y=Random.nextInt(from = 100, until = 400)
                    )
                )
                newHelicopters.add(helicopter)
            }


        }

        updatedRocket.forEachIndexed { index, position ->

            updatedRocket[index]  = position.copy(
                x=position.x,
                y = when(currentState.level){
                    Level.Low -> position.y-30
                    Level.Medium -> position.y-45
                    Level.High -> position.y-60
                }
            )
        }

//        change
        val removed = isPlayerHit(updatedRocket,newHelicopters)


        for (i in removed[1].indices){
            newHelicopters[i] = newHelicopters[i].copy(
                direction = if(newHelicopters[i].direction==Direction.RIGHT) Direction.LEFT else Direction.RIGHT,
                position = Position(
                    x =if(newHelicopters[i].direction==Direction.RIGHT) currentState.pageSize.width.toInt()+100 else -200,
                    y=Random.nextInt(from = 0, until = 350)
                )
            )
        }

//        update bombs if exist
        for (i in newBombs.indices){
            newBombs[i] = newBombs[i].copy(y=newBombs[i].y+when(currentState.level){
                Level.Low -> 30
                Level.Medium -> 45
                Level.High -> 60
            })
        }

        newBombs = newBombs.filter { inBound(it,Direction.DOWN) }.toMutableList()
        updatedRocket = customFilter(removed[0],updatedRocket)
        val removedBombs = isPlayerKilled(_state.value.playerPosition,newBombs)
        if (removedBombs!=-1){
            newBombs.removeAt(removedBombs)
        }





    return currentState.copy(helicopters = newHelicopters,
        rocketsPosition = updatedRocket,
        bombs = newBombs,
        killed = currentState.killed+removed[0].size,
        live = if (removedBombs!=-1) currentState.live-1 else currentState.live
    )


    }

    private fun inBound(position: Position,direction: Direction):Boolean {
        val inX  = (position.x in 0.._state.value.pageSize.width.toInt()) || (position.x<0 && direction==Direction.RIGHT) || (position.x>_state.value.pageSize.width && direction==Direction.LEFT)
        val inY = position.y in 0 .. _state.value.pageSize.height.toInt()

        return inX && inY
    }


    private fun isPlayerHit(rockets: List<Position>, helicopters: List<Helicopter>): List<List<Int>> {
        val removeHelicopter = mutableSetOf<Int>()
        val removeRocket = mutableSetOf<Int>()

        rockets.forEachIndexed { rocketIndex, rocket ->
            helicopters.forEachIndexed { heliIndex, helicopter ->
                if (rocket.x - helicopter.position.x in -25..150 && rocket.y - helicopter.position.y in 0..150) {
                    removeHelicopter.add(heliIndex)
                    removeRocket.add(rocketIndex)
                }
            }
        }
        return listOf(removeRocket.toList(), removeHelicopter.toList())
    }



    private fun isPlayerKilled(player: Position,bombs: List<Position>):Int{
//        when know that helicopter size is 150
        bombs.forEachIndexed() { index,bomb->
            if(bomb.x-player.x in -48..200 && player.y-bomb.y <=0 ){
                return index
            }
        }
        return  -1
    }

    private fun customFilter(removed:List<Int>, origin:List<Position>): MutableList<Position> {
        val newList = mutableListOf<Position>()
        for (i in origin.indices){
            if (i !in removed || !inBound(origin[i],Direction.UP)){
                newList.add(origin[i])
            }
        }
        return newList

    }


    private fun resetGame() {
        gameJob?.cancel()
        _state.value = GameScreenState(
            gameSate = GameSate.Initial,
            pageSize = _state.value.pageSize,
            level = _state.value.level,
            playerPosition = _state.value.playerPosition,
            killed = 0,
            live = 5,
            helicopters = listOf(Helicopter(Position(0,200), direction = Direction.RIGHT)),
            rocketsPosition = emptyList(),
            bombs = emptyList()
        )
    }



}

