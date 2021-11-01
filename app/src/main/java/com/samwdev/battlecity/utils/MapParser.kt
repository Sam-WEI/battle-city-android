package com.samwdev.battlecity.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.samwdev.battlecity.entity.StageConfigJson

object MapParser {
    @Composable
    fun parseJson(index: Int): StageConfigJson {
        val jsonStr = with (LocalContext.current) {
            val rawId = resources.getIdentifier("stage_${index}", "raw", packageName)
            resources.openRawResource(rawId).readBytes()
        }
        return jacksonObjectMapper().readValue(jsonStr)
    }


}