package com.samwdev.battlecity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.samwdev.battlecity.core.Ticker
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BattleCityTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf(0L) }
    DisposableEffect(key1 = "") {
        onDispose {

        }
    }

    LaunchedEffect(key1 = "aaa") {
        val tick = Ticker()
        tick.start()
        tick.flow.collect { delta ->
            text.value = delta
        }
    }

    Text(text = "Hello $name! ${text.value}")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BattleCityTheme {
        Greeting("Android")
    }
}