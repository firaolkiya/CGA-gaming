package com.example.snake

import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.abs
import kotlin.math.log


@RequiresApi(35)
@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    val screenState = viewModel.state.collectAsState()
    var boardSize = Size(200f,200f)
    val context= LocalContext.current
    val gotFood = remember {

       MediaPlayer.create(context,R.raw.ate)
    }
    val gameOver = remember {
        MediaPlayer.create(context,R.raw.over)
    }

    LaunchedEffect (screenState.value.snakeBody.size){
        if (screenState.value.snakeBody.size>1){
            gotFood.start()
        }


    }
    LaunchedEffect (screenState.value.gameState){
        if(screenState.value.gameState == GameState.GameOver){
            gameOver.start()
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
            .background(Color(31, 91, 129, 255)),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.Transparent
            )


        ) {

            Text(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                text = "Score: ${screenState.value.snakeBody.size-1}", style = MaterialTheme.typography.headlineMedium
            )
        }
        
        Box (
            contentAlignment = Alignment.Center
        ){
            Canvas(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // Consume the gesture to prevent it from propagating
                            val (dragX, dragY) = dragAmount
                            if (abs(dragX) <= 2 && abs(dragY) <= 2) {
                                return@detectDragGestures
                            }
                            viewModel.onEvent(

                                event = GameEvent.ChangeDirection(
                                    when {
                                        // Swipe Right
                                        dragX > 2 && abs(dragX) > abs(dragY) -> {
                                            ScreenDirection.RIGHT
                                        }
                                        // Swipe Left
                                        dragX < -2 && abs(dragX) > abs(dragY) -> {
                                            ScreenDirection.LEFT
                                        }
                                        // Swipe Down
                                        dragY > 2 && abs(dragY) > abs(dragX) -> {
                                            ScreenDirection.DOWN
                                        }
                                        // Swipe Up
                                        else -> {
                                            ScreenDirection.UP
                                        }

                                    }
                                )
                            )

                        }
                    }

            ) {

                boardSize = Size(width = size.width, height = size.height)
                drawRect(
                    size = Size(size.width , boardSize.height),
                    color = Color(139, 195, 74, 255)
                )


                    drawSnake(screenState.value.snakeBody, gameState = screenState.value.gameState)
                    drawPoint(15f,screenState.value.pointPosition.x.toFloat(),screenState.value.pointPosition.y.toFloat())



            }
            if(screenState.value.gameState==GameState.GameOver){
                Text(text = "Game Over!", style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 40.sp
                ))
            }
        }
        
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ElevatedButton(
                modifier = Modifier
                    .padding(horizontal = 30.dp),

                enabled = (screenState.value.gameState !=GameState.GameRunning),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(65, 158, 170, 255),
                            contentColor = Color.White
                ),
                onClick = {
                    viewModel.setSize(boardSize)
                    viewModel.onEvent(
                        when(screenState.value.gameState){
                            is GameState.GameOver -> GameEvent.Reset
                            is GameState.GameRunning -> GameEvent.Resume
                            is GameState.Initial ->  GameEvent.Start
                            is GameState.Paused -> GameEvent.Resume
                        }

                    )
                }) {
                Text(text = when(screenState.value.gameState){
                    GameState.GameOver ->"Restart"
                    GameState.GameRunning -> "Start"
                    GameState.Initial -> "New Game"
                    GameState.Paused -> "Resume"
                })

            }

            ElevatedButton(
                modifier = Modifier
                    .padding(horizontal = 30.dp),

                enabled = screenState.value.gameState!=GameState.Initial && screenState.value.gameState!=GameState.Paused,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(65, 158, 170, 255),
                    contentColor = Color.White
                ),
                onClick = {
                   viewModel.onEvent(
                       when(screenState.value.gameState){
                           GameState.GameOver -> GameEvent.Reset
                           GameState.GameRunning -> GameEvent.Pause
                           GameState.Initial -> TODO()
                           GameState.Paused -> GameEvent.Reset
                       }
                   )
                }) {
                Text(text = when(screenState.value.gameState){
                GameState.GameOver ->"Reset"
                GameState.GameRunning -> "Pause"
                GameState.Initial -> "Pause"
                GameState.Paused -> "Reset"
            })

            }


        }

    }


}


fun DrawScope.drawHead(
    radius: Float,
    x: Float,
    y: Float,
) {

    drawCircle(brush = Brush.linearGradient(
        listOf(Color(255, 87, 34, 255),Color.Magenta)),
        radius = radius,
        center = Offset(x=x,y=y),

    )

}

fun DrawScope.drawBody(
    radius: Float,
    x: Float,
    y: Float,
) {

    drawCircle(brush = Brush.linearGradient(
        listOf(Color(255, 193, 7, 255),Color.Magenta)),
        radius = radius,
        center = Offset(x=x,y=y),

        )

}

fun DrawScope.drawPoint(
    radius: Float,
    x: Float,
    y: Float,
) {

    drawCircle(brush = Brush.linearGradient(
        listOf(Color(255, 12, 7, 255),Color.Magenta)),
        radius = radius,
        center = Offset(x=x,y=y),

        )

}



 fun DrawScope.drawSnake(snake:List<Position>,gameState: GameState){
     snake.forEachIndexed { index, position ->
         Log.d("pos", "drawSnake: $position")
         if(index==0){
             if(gameState!=GameState.GameOver) {
                 drawHead(15f, position.x.toFloat(), position.y.toFloat())
             }
         }
         else{
             drawBody(13f,position.x.toFloat(),position.y.toFloat())
         }
     }

 }
