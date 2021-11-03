package com.samwdev.battlecity.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.samwdev.battlecity.entity.*

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
        val bricks = mutableListOf<BrickElement>()
        val steels = mutableListOf<SteelElement>()
        val trees = mutableListOf<TreeElement>()
        val ices = mutableListOf<IceElement>()
        val waters = mutableListOf<WaterElement>()
        var eagle: IntOffset? = null
        configJson.map.forEachIndexed { r, row ->
            row.split(Regex("\\s+")).filter { it.isNotEmpty() }.forEachIndexed { c, block ->
                when (block[0]) {
                    'E' -> {
                        // eagle
                        if (eagle != null) {
                            throw IllegalArgumentException("Eagle can only appear once.")
                        }
                        eagle = IntOffset(c, r)
                    }
                    'B' -> {
                        // brick
                        val blockInfo = Integer.parseInt(block.substring(1), 16)
                        var brickBits = 0
                        if (blockInfo and 0b0001 != 0) { brickBits += 0xf000 }
                        if (blockInfo and 0b0010 != 0) { brickBits += 0x0f00 }
                        if (blockInfo and 0b0100 != 0) { brickBits += 0x00f0 }
                        if (blockInfo and 0b1000 != 0) { brickBits += 0x000f }

                        val cellRow = 4 * r
                        val cellCol = 4 * c
                        val cellCountInARow = 4 * MAP_BLOCK_COUNT

                        ((brickBits shr 12) and 0xf).takeIf { it != 0 }?.let {
                            // 4 TL cells
                            val topLeftIndex = cellRow * cellCountInARow + cellCol
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftIndex + 0)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftIndex + 1)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftIndex + cellCountInARow)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftIndex + 1 + cellCountInARow)) }
                        }

                        ((brickBits shr 8) and 0xf).takeIf { it != 0 }?.let {
                            // 4 TR cells
                            val topLeftIndex = cellRow * cellCountInARow + cellCol + 2
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftIndex + 0)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftIndex + 1)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftIndex + cellCountInARow)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftIndex + 1 + cellCountInARow)) }
                        }

                        ((brickBits shr 4) and 0xf).takeIf { it != 0 }?.let {
                            // 4 BL cells
                            val topLeftIndex = (cellRow + 2) * cellCountInARow + cellCol
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftIndex + 0)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftIndex + 1)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftIndex + cellCountInARow)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftIndex + 1 + cellCountInARow)) }
                        }

                        ((brickBits shr 0) and 0xf).takeIf { it != 0 }?.let {
                            // 4 BR cells
                            val topLeftIndex = (cellRow + 2) * cellCountInARow + cellCol + 2
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftIndex + 0)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftIndex + 1)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftIndex + cellCountInARow)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftIndex + 1 + cellCountInARow)) }
                        }

                    }
                    'T' -> {
                        // steel
                        val blockInfo = Integer.parseInt(block.substring(1), 16)
                        val cellRow = 2 * r
                        val cellCol = 2 * c
                        val cellCountInARow = 2 * MAP_BLOCK_COUNT
                        if (blockInfo and 0b0001 != 0) {
                            steels.add(SteelElement(cellRow * cellCountInARow + cellCol))
                        }
                        if (blockInfo and 0b0010 != 0) {
                            steels.add(SteelElement(cellRow * cellCountInARow + cellCol + 1))
                        }
                        if (blockInfo and 0b0100 != 0) {
                            steels.add(SteelElement((cellRow + 1) * cellCountInARow + cellCol))
                        }
                        if (blockInfo and 0b1000 != 0) {
                            steels.add(SteelElement((cellRow + 1) * cellCountInARow + cellCol + 1))
                        }
                    }
                    'S' -> {
                        // ice
                        ices.add(IceElement(r * MAP_BLOCK_COUNT + c))
                    }
                    'R' -> {
                        // water
                        waters.add(WaterElement(r * MAP_BLOCK_COUNT + c))
                    }
                    'F' -> {
                        // tree
                        trees.add(TreeElement(r * MAP_BLOCK_COUNT + c))
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