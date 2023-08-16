package com.samwdev.battlecity.core.plugin

import com.samwdev.battlecity.core.Tank
import java.util.TreeMap

abstract class PluginManager<T> : Plugin<T> {
    constructor()
    override val identifier: String = "Plugin Manager"

    private val plugins = TreeMap<String, Plugin<T>>()

    var onPluginAdded: ((Plugin<T>) -> Unit)? = null
    var onPluginRemoved: ((Plugin<T>) -> Unit)? = null

    override fun transform(obj: T): T {
        return plugins.values.fold(obj) { o, plugin -> plugin.transform(o) }
    }

    override fun detransform(obj: T): T {
        return plugins.values.fold(obj) { o, plugin -> plugin.transform(o) }
    }

    fun addPlugin(plugin: Plugin<T>) {
        plugins[plugin.identifier] = plugin
        onPluginAdded?.invoke(plugin)
    }

    fun removePlugin(plugin: Plugin<T>) {
        plugins.remove(plugin.identifier)
        onPluginRemoved?.invoke(plugin)
    }

    fun removePlugin(identifier: String) {
        plugins[identifier]?.let { removePlugin(it) }
    }
}

class TankPluginManager : PluginManager<Tank>()