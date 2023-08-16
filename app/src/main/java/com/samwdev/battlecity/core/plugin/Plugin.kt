package com.samwdev.battlecity.core.plugin

interface Plugin<T> {
    val identifier: String
    fun transform(obj: T): T
    fun detransform(obj: T): T
}
