package me.kcra.cellulose.script.extension

import kotlinx.coroutines.runBlocking
import me.kcra.cellulose.CellulosePlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

inline fun bukkitRunnable(crossinline block: BukkitRunnable.() -> Unit): BukkitRunnable = object : BukkitRunnable() {
    override fun run() = block(this)
}

inline fun schedule(
    delay: Long = 0,
    period: Long = 0,
    crossinline block: BukkitRunnable.() -> Unit
): BukkitTask {
    val runnable: BukkitRunnable = bukkitRunnable(block)

    return when {
        period > 0 -> runnable.runTaskTimer(
            CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled"),
            delay,
            period
        )
        delay > 0 -> runnable.runTaskLater(
            CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled"),
            delay
        )
        else -> runnable.runTask(
            CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled")
        )
    }
}

inline fun scheduleAsync(
    delay: Long = 0,
    period: Long = 0,
    crossinline block: BukkitRunnable.() -> Unit
): BukkitTask {
    val runnable: BukkitRunnable = bukkitRunnable(block)

    return when {
        period > 0 -> runnable.runTaskTimerAsynchronously(
            CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled"),
            delay,
            period
        )
        delay > 0 -> runnable.runTaskLaterAsynchronously(
            CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled"),
            delay
        )
        else -> runnable.runTaskAsynchronously(
            CellulosePlugin.INSTANCE ?: throw UnsupportedOperationException("Tasks cannot be scheduled when Cellulose is disabled")
        )
    }
}

// coroutines

inline fun scheduleSuspend(
    delay: Long = 0,
    period: Long = 0,
    crossinline block: suspend BukkitRunnable.() -> Unit
): BukkitTask = scheduleAsync(delay, period) { runBlocking { block() } }