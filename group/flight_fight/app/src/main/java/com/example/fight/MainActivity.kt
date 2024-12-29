package com.example.fight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fight.game.GameScreen
import com.example.fight.game.GameViewModel
import com.example.fight.game.HomeScreen
import com.example.fight.game.entity.GameEvent
import com.example.fight.game.entity.Level
import com.example.fight.ui.theme.FightTheme

class MainActivity : ComponentActivity() {
    private lateinit var  viewModel:GameViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            FightTheme {
                Scaffold(
                    modifier = Modifier
                        .padding(bottom = 0.dp)
                        .fillMaxSize(),
                ) { innerPadding ->
                    innerPadding.calculateTopPadding()
                    val navHostController = rememberNavController()
                     viewModel = viewModel<GameViewModel>()
                    NavHost(navController = navHostController, startDestination = "home_screen") {
                        composable(route = "game_screen/{level}",
                            arguments = listOf(navArgument("level") { type = NavType.IntType })
                            ) {
                            val level = it.arguments?.getInt("level") ?: 1


                            viewModel.onEvent(GameEvent.StartGame)
                            GameScreen(viewModel,level= when (level) {
                                3 -> Level.High
                                2 -> Level.Medium
                                else -> Level.Low
                            }
                            )
                        }

                        composable("home_screen") {
                            HomeScreen(navController = navHostController)
                        }

                    }
                }
            }
        }

    }

}
