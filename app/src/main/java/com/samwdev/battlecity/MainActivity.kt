package com.samwdev.battlecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.TanksViewModel
import com.samwdev.battlecity.core.rememberGameState
import com.samwdev.battlecity.ui.components.BattleField
import com.samwdev.battlecity.ui.components.Controller
import com.samwdev.battlecity.ui.components.rememberBattleCityController
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val tanksViewModel = TanksViewModel()
            val gameState = rememberGameState(tanksViewModel)
            val scope = rememberCoroutineScope()
            val controllerState = rememberBattleCityController()

            Column(modifier = Modifier.fillMaxSize()) {
                BattleField(gameState = gameState, tanksViewModel = tanksViewModel, modifier = Modifier.fillMaxWidth())
                Controller(
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxWidth(),
                    onSteer = { offset ->
                        tanksViewModel.updateTank((5 * offset.x).roundToInt(), (5 * offset.y).roundToInt())
                        controllerState.setCurrentInput(offset = offset)
                    },
                    onFire = {  },
                )

                Text(text = "ctrler State: ${controllerState.direction}", color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BattleCityTheme {

    }
}