package com.example.fight.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fight.R
import com.example.fight.game.entity.Direction
import com.example.fight.game.entity.GameEvent
import com.example.fight.game.entity.GameSate
import com.example.fight.game.entity.Helicopter
import com.example.fight.game.entity.Level
import com.example.fight.game.entity.Position

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    level: Level
) {
    viewModel.setLevel(level)
    val screenState: State<GameScreenState> = viewModel.state.collectAsState()
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.sky)
    val heartBitmap = ImageBitmap.imageResource(id = R.drawable.heart)
    val playerImage = ImageBitmap.imageResource(id = R.drawable.tank)
    val helicopterImage = ImageBitmap.imageResource(id = R.drawable.h1)
    val bombImage = ImageBitmap.imageResource(id = R.drawable.bomb)
    val rocketImage = ImageBitmap.imageResource(id = R.drawable.rocket2)

    val playerSize = Size(200f, 200f)
    val helicopterSize = Size(150f, 150f)
    val bombSize = Size(50f, 50f)
    val rocketSize = Size(80f, 80f)

    val scoreTextMeasure = rememberTextMeasurer()
    val levelTextMeasurer = rememberTextMeasurer()
    GameScreenView(
        screenState = screenState,
        imageBitmap = imageBitmap,
        heartBitmap = heartBitmap,
        helicopterImage = helicopterImage,
        playerImage = playerImage,
        bombImage = bombImage,
        rocketImage = rocketImage,
        playerSize = playerSize,
        helicopterSize = helicopterSize,
        bombSize = bombSize,
        rocketSize = rocketSize,
        viewModel = viewModel,
        level = level,
        scoreTextMeasure = scoreTextMeasure,
        levelTextMeasurer = levelTextMeasurer
    )

}

@Composable
fun GameScreenView(
    screenState: State<GameScreenState>,
    imageBitmap: ImageBitmap,
    heartBitmap: ImageBitmap,
    helicopterImage: ImageBitmap,
    playerImage: ImageBitmap,
    bombImage: ImageBitmap,
    rocketImage: ImageBitmap,
    playerSize: Size,
    helicopterSize: Size,
    bombSize: Size,
    rocketSize: Size,
    viewModel: GameViewModel,
    level: Level,
    scoreTextMeasure: TextMeasurer,
    levelTextMeasurer: TextMeasurer
) {
    viewModel.setLevel(level)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier
                .fillMaxHeight(0.85f)
                .fillMaxWidth()
                .pointerInput(Unit) {

                    detectDragGestures { _, dragAmount ->
                        when {
                            dragAmount.x > 2 -> {
                                viewModel.onEvent(GameEvent.MovePlayer(Direction.RIGHT))
                            }

                            dragAmount.x < -2 -> {
                                viewModel.onEvent(GameEvent.MovePlayer(Direction.LEFT))
                            }

                            else -> {
                                viewModel.onEvent(GameEvent.Fight)
                            }
                        }
                    }
                }
            ) {


                viewModel.setSize(size)
                drawImage(
                    image = imageBitmap
                )

                drawScreen(size)


                drawImage(
                    dstSize = IntSize(playerSize.width.toInt(), playerSize.height.toInt()),
                    image = playerImage,
                    dstOffset = IntOffset(
                        screenState.value.playerPosition.x,
                        screenState.value.playerPosition.y
                    ),
                )

                drawHelicopter(
                    helicopterImage = helicopterImage,
                    helicopterSize = helicopterSize,
                    helicopters = screenState.value.helicopters
                )

                drawLive(
                    heartBitmap,
                    screenState.value.live
                )

                drawText(
                    scoreTextMeasure,
                    "Score: ${screenState.value.killed}",
                    topLeft = Offset(80f, 80f),
                    style = TextStyle(color = Color.Yellow, fontSize = 25.sp)
                )

                drawText(
                    levelTextMeasurer,
                    when (screenState.value.level) {
                        Level.Low -> "Level-I"
                        Level.Medium -> "Level-II"
                        Level.High -> "Level-II"
                    },
                    topLeft = Offset(size.width - 200, 80f),
                    style = TextStyle(color = Color.Yellow, fontSize = 25.sp)
                )





                drawElement(
                    imageBitmap = bombImage,
                    elementSize = bombSize,
                    positions = screenState.value.bombs
                )
                drawElement(
                    imageBitmap = rocketImage,
                    elementSize = rocketSize,
                    positions = screenState.value.rocketsPosition
                )



            }

            if (screenState.value.gameSate == GameSate.GameOver) {
                Text(text = "GameOver", style = MaterialTheme.typography.headlineLarge)
            }

        }
        Row(
            modifier = Modifier
                .background(Color(0, 150, 136, 255))
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceAround
        ) {

            CustomIconButton(image = Icons.Default.ArrowBack) {
                viewModel.onEvent(GameEvent.MovePlayer(Direction.LEFT))
            }
            CustomIconButton(image = if (screenState.value.gameSate != GameSate.GameOver) Icons.Default.PlayArrow else Icons.Default.Refresh) {
                when (screenState.value.gameSate) {
                    GameSate.GameOver -> viewModel.onEvent(GameEvent.ResumeGame)
                    GameSate.Initial -> TODO()
                    GameSate.Paused -> TODO()
                    GameSate.Playing -> viewModel.onEvent(GameEvent.Fight)
                }
            }

            CustomIconButton(image = Icons.Default.ArrowForward) {
                viewModel.onEvent(GameEvent.MovePlayer(Direction.RIGHT))
            }

        }
    }
}




fun DrawScope.drawElement(
    elementSize: Size,
    imageBitmap: ImageBitmap,
    positions: List<Position>
) {
    positions.forEach {
        drawImage(
            dstSize = IntSize(elementSize.width.toInt(), elementSize.width.toInt()),
            image = imageBitmap,
            dstOffset = IntOffset(it.x, it.y),
        )
    }
}

fun DrawScope.drawScreen(size: Size) {
    drawRect(
        topLeft = Offset(0f, size.height - size.height / 4),
        brush = Brush.verticalGradient(
            startY = size.height - size.height / 4,
            colors = listOf(
                Color(50, 141, 87, 255), Color(30, 77, 46, 255), Color(
                    21,
                    43,
                    28,
                    255
                ), Color(10, 24, 18, 255)
            )
        ),
        size = Size(size.width, size.height / 4)
    )
}

fun DrawScope.drawLive(
    heartBitmap: ImageBitmap,
    live: Int
) {
    for (i in 1..live) {
        drawImage(
            dstSize = IntSize(40, 40),
            image = heartBitmap,
            dstOffset = IntOffset(size.width.toInt() / 3 + i * 40, 80),
        )

    }
}


@Composable
fun CustomIconButton(
    image: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(255, 152, 0, 255)
        ),
        shape = CircleShape,
        onClick = {
            onClick()
        }
    ) {
        Icon(

            modifier = Modifier.size(50.dp),
            imageVector = image, contentDescription = "left"
        )
    }
}


fun DrawScope.drawHelicopter(
    helicopterImage: ImageBitmap,
    helicopterSize: Size,
    helicopters: List<Helicopter>
) {

    helicopters.forEach {
        drawImage(
            dstSize = IntSize(
                helicopterSize.width.toInt(),
                helicopterSize.width.toInt()
            ),
            image = helicopterImage,
            dstOffset = IntOffset(it.position.x, it.position.y),
        )
    }
}