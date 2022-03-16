package me.kcra.cellulose.script.extension

import me.kcra.cellulose.CellulosePlugin
import me.kcra.cellulose.script.ScriptBase
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

inline fun <reified T : Event> ScriptBase.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline block: (T) -> Unit
) {
    Bukkit.getPluginManager().registerEvent(
        T::class.java,
        this,
        priority,
        { _, event -> block(event as T) },
        CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Event listeners cannot be registered when Cellulose is disabled"),
        ignoreCancelled
    )
}