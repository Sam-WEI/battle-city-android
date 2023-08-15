package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samwdev.battlecity.core.*
import com.samwdev.battlecity.ui.theme.BattleCityTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val ColorRed = Color(210, 81, 65)
private val ColorOrange = Color(241, 176, 96)

@Composable
fun ScoreboardScreen() {
    val battleViewModel: BattleViewModel = LocalBattleViewModel.current
    val data: ScoreboardData = battleViewModel.scoreState.collectScoreboardData()
    ScoreboardScreen(data = data, stageName = battleViewModel.currentStageName!!) {
        battleViewModel.scoreboardCompleted()
    }
}

@Composable
private fun ScoreboardScreen(data: ScoreboardData, stageName: String, doneDisplaying: () -> Unit) {
    val frameList = remember(data) {
        var lastFrameData = ScoreDisplayData(totalScore = data.totalScore)
        val frames = mutableListOf(ScoreDisplayFrame(lastFrameData, 500))
        data.killCount.entries.forEach { (lvl, num) ->
            repeat(num + 1) { i ->
                val score = when (lvl) {
                    TankLevel.Level1 -> lastFrameData.copy(level1Num = i)
                    TankLevel.Level2 -> lastFrameData.copy(level2Num = i)
                    TankLevel.Level3 -> lastFrameData.copy(level3Num = i)
                    TankLevel.Level4 -> lastFrameData.copy(level4Num = i)
                }
                if (num == 0) {
                    frames.add(ScoreDisplayFrame(score, 100))
                } else if (i > 0) {
                    frames.add(ScoreDisplayFrame(score, 150, SoundEffect.ScoreboardTick))
                }
                lastFrameData = score
            }
            frames.add(ScoreDisplayFrame(lastFrameData, 500))
        }
        frames.add(ScoreDisplayFrame(
            lastFrameData.copy(totalNum = data.killCount.values.sum()), 0)
        ) // total
        frames.toList()
    }

    var displayScoreData by remember { mutableStateOf(ScoreDisplayData(totalScore = 20000)) }

    val coroutine = rememberCoroutineScope()
    LaunchedEffect(data) {
        coroutine.launch {
            frameList.forEach { (score, delay, soundEffect) ->
                displayScoreData = score
                soundEffect?.let { SoundPlayer.INSTANCE.play(it) }
                delay(delay)
            }
            delay(1000)
            doneDisplaying()
        }
    }
    ScoreboardDataFrame(displayScoreData, stageName)
}

@Composable
private fun ScoreboardDataFrame(displayData: ScoreDisplayData, stageName: String) {
    Grid(
        gridSize = 15,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.cell2mpx.mpx2dp)
                .offset(y = 1.cell2mpx.mpx2dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Row(Modifier.weight(1f, true),
                horizontalArrangement = Arrangement.End) {
                PixelText(
                    text = "HI-SCORE",
                    charHeight = 0.5f.cell2mpx,
                    topLeft = Offset.Zero,
                    textColor = ColorRed,
                )
            }
            Row(Modifier.weight(1f, true),
                horizontalArrangement = Arrangement.Start) {
                PixelText(
                    text = "   20000",
                    charHeight = 0.5f.cell2mpx,
                    topLeft = Offset.Zero,
                    textColor = ColorOrange,
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.cell2mpx.mpx2dp)
                .offset(y = 2.cell2mpx.mpx2dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            PixelText(
                text = "STAGE   $stageName",
                charHeight = 0.5f.cell2mpx,
                topLeft = Offset.Zero,
            )
        }
        DynamicBody(displayData)
    }
}

@Composable
private fun DynamicBody(displayData: ScoreDisplayData) {
    Row(
        Modifier
            .offset(y = 3.cell2mpx.mpx2dp)
            .fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f), horizontalAlignment = Alignment.End) {
            PlayerInfo(displayData, modifier = Modifier.fillMaxWidth())
            PixelText(
                text = "TOTAL",
                charHeight = 0.5f.cell2mpx,
                modifier = Modifier.height(1f.cell2mpx.mpx2dp),
            )
        }
        Row(
            Modifier
                .fillMaxHeight()
                .weight(1f, true)
                .offset(y = 3f.cell2mpx.mpx2dp)
                .padding(horizontal = 0.5f.cell2mpx.mpx2dp),
        ) {
            Column(Modifier.weight(2f), horizontalAlignment = Alignment.End) {
                PixelText(
                    text = "${displayData.level1NumText}←",
                    charHeight = 0.5f.cell2mpx,
                    modifier = Modifier.height(1f.cell2mpx.mpx2dp).padding(end = 2f.mpx2dp),
                )
                Spacer(modifier = Modifier.height(1f.cell2mpx.mpx2dp))
                PixelText(
                    text = "${displayData.level2NumText}←",
                    charHeight = 0.5f.cell2mpx,
                    modifier = Modifier.height(1f.cell2mpx.mpx2dp).padding(end = 2f.mpx2dp),
                )
                Spacer(modifier = Modifier.height(1f.cell2mpx.mpx2dp))
                PixelText(
                    text = "${displayData.level3NumText}←",
                    charHeight = 0.5f.cell2mpx,
                    modifier = Modifier.height(1f.cell2mpx.mpx2dp).padding(end = 2f.mpx2dp),
                )
                Spacer(modifier = Modifier.height(1f.cell2mpx.mpx2dp))
                PixelText(
                    text = "${displayData.level4NumText}←",
                    charHeight = 0.5f.cell2mpx,
                    modifier = Modifier.height(1f.cell2mpx.mpx2dp).padding(end = 2f.mpx2dp),
                )
                PixelText(
                    text = displayData.totalNumText.padStart(2),
                    charHeight = 0.5f.cell2mpx,
                    modifier = Modifier
                        .height(1f.cell2mpx.mpx2dp)
                        .align(Start),
                )

            }
            Column(Modifier.weight(1f)) {
                PixelCanvas(Modifier.offset(y = -0.2f.cell2mpx.mpx2dp)) {
                    drawBotTankLevel1(0, BotNormalPalette)
                    translate(top = 2.cell2mpx) {
                        this as PixelDrawScope
                        drawBotTankLevel2(0, BotNormalPalette)
                    }
                    translate(top = 4.cell2mpx) {
                        this as PixelDrawScope
                        drawBotTankLevel3(0, BotNormalPalette)
                    }
                    translate(top = 6.cell2mpx) {
                        this as PixelDrawScope
                        drawBotTankLevel4(0, BotNormalPalette)
                    }
                    translate(top = 7.cell2mpx) {
                        this as PixelDrawScope
                        drawRect(Color.White, topLeft = Offset((-1.5f).cell2mpx, 0f), size = Size(4f.cell2mpx, 2f))
                    }
                }
            }
            Column(Modifier.weight(2f)) {
                // →
            }

        }
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)) {
            if (false) {
                PlayerInfo(
                    displayData = displayData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 1.cell2mpx.mpx2dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerInfo(displayData: ScoreDisplayData, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.End) {
        PixelText(
            text = "I-PLAYER",
            charHeight = 0.5f.cell2mpx,
            modifier = Modifier.height(1f.cell2mpx.mpx2dp),
            textColor = ColorRed
        )
        PixelText(
            text = displayData.totalScore.toString(),
            charHeight = 0.5f.cell2mpx,
            modifier = Modifier.height(1f.cell2mpx.mpx2dp),
            textColor = ColorOrange
        )
        Spacer(modifier = Modifier.height(1f.cell2mpx.mpx2dp))
        PixelText(
            text = "${displayData.level1ScoreText} PTS",
            charHeight = 0.5f.cell2mpx,
            modifier = Modifier.height(1f.cell2mpx.mpx2dp),
        )
        Spacer(modifier = Modifier.height(1f.cell2mpx.mpx2dp))
        PixelText(
            text = "${displayData.level2ScoreText} PTS",
            charHeight = 0.5f.cell2mpx,
            modifier = Modifier.height(1f.cell2mpx.mpx2dp),
        )
        Spacer(modifier = Modifier.height(1f.cell2mpx.mpx2dp))
        PixelText(
            text = "${displayData.level3ScoreText} PTS",
            charHeight = 0.5f.cell2mpx,
            modifier = Modifier.height(1f.cell2mpx.mpx2dp),
        )
        Spacer(modifier = Modifier.height(1f.cell2mpx.mpx2dp))
        PixelText(
            text = "${displayData.level4ScoreText} PTS",
            charHeight = 0.5f.cell2mpx,
            modifier = Modifier.height(1f.cell2mpx.mpx2dp),
        )
    }
}

private data class ScoreDisplayFrame(
    val data: ScoreDisplayData,
    val delay: Long,
    val soundEffect: SoundEffect? = null,
)

private data class ScoreDisplayData(
    val totalScore: Int,
    val level1Num: Int? = null,
    val level2Num: Int? = null,
    val level3Num: Int? = null,
    val level4Num: Int? = null,
    val totalNum: Int? = null,
) {
    val level1NumText: String get() = level1Num?.toString() ?: ""
    val level2NumText: String get() = level2Num?.toString() ?: ""
    val level3NumText: String get() = level3Num?.toString() ?: ""
    val level4NumText: String get() = level4Num?.toString() ?: ""

    val level1ScoreText: String get() = level1Num?.let { it * 100 }?.toString() ?: ""
    val level2ScoreText: String get() = level2Num?.let { it * 200 }?.toString() ?: ""
    val level3ScoreText: String get() = level3Num?.let { it * 300 }?.toString() ?: ""
    val level4ScoreText: String get() = level4Num?.let { it * 400 }?.toString() ?: ""

    val totalNumText: String get() = totalNum?.toString() ?: ""
}

@Preview
@Composable
private fun ScoreboardScreenPreview() {
    BattleCityTheme {
        Box(modifier = Modifier.size(500.dp)) {
            ScoreboardDataFrame(
                ScoreDisplayData(
                    totalScore = 2000,
                    level1Num = 1,
                    level2Num = 3,
                    level3Num = 3,
                    level4Num = 2,
                    totalNum = 16,
                ),
                "2"
            )
        }
    }
}