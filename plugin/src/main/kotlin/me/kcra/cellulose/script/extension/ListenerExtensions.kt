@file:Suppress("unused") // for use in scripts

package me.kcra.cellulose.script.extension

import me.kcra.cellulose.CellulosePlugin.Companion.ensureEnabled
import me.kcra.cellulose.script.ScriptBase
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

inline fun <reified T : Event> ScriptBase.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline block: (T) -> Unit,
) = ensureEnabled { plugin ->
    Bukkit.getPluginManager()
        .registerEvent(T::class.java, this, priority, { _, event -> block(event as T) }, plugin, ignoreCancelled)
}