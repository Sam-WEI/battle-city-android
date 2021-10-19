package com.samwdev.battlecity

import android.os.Bundle
import android.view.Choreographer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiFrameClock
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.components.GameScene
import com.samwdev.battlecity.components.JoyStick
import com.samwdev.battlecity.components.swipeToDismiss
import com.samwdev.battlecity.core.BulletHandler
import com.samwdev.battlecity.core.Ticker
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import com.samwdev.battlecity.utils.logE
import com.samwdev.battlecity.utils.logI
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                JoyStick(modifier = Modifier.align(Alignment.BottomStart)) {
                    logI("  callback $it")
                }
//                Box(modifier = Modifier
//                    .height(80.dp)
//                    .fillMaxWidth()
//                    .swipeToDismiss { }
//                    .background(Color.Blue)) {
//                    Text("This is a button")
//                }
            }
        }
    }
}

@Composable
fun BattleScene() {

    val ticker = remember {
        Ticker().also { /*it.start()*/ }
    }
    val bulletHandler = remember {
        BulletHandler(ticker).also { /*it.start()*/ }
    }
    val text = remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
//        ticker.flow.collect { delta ->
//            text.value = delta
//        }

    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = scope) {
        launch(AndroidUiFrameClock(Choreographer.getInstance())) {
            withFrameMillis {
                logE("withFrameMillis: $it")
            }
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