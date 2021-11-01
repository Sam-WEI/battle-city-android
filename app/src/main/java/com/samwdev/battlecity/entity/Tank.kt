package com.samwdev.battlecity.entity

enum class BotTankLevel(
    val hp: Int,
    val moveSpeed: Float,
    val bulletSpeed: Float,
    val point: Int,
) {
    Basic(
        hp = 1,
        moveSpeed = 1f,
        bulletSpeed = 1f,
        point = 100,
    ),
    Fast(
        hp = 1,
        moveSpeed = 3f,
        bulletSpeed = 2f,
        point = 200,
    ),
    Power(
        hp = 1,
        moveSpeed = 2f,
        bulletSpeed = 3f,
        point = 300,
    ),
    Armor(
        hp = 4,
        moveSpeed = 2f,
        bulletSpeed = 2f,
        point = 400,
    ),
}
