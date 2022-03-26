@file:Suppress("unused") // for use in scripts

package me.kcra.cellulose.script.extension

import kotlinx.coroutines.runBlocking
import me.kcra.cellulose.CellulosePlugin.Companion.pluginContext
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

inline fun bukkitRunnable(crossinline block: BukkitRunnable.() -> Unit): BukkitRunnable = object : BukkitRunnable() {
    override fun run() = block(this)
}

inline fun schedule(
    delay: Long = 0,
    period: Long = 0,
    crossinline block: BukkitRunnable.() -> Unit,
): BukkitTask {
    val runnable: BukkitRunnable = bukkitRunnable(block)

    pluginContext { instance ->
        return when {
            period > 0 -> runnable.runTaskTimer(instance, delay, period)
            delay > 0 -> runnable.runTaskLater(instance, delay)
            else -> runnable.runTask(instance)
        }
    }
}

inline fun scheduleAsync(
    delay: Long = 0,
    period: Long = 0,
    crossinline block: BukkitRunnable.() -> Unit,
): BukkitTask {
    val runnable: BukkitRunnable = bukkitRunnable(block)

    pluginContext { instance ->
        return when {
            period > 0 -> runnable.runTaskTimerAsynchronously(instance, delay, period)
            delay > 0 -> runnable.runTaskLaterAsynchronously(instance, delay)
            else -> runnable.runTaskAsynchronously(instance)
        }
    }
}

// coroutines

inline fun scheduleSuspend(
    delay: Long = 0,
    period: Long = 0,
    crossinline block: suspend BukkitRunnable.() -> Unit,
): BukkitTask = scheduleAsync(delay, period) {
    runBlocking {
        block()
    }
}