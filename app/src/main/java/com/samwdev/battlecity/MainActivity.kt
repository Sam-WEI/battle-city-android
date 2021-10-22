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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiFrameClock
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.components.*
import com.samwdev.battlecity.core.BulletHandler
import com.samwdev.battlecity.core.Ticker
import com.samwdev.battlecity.ui.components.BattleField
import com.samwdev.battlecity.ui.components.Controller
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
            Column(modifier = Modifier.fillMaxSize()) {
                BattleField(modifier = Modifier.fillMaxWidth())
                Controller(
                    modifier = Modifier.padding(30.dp).fillMaxWidth(),
                    onSteer = {  },
                    onFire = {  },
                )
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