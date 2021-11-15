package com.samwdev.battlecity.entity

enum class BotTankLevel(
    val hp: Int,
    val moveSpeed: Float,
    val bulletSpeed: Float,
    val point: Int,
    val fireCoolDown: Int,
) {
    Basic(
        hp = 1,
        moveSpeed = 1f,
        bulletSpeed = 1f,
        point = 100,
        fireCoolDown = 300,
    ),
    Fast(
        hp = 1,
        moveSpeed = 3f,
        bulletSpeed = 2f,
        point = 200,
        fireCoolDown = 200,
    ),
    Power(
        hp = 1,
        moveSpeed = 2f,
        bulletSpeed = 3f,
        point = 300,
        fireCoolDown = 200,
    ),
    Armor(
        hp = 4,
        moveSpeed = 2f,
        bulletSpeed = 2f,
        point = 400,
        fireCoolDown = 200,
    ),
}
