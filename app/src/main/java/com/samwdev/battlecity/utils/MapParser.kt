package com.samwdev.battlecity.utils

import android.content.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.samwdev.battlecity.core.TankLevel
import com.samwdev.battlecity.entity.BotGroup
import com.samwdev.battlecity.entity.BrickElement
import com.samwdev.battlecity.entity.EagleElement
import com.samwdev.battlecity.entity.IceElement
import com.samwdev.battlecity.entity.MapConfig
import com.samwdev.battlecity.entity.MapDifficulty
import com.samwdev.battlecity.entity.StageConfig
import com.samwdev.battlecity.entity.StageConfigJson
import com.samwdev.battlecity.entity.SteelElement
import com.samwdev.battlecity.entity.TreeElement
import com.samwdev.battlecity.entity.WaterElement
import java.io.IOException
import kotlin.jvm.Throws

object MapParser {
    fun readJsonFile(context: Context, name: String): StageConfigJson {
        return jacksonObjectMapper().readValue(context.assets.open("maps/stage_${name}.json"))
    }

    fun parse(context: Context, name: String): StageConfig {
        return parse(readJsonFile(context, name))
    }

    fun parse(configJson: StageConfigJson): StageConfig {
        val bricks = mutableSetOf<BrickElement>()
        val steels = mutableSetOf<SteelElement>()
        val trees = mutableSetOf<TreeElement>()
        val ices = mutableSetOf<IceElement>()
        val waters = mutableSetOf<WaterElement>()
        var eagle: EagleElement? = null
        val (width, height) = configJson.size.split("x")
        val hGridSize = width.toInt()
        val vGridSize = height.toInt()

        configJson.map.forEachIndexed { r, row ->
            row.split(Regex("\\s+")).filter { it.isNotEmpty() }.forEachIndexed { c, block ->
                when (block[0]) {
                    'E' -> {
                        // eagle
                        if (eagle != null) {
                            throw IllegalArgumentException("Eagle can only appear once.")
                        }
                        eagle = EagleElement(r, c, hGridSize)
                    }
                    'B' -> {
                        // brick
                        val blockInfo = Integer.parseInt(block.substring(1), 16)
                        var brickBits = 0
                        if (blockInfo and 0b0001 != 0) { brickBits += 0xf000 }
                        if (blockInfo and 0b0010 != 0) { brickBits += 0x0f00 }
                        if (blockInfo and 0b0100 != 0) { brickBits += 0x00f0 }
                        if (blockInfo and 0b1000 != 0) { brickBits += 0x000f }

                        val cellRow = BrickElement.granularity * r
                        val cellCol = BrickElement.granularity * c

                        ((brickBits shr 12) and 0xf).takeIf { it != 0 }?.let {
                            // TL quarter
                            val topLeftRow = cellRow
                            val topLeftCol = cellCol
                            if (it and 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridSize)) }
                            if (it and 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridSize)) }
                            if (it and 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridSize)) }
                            if (it and 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridSize)) }
                        }

                        ((brickBits shr 8) and 0xf).takeIf { it != 0 }?.let {
                            // TR quarter
                            val topLeftRow = cellRow
                            val topLeftCol = cellCol + 2
                            if (it and 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridSize)) }
                            if (it and 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridSize)) }
                            if (it and 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridSize)) }
                            if (it and 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridSize)) }
                        }

                        ((brickBits shr 4) and 0xf).takeIf { it != 0 }?.let {
                            // BL quarter
                            val topLeftRow = cellRow + 2
                            val topLeftCol = cellCol
                            if (it and 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridSize)) }
                            if (it and 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridSize)) }
                            if (it and 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridSize)) }
                            if (it and 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridSize)) }
                        }

                        ((brickBits shr 0) and 0xf).takeIf { it != 0 }?.let {
                            // BR quarter
                            val topLeftRow = cellRow + 2
                            val topLeftCol = cellCol + 2
                            if (it and 0b0001 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol, hGridSize)) }
                            if (it and 0b0010 != 0) { bricks.add(BrickElement(topLeftRow, topLeftCol + 1, hGridSize)) }
                            if (it and 0b0100 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol, hGridSize)) }
                            if (it and 0b1000 != 0) { bricks.add(BrickElement(topLeftRow + 1, topLeftCol + 1, hGridSize)) }
                        }

                    }
                    'S' -> {
                        // steel
                        val blockInfo = Integer.parseInt(block.substring(1), 16)
                        val cellRow = SteelElement.granularity * r
                        val cellCol = SteelElement.granularity * c
                        if (blockInfo and 0b0001 != 0) {
                            steels.add(SteelElement(cellRow, cellCol, hGridSize))
                        }
                        if (blockInfo and 0b0010 != 0) {
                            steels.add(SteelElement(cellRow, cellCol + 1, hGridSize))
                        }
                        if (blockInfo and 0b0100 != 0) {
                            steels.add(SteelElement(cellRow + 1, cellCol, hGridSize))
                        }
                        if (blockInfo and 0b1000 != 0) {
                            steels.add(SteelElement(cellRow + 1, cellCol + 1, hGridSize))
                        }
                    }
                    'I' -> {
                        // ice
                        ices.add(IceElement(r, c, hGridSize))
                    }
                    'W' -> {
                        // water
                        waters.add(WaterElement(r, c, hGridSize))
                    }
                    'T' -> {
                        // tree
                        trees.add(TreeElement(r, c, hGridSize))
                    }
                }
            }
        }

        val bots = configJson.bots.map { grp ->
            // e.g, L1x10, L3x2
            val (level, count) = grp.split("x")
            val levelEnum = TankLevel.values()[level.substring(1).toInt() - 1]
            return@map BotGroup(
                count = count.toInt(),
                level = levelEnum
            )
        }

        return StageConfig(
            name = configJson.name,
            difficulty = MapDifficulty.of(configJson.difficulty),
            map = MapConfig(
                hGridSize = hGridSize,
                vGridSize = vGridSize,
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