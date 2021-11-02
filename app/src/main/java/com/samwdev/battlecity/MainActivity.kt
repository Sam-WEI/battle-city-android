package com.samwdev.battlecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.rememberGameState
import com.samwdev.battlecity.ui.components.BattleField
import com.samwdev.battlecity.ui.components.Controller
import com.samwdev.battlecity.ui.components.rememberControllerState
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import com.samwdev.battlecity.utils.MapParser

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            val gameState = rememberGameState(stageConfigJson = MapParser.parseJson(1))

            Column(modifier = Modifier.fillMaxSize()) {
                BattleField(gameState = gameState, modifier = Modifier.fillMaxWidth())
                Controller(
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxWidth(),
                    controllerState = gameState.controllerState,
                )

                Text(text = "direction: ${gameState.controllerState.direction}", color = Color.Gray, fontSize = 30.sp)
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