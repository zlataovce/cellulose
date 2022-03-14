package me.kcra.cellulose

import me.kcra.cellulose.api.Cellulose
import me.kcra.cellulose.script.ScriptBase
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.Collections
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.util.isError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.system.measureTimeMillis

class CellulosePlugin : JavaPlugin(), Cellulose {
    private val compilationConfiguration: ScriptCompilationConfiguration = createJvmCompilationConfigurationFromTemplate<ScriptBase>()
    private val loadedScripts: MutableList<Any> = mutableListOf()
    private val scriptingHost = BasicJvmScriptingHost()

    companion object {
        @JvmStatic
        private var INSTANCE: Cellulose? = null
    }

    init {
        INSTANCE = this
    }

    override fun onLoad() {
        scriptsFolder.listFiles { file -> file.isFile && file.name.endsWith(".kts") }?.forEach { file ->
            loadScript(file, false)?.also { script ->
                try {
                    script.javaClass.getDeclaredMethod("load").also { it.isAccessible = true }.invoke(script)
                } catch (ignored: NoSuchMethodException) {
                    // ignored
                }
            }
        }
    }

    override fun onEnable() {
        loadedScripts.forEach { script ->
            try {
                script.javaClass.getDeclaredMethod("enable").also { it.isAccessible = true }.invoke(script)
            } catch (ignored: NoSuchMethodException) {
                // ignored
            }
        }
    }

    override fun onDisable() {
        loadedScripts.forEach { script ->
            try {
                script.javaClass.getDeclaredMethod("disable").also { it.isAccessible = true }.invoke(script)
            } catch (ignored: NoSuchMethodException) {
                // ignored
            }
        }
        loadedScripts.clear() // remove any references to the scripts, so they can be GC'd
    }

    override fun getInstance(): Cellulose = INSTANCE ?: throw UnsupportedOperationException("Cellulose is not loaded yet")
    override fun getScriptsFolder(): File = dataFolder.resolve("scripts").also { it.mkdirs() }
    override fun getLoadedScripts(): MutableList<Any> = Collections.unmodifiableList(loadedScripts)

    override fun loadScript(file: File, silent: Boolean): Any? {
        var result: ResultWithDiagnostics<EvaluationResult>
        val time: Long = measureTimeMillis {
            result = scriptingHost.eval(file.toScriptSource(), compilationConfiguration, null)
        }

        if (!silent) {
            if (result.isError()) {
                logger.severe("Compilation of script '${file.name}' failed.")
                result.reports.forEach { it.exception?.printStackTrace() }
            } else {
                logger.info("Compiled and executed script '${file.name}' in ${time / 1000} seconds.")
            }
        }

        return result.valueOrNull()?.returnValue?.scriptInstance?.also { loadedScripts.add(it) }
    }

    override fun loadScript(script: String, name: String?, silent: Boolean): Any? {
        var result: ResultWithDiagnostics<EvaluationResult>
        val time: Long = measureTimeMillis {
            result = scriptingHost.eval(script.toScriptSource(name), compilationConfiguration, null)
        }

        if (!silent) {
            if (result.isError()) {
                logger.severe("Compilation of script ${if (name != null) "'$name' " else ""}failed.")
                result.reports.forEach { it.exception?.printStackTrace() }
            } else {
                logger.info("Compiled and executed script ${if (name != null) "'$name' " else ""}in ${time / 1000} seconds.")
            }
        }

        return result.valueOrNull()?.returnValue?.scriptInstance?.also { loadedScripts.add(it) }
    }
}