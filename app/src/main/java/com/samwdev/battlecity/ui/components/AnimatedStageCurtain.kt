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
import com.samwdev.battlecity.core.cell2mpx
import com.samwdev.battlecity.entity.StageConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ColorCurtain = Color(117, 117, 117)

@Composable
fun AnimatedStageCurtain(
    stageConfigPrev: StageConfig?,
    stageConfigNext: StageConfig,
    modifier: Modifier = Modifier,
    onAnimationComplete: suspend () -> Unit,
) {
    val curtainSlidingDelay = 200L
    val curtainSlidingTime = 500L
    val curtainShutTime = 200L
    var currStageConfig by remember { mutableStateOf(stageConfigPrev) }
    var currentClosed by remember { mutableStateOf(false) }

    val curtainHeightPercentage by animateFloatAsState(
        targetValue = if (currentClosed) 1f else 0f,
        animationSpec = tween(curtainSlidingTime.toInt(), easing = FastOutSlowInEasing),
        label = "stage curtain"
    )

    val co = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        co.launch {
            delay(curtainSlidingDelay)
            currentClosed = true
            delay(curtainSlidingTime)
            currStageConfig = null
            delay(curtainShutTime)
            currentClosed = false
            delay(curtainSlidingTime)
            onAnimationComplete()
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
                charHeight = 0.5f.cell2mpx,
                textColor = Color.Black,
                modifier = Modifier.align(Alignment.Center))
        }
    }
}