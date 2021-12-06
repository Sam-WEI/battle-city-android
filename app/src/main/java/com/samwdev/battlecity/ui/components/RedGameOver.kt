package com.samwdev.battlecity.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.samwdev.battlecity.core.grid2mpx
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ColorCurtain = Color(117, 117, 117)

@Composable
fun RedGameOver(
    modifier: Modifier = Modifier,
    animComplete: suspend () -> Unit,
) {
    val slidingDuration = 2000
    var up by remember { mutableStateOf(false) }

    val curtainHeightPercentage by animateFloatAsState(
        targetValue = if (up) 1f else 0f,
        animationSpec = tween(slidingDuration, easing = LinearEasing)
    )

    val co = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        co.launch {
            up = true
            delay(slidingDuration.toLong())
            delay(1000)
            animComplete()
        }
    }

    Grid(modifier = modifier
        .aspectRatio(1f)
        .fillMaxWidth()) {

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(Color.Transparent)
            .fillMaxWidth()
            .fillMaxHeight(curtainHeightPercentage),
            contentAlignment = Alignment.Center,
        ) {
            PixelText(text = "GAME\nOVER", charHeight = 0.5f.grid2mpx, textColor = Color.Red)
        }
    }
}