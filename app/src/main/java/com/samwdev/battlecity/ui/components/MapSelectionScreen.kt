package com.samwdev.battlecity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.samwdev.battlecity.core.grid2mpx
import com.samwdev.battlecity.entity.MapConfig
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.utils.MapParser
import kotlinx.coroutines.launch

@Composable
fun MapSelectionScreen(onSelect: (StageConfig) -> Unit) {
    val assetManager = LocalContext.current.assets
    val co = rememberCoroutineScope()
    val objectMapper = remember { jacksonObjectMapper() }

    var stages by remember(assetManager) { mutableStateOf(listOf<StageConfig>()) }

    LaunchedEffect(Unit) {
        co.launch {
            val stageConfigs = assetManager.list("maps")!!.map { path ->
                val jsonObject = objectMapper.readValue<StageConfigJson>(assetManager.open("maps/$path"))
                MapParser.parse(jsonObject)
            }.sortedWith { s1, s2 ->
                val n1 = s1.name.substringAfter("stage_").toInt()
                val n2 = s2.name.substringAfter("stage_").toInt()
                n1 - n2
            }
            stages = stageConfigs
        }
    }

    LazyColumn(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(stages, key = { it.name }) { item ->
            Grid(modifier = Modifier
                .clickable { onSelect(item) }
                .background(Color.DarkGray)
                .padding(10.dp)
                .fillMaxWidth(0.6f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PixelText(text = "STAGE ${item.name}", textColor = Color.White, charHeight = 0.5f.grid2mpx)
                    BattlefieldStatic(mapConfig = item.map)
                }
            }
        }
    }
}