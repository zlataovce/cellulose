package me.kcra.cellulose.script.extension

import me.kcra.cellulose.CellulosePlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

inline fun bukkitRunnable(crossinline block: (BukkitRunnable) -> Unit): BukkitRunnable = object : BukkitRunnable() {
    override fun run() = block(this)
}

inline fun schedule(crossinline block: (BukkitRunnable) -> Unit): BukkitTask = bukkitRunnable(block).runTask(
    CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled")
)

inline fun scheduleAsync(crossinline block: (BukkitRunnable) -> Unit): BukkitTask = bukkitRunnable(block).runTaskAsynchronously(
    CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled")
)