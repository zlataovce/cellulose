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
    internal var loadAction: () -> Unit = { invokeScriptMethod("load") }
    internal var enableAction: () -> Unit = { invokeScriptMethod("enable") }
    internal var disableAction: () -> Unit = { invokeScriptMethod("disable") }

    protected fun load(block: () -> Unit) {
        loadAction = block
    }

    protected fun enable(block: () -> Unit) {
        enableAction = block
    }

    protected fun disable(block: () -> Unit) {
        disableAction = block
    }

    private fun invokeScriptMethod(method: String, vararg args: Any) {
        try {
            javaClass.getDeclaredMethod(method).also { it.isAccessible = true }.invoke(this, args)
        } catch (ignored: NoSuchMethodException) {
            // ignored
        }
    }
}