package com.samwdev.battlecity.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.StageConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ColorCurtain = Color(117, 117, 117)

@Composable
fun StageCurtain(
    stageConfigPrev: StageConfig?,
    stageConfigNext: StageConfig,
    modifier: Modifier = Modifier,
    curtainDone: suspend () -> Unit,
) {
    val curtainSlidingDelay = 200/2
    val curtainSlidingTime = 500 /2
    val curtainShutTime = 1000 / 2
    var currStageConfig by remember { mutableStateOf(stageConfigPrev) }
    var currentClosed by remember { mutableStateOf(false) }

    val curtainHeightPercentage by animateFloatAsState(
        targetValue = if (currentClosed) 1f else 0f,
        animationSpec = tween(curtainSlidingTime, easing = FastOutSlowInEasing)
    )

    val co = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        co.launch {
            delay(curtainSlidingDelay.toLong())
            currentClosed = true
            delay(curtainSlidingTime.toLong())
            currStageConfig = null
            delay(curtainShutTime.toLong())
            currentClosed = false
            delay(curtainSlidingTime.toLong())
            curtainDone()
        }
    }

    Grid(modifier = modifier
        .aspectRatio(1f)
        .fillMaxWidth()) {
        if (currStageConfig != null) {
            BattlefieldStatic(mapConfig = currStageConfig!!.map)
        }
        Box(modifier = Modifier
            .background(ColorCurtain)
            .fillMaxWidth()
            .fillMaxHeight(0.5f * curtainHeightPercentage)
            .align(Alignment.TopCenter)
        )
        Box(modifier = Modifier
            .background(ColorCurtain)
            .fillMaxWidth()
            .fillMaxHeight(0.5f * curtainHeightPercentage)
            .align(Alignment.BottomCenter)
        )
        if (curtainHeightPercentage > 0.97f) {
            PixelText(
                text = "STAGE ${stageConfigNext.name}",
                charHeight = 0.5f.grid2mpx,
                textColor = Color.Black,
                modifier = Modifier.align(Alignment.Center))
        }
    }
}