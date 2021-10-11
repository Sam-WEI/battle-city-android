package com.samwdev.battlecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.samwdev.battlecity.core.BulletHandler
import com.samwdev.battlecity.core.Ticker
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BattleCityTheme {
                Surface(color = MaterialTheme.colors.background) {
                    BattleScene()
                }
            }
        }
    }
}

@Composable
fun BattleScene() {
    val ticker = remember {
        Ticker().also { it.start() }
    }
    val bulletHandler = remember {
        BulletHandler(ticker).also { it.start() }
    }
    val text = remember { mutableStateOf(0L) }

    LaunchedEffect(key1 = "ticker") {
        ticker.flow.collect { delta ->
            text.value = delta
        }
    }

    Text(text = "Hello! ${text.value}")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BattleCityTheme {
        BattleScene()
    }
}