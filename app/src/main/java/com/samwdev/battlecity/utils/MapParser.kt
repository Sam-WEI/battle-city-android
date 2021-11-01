package com.samwdev.battlecity.utils

import android.graphics.Point
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.samwdev.battlecity.entity.*
import java.lang.IllegalArgumentException
import java.util.*

object MapParser {
    @Composable
    fun parseJson(index: Int): StageConfigJson {
        val jsonStr = with (LocalContext.current) {
            val rawId = resources.getIdentifier("stage_${index}", "raw", packageName)
            resources.openRawResource(rawId).readBytes()
        }
        return jacksonObjectMapper().readValue(jsonStr)
    }

    fun parse(configJson: StageConfigJson): StageConfig {
        val bricks = mutableListOf<Int>()
        val steels = mutableListOf<Int>()
        val trees = mutableListOf<Int>()
        val ices = mutableListOf<Int>()
        val waters = mutableListOf<Int>()
        var eagle: Point? = null
        configJson.map.forEachIndexed { r, row ->
            row.split(Regex("\\s+")).forEachIndexed { c, block ->
                when (block[0]) {
                    'E' -> {
                        // eagle
                        if (eagle != null) {
                            throw IllegalArgumentException("Eagle can only appear once.")
                        }
                        eagle = Point(r, c)
                    }
                    'B' -> {
                        // brick
                        val blockInfo = Integer.parseInt(block.substring(1), 16)
                        var brickBits = 0
                        when {
                            blockInfo or 0b0001 != 0 -> brickBits += 0xf000
                            blockInfo or 0b0010 != 0 -> brickBits += 0x0f00
                            blockInfo or 0b0100 != 0 -> brickBits += 0x00f0
                            blockInfo or 0b1000 != 0 -> brickBits += 0x000f
                        }
                        val cellRow = 4 * r
                        val cellCol = 4 * c
                        val cellCountInARow = 4 * MAP_BLOCK_COUNT

                        with ((brickBits shr 12) and 0xf) {
                            // 4 TL cells
                            val topLeftIndex = cellRow * cellCountInARow + cellCol
                            if (this or 0b0001 != 0) { bricks.add(topLeftIndex + 0) }
                            if (this or 0b0010 != 0) { bricks.add(topLeftIndex + 1) }
                            if (this or 0b0100 != 0) { bricks.add(topLeftIndex + cellCountInARow) }
                            if (this or 0b1000 != 0) { bricks.add(topLeftIndex + 1 + cellCountInARow) }
                        }

                        with ((brickBits shr 8) and 0xf) {
                            // 4 TR cells
                            val topLeftIndex = cellRow * cellCountInARow + 2
                            if (this or 0b0001 != 0) { bricks.add(topLeftIndex + 0) }
                            if (this or 0b0010 != 0) { bricks.add(topLeftIndex + 1) }
                            if (this or 0b0100 != 0) { bricks.add(topLeftIndex + cellCountInARow) }
                            if (this or 0b1000 != 0) { bricks.add(topLeftIndex + 1 + cellCountInARow) }
                        }

                        with ((brickBits shr 4) and 0xf) {
                            // 4 BL cells
                            val topLeftIndex = (cellRow + 2) * cellCountInARow + cellCol
                            if (this or 0b0001 != 0) { bricks.add(topLeftIndex + 0) }
                            if (this or 0b0010 != 0) { bricks.add(topLeftIndex + 1) }
                            if (this or 0b0100 != 0) { bricks.add(topLeftIndex + cellCountInARow) }
                            if (this or 0b1000 != 0) { bricks.add(topLeftIndex + 1 + cellCountInARow) }
                        }

                        with (brickBits and 0xf) {
                            // 4 BR cells
                            val topLeftIndex = (cellRow + 2) * cellCountInARow + cellCol + 2
                            if (this or 0b0001 != 0) { bricks.add(topLeftIndex + 0) }
                            if (this or 0b0010 != 0) { bricks.add(topLeftIndex + 1) }
                            if (this or 0b0100 != 0) { bricks.add(topLeftIndex + cellCountInARow) }
                            if (this or 0b1000 != 0) { bricks.add(topLeftIndex + 1 + cellCountInARow) }
                        }
                    }
                    'T' -> {
                        // steel
                        val blockInfo = Integer.parseInt(block.substring(1), 16)
                        val cellRow = 2 * r
                        val cellCol = 2 * c
                        val cellCountInARow = 2 * MAP_BLOCK_COUNT
                        when {
                            blockInfo and 0b0001 != 0 -> steels.add(cellRow * cellCountInARow + cellCol)
                            blockInfo and 0b0010 != 0 -> steels.add(cellRow * cellCountInARow + cellCol + 1)
                            blockInfo and 0b0100 != 0 -> steels.add((cellRow + 1) * cellCountInARow + cellCol)
                            blockInfo and 0b1000 != 0 -> steels.add((cellRow + 1) * cellCountInARow + cellCol + 1)
                        }
                    }
                    'S' -> {
                        // ice
                        ices.add(r * MAP_BLOCK_COUNT + c)
                    }
                    'R' -> {
                        // water
                        waters.add(r * MAP_BLOCK_COUNT + c)
                    }
                    'F' -> {
                        // tree
                        trees.add(r * MAP_BLOCK_COUNT + c)
                    }
                }
            }
        }

        val bots = configJson.bots.map { grp ->
            val (count, level) = grp.split("*")
            return@map BotGroup(
                count = count.toInt(),
                level = level.lowercase().replaceFirstChar { it.uppercase() }.let { BotTankLevel.valueOf(it) }
            )
        }

        return StageConfig(
            name = configJson.name,
            difficulty = configJson.difficulty,
            map = MapElements(
                trees = trees,
                bricks = bricks,
                steels = steels,
                waters = waters,
                ices = ices,
                eagle = eagle!!,
            ),
            bots = bots,
        )
    }
}