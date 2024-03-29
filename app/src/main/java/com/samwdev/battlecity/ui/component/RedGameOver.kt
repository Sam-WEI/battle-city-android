package com.samwdev.battlecity.ui.component

import androidx.compose.animation.core.LinearEasing
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
import com.samwdev.battlecity.ui.screen.PixelText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RedGameOver(
    modifier: Modifier = Modifier,
    onAnimationComplete: suspend () -> Unit,
) {
    val slidingDuration = 2000
    var up by remember { mutableStateOf(false) }

    val gameOverLayerHeightPercentage by animateFloatAsState(
        targetValue = if (up) 1f else 0f,
        animationSpec = tween(slidingDuration, easing = LinearEasing),
        label = "game over layer"
    )

    val co = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        co.launch {
            up = true
            delay(slidingDuration.toLong())
            delay(1000)
            onAnimationComplete()
        }
    }

    Grid(modifier = modifier
        .aspectRatio(1f)
        .fillMaxWidth()) {

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(Color.Transparent)
            .fillMaxWidth()
            .fillMaxHeight(gameOverLayerHeightPercentage),
            contentAlignment = Alignment.Center,
        ) {
            PixelText(text = "GAME\nOVER", charHeight = 0.5f.cell2mpx, textColor = Color.Red)
        }
    }
}