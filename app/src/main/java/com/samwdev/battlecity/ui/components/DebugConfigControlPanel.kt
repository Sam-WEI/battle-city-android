package com.samwdev.battlecity.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Build
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samwdev.battlecity.core.DebugConfig
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@Composable
fun DebugConfigControlToggle(
    modifier: Modifier = Modifier,
    debugConfig: DebugConfig,
    onConfigChange: (DebugConfig) -> Unit,
) {
    var showDebugPanel by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(
            modifier = modifier.align(Alignment.BottomEnd),
            onClick = { showDebugPanel = !showDebugPanel },
        ) {
            Icon(
                imageVector = Icons.Outlined.Build,
                tint = Color.LightGray,
                contentDescription = null,
            )
        }

        AnimatedVisibility(
            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 50.dp),
            visible = showDebugPanel
        ) {
            DebugConfigControlPanel(
            modifier = Modifier.align(Alignment.TopStart),
                debugConfig = debugConfig,
                onConfigChange = onConfigChange,
                onClose = { showDebugPanel = false }
            )
        }
    }

}

@Composable
fun DebugConfigControlPanel(
    modifier: Modifier = Modifier,
    debugConfig: DebugConfig,
    onConfigChange: (DebugConfig) -> Unit,
    onClose: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .wrapContentSize(),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
    ) {
        Box {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onClose
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
            }

            Column(
                modifier = modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Debug", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                DebugConfigSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Show FPS",
                    value = debugConfig.showFps,
                    onSwitch = { onConfigChange(debugConfig.copy(showFps = it)) }
                )
                DebugConfigSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Show brick index",
                    value = debugConfig.showBrickIndex,
                    onSwitch = { onConfigChange(debugConfig.copy(showBrickIndex = it)) }
                )
                DebugConfigSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Show steel index",
                    value = debugConfig.showSteelIndex,
                    onSwitch = { onConfigChange(debugConfig.copy(showSteelIndex = it)) }
                )
                DebugConfigSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Show tank pivot box",
                    value = debugConfig.showPivotBox,
                    onSwitch = { onConfigChange(debugConfig.copy(showPivotBox = it)) }
                )
                DebugConfigSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Lock tick",
                    value = debugConfig.fixTickDelta,
                    onSwitch = { onConfigChange(debugConfig.copy(fixTickDelta = it)) }
                )
                if (debugConfig.fixTickDelta) {
                    Text(
                        text = "Locked tick: ${debugConfig.tickDelta}",
                        fontSize = 14.sp
                    )
                    Slider(
                        value = debugConfig.tickDelta.toFloat(),
                        enabled = debugConfig.fixTickDelta,
                        valueRange = 1f..100f,
                        onValueChange = { onConfigChange(debugConfig.copy(tickDelta = it.toInt())) }
                    )
                }

                Text(
                    text = "Max FPS: ${debugConfig.maxFps}",
                    fontSize = 14.sp
                )
                Slider(
                    value = debugConfig.maxFps.toFloat(),
                    valueRange = 1f..150f,
                    onValueChange = { onConfigChange(debugConfig.copy(maxFps = it.toInt())) }
                )

                Text(
                    text = "Max bot: ${debugConfig.maxBot}",
                    fontSize = 14.sp
                )
                Slider(
                    value = debugConfig.maxBot.toFloat(),
                    valueRange = 0f..50f,
                    onValueChange = { onConfigChange(debugConfig.copy(maxBot = it.toInt())) }
                )
            }
        }
    }
}

@Composable
private fun DebugConfigSwitch(
    modifier: Modifier = Modifier,
    label: String,
    value: Boolean,
    onSwitch: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = value,
            onCheckedChange = onSwitch,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 14.sp,
        )
    }
}

@Preview
@Composable
private fun ControlPanelPreview() {
    BattleCityTheme {
        Box(modifier = Modifier.wrapContentSize()) {
            DebugConfigControlPanel(
                debugConfig = DebugConfig(
                    showFps = true,
                    fixTickDelta = true,
                ),
                onConfigChange = {}
            )
        }
    }
}