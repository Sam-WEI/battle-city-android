package com.samwdev.battlecity.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.samwdev.battlecity.core.MAP_BLOCK_COUNT
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
        val bricks = mutableSetOf<BrickElement>()
        val steels = mutableSetOf<SteelElement>()
        val trees = mutableSetOf<TreeElement>()
        val ices = mutableSetOf<IceElement>()
        val waters = mutableSetOf<WaterElement>()
        var eagle: EagleElement? = null
        val hGridUnitNum = MAP_BLOCK_COUNT // todo get from config json
        val vGridUnitNum = MAP_BLOCK_COUNT // todo get from config json

        configJson.map.forEachIndexed { r, row ->
            row.split(Regex("\\s+")).filter { it.isNotEmpty() }.forEachIndexed { c, block ->
                when (block[0]) {
                    'E' -> {
                        // eagle
                        if (eagle != null) {
                            throw IllegalArgumentException("Eagle can only appear once.")
                        }
                        eagle = EagleElement(r, c, hGridUnitNum)
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
                        val cellCountInARow = 4 * hGridUnitNum

                        ((brickBits shr 12) and 0xf).takeIf { it != 0 }?.let {
                            // 4 TL cells
                            val topLeftRow = cellRow
                            val topLeftCol = cellCol
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridUnitNum)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridUnitNum)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridUnitNum)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridUnitNum)) }
                        }

                        ((brickBits shr 8) and 0xf).takeIf { it != 0 }?.let {
                            // 4 TR cells
                            val topLeftRow = cellRow
                            val topLeftCol = cellCol + 2
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridUnitNum)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridUnitNum)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridUnitNum)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridUnitNum)) }
                        }

                        ((brickBits shr 4) and 0xf).takeIf { it != 0 }?.let {
                            // 4 BL cells
                            val topLeftRow = cellRow + 2
                            val topLeftCol = cellCol
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridUnitNum)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridUnitNum)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridUnitNum)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridUnitNum)) }
                        }

                        ((brickBits shr 0) and 0xf).takeIf { it != 0 }?.let {
                            // 4 BR cells
                            val topLeftRow = cellRow + 2
                            val topLeftCol = cellCol + 2
                            if (it or 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridUnitNum)) }
                            if (it or 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridUnitNum)) }
                            if (it or 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridUnitNum)) }
                            if (it or 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridUnitNum)) }
                        }

                    }
                    'T' -> {
                        // steel
                        val blockInfo = Integer.parseInt(block.substring(1), 16)
                        val cellRow = 2 * r
                        val cellCol = 2 * c
                        if (blockInfo and 0b0001 != 0) {
                            steels.add(SteelElement(cellRow, cellCol, hGridUnitNum))
                        }
                        if (blockInfo and 0b0010 != 0) {
                            steels.add(SteelElement(cellRow, cellCol + 1, hGridUnitNum))
                        }
                        if (blockInfo and 0b0100 != 0) {
                            steels.add(SteelElement(cellRow + 1, cellCol, hGridUnitNum))
                        }
                        if (blockInfo and 0b1000 != 0) {
                            steels.add(SteelElement(cellRow + 1, cellCol + 1, hGridUnitNum))
                        }
                    }
                    'S' -> {
                        // ice
                        ices.add(IceElement(r, c, hGridUnitNum))
                    }
                    'R' -> {
                        // water
                        waters.add(WaterElement(r, c, hGridUnitNum))
                    }
                    'F' -> {
                        // tree
                        trees.add(TreeElement(r, c, hGridUnitNum))
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
                hGridUnitNum = hGridUnitNum,
                vGridUnitNum = vGridUnitNum,
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