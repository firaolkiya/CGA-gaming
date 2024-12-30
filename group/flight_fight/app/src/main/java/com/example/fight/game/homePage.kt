package com.example.fight.game

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fight.R

@Composable
fun HomeScreen(
    navController:NavController
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp),
            painter = painterResource(id = R.drawable.robot),
            contentDescription = "" )
        Spacer(modifier = Modifier.height(20.dp))

        Row (
            modifier = Modifier.fillMaxWidth(0.8f),

        ){
            Text(text = "Choose Game Level", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(20.dp))
        CustomButton(onClick = {
            val arg=1
            navController.navigate("game_screen/$arg")
        }, text = "LOW")
        Spacer(modifier = Modifier.height(10.dp))
        CustomButton(onClick = {
            val arg=2
            navController.navigate("game_screen/$arg")

        }, text = "Medium")
        Spacer(modifier = Modifier.height(10.dp))
        CustomButton(onClick = {
            val arg=3
            navController.navigate("game_screen/$arg")
        }, text = "High")
        Spacer(modifier = Modifier.height(20.dp))
        ElevatedButton(
            modifier = Modifier.fillMaxWidth(0.4f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = { val arg=1
            navController.navigate("game_screen/$arg")
            }) {
            Text(text = " Start Game")
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    )
@Composable
fun HomeView(){
    HomeScreen(navController = NavController(LocalContext.current))
}

@Composable
fun CustomButton(onClick:()->Unit,text:String){
    ElevatedButton(
        modifier = Modifier
            .fillMaxWidth(0.6f),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = {
            onClick()
        }) {
        Text(text = text)
    }
}