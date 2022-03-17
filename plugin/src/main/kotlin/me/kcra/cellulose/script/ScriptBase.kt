package me.kcra.cellulose.script

import me.kcra.cellulose.script.internal.CompilationConfiguration
import me.kcra.cellulose.script.internal.EvaluationConfiguration
import org.bukkit.event.Listener
import java.util.logging.Logger
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "cell.kts",
    compilationConfiguration = CompilationConfiguration::class,
    evaluationConfiguration = EvaluationConfiguration::class
)
abstract class ScriptBase : Listener {
    protected val log: Logger = Logger.getLogger("Cellulose [${javaClass.simpleName.lowercase().replace("_cell", "")}]")

    // don't generate getters and setters for these
    @JvmField
    internal var load: () -> Unit = { invokeScriptMethod("load") }
    @JvmField
    internal var enable: () -> Unit = { invokeScriptMethod("enable") }
    @JvmField
    internal var disable: () -> Unit = { invokeScriptMethod("disable") }

    protected fun load(block: () -> Unit) {
        load = block
    }

    protected fun enable(block: () -> Unit) {
        enable = block
    }

    protected fun disable(block: () -> Unit) {
        disable = block
    }

    private fun invokeScriptMethod(method: String, vararg args: Any) {
        try {
            javaClass.getDeclaredMethod(method, *args.map { it.javaClass }.toTypedArray())
                .also { it.isAccessible = true }
                .invoke(this, *args)
        } catch (ignored: NoSuchMethodException) {
            // ignored
        }
    }
}