package me.kcra.cellulose

import cloud.commandframework.CommandManager
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import me.kcra.cellulose.api.Cellulose
import me.kcra.cellulose.script.ScriptBase
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import java.util.function.Function
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.util.isError
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.system.measureTimeMillis

class CellulosePlugin : JavaPlugin(), Cellulose {
    private val compilationConfiguration: ScriptCompilationConfiguration = createJvmCompilationConfigurationFromTemplate<ScriptBase>()
    private val loadedScripts: MutableList<ScriptBase> = mutableListOf()
    private val scriptingHost = BasicJvmScriptingHost()

    internal lateinit var commandManager: CommandManager<CommandSender>

    companion object {
        @JvmStatic
        var INSTANCE: CellulosePlugin? = null
    }

    init {
        INSTANCE = this
    }

    override fun onLoad() {
        scriptsFolder.listFiles { file -> file.isFile && file.name.endsWith(".cell.kts") }?.forEach { file ->
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
        commandManager = PaperCommandManager(
            this,
            CommandExecutionCoordinator.simpleCoordinator(),
            Function.identity(),
            Function.identity()
        )

        loadedScripts.forEach { script ->
            Bukkit.getPluginManager().registerEvents(script, this)
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
    }

    override fun getScriptsFolder(): File = dataFolder.resolve("scripts").also { it.mkdirs() }
    override fun getLoadedScripts(): MutableList<Any> = Collections.unmodifiableList(loadedScripts)

    override fun loadScript(file: File, silent: Boolean): ScriptBase? {
        var result: ResultWithDiagnostics<EvaluationResult>
        val time: Long = measureTimeMillis {
            result = scriptingHost.eval(file.toScriptSource(), compilationConfiguration, null)
        }

        if (!silent) {
            if (result.isError()) {
                logger.severe("Compilation of script '${file.name}' failed.")
                result.reports.forEach { if (it.severity == ScriptDiagnostic.Severity.ERROR) logger.severe(it.render()) }
            } else {
                logger.info("Compiled and executed script '${file.name}' in ${time / 1000} seconds.")
            }
        }

        return (result.valueOrNull()?.returnValue?.scriptInstance as ScriptBase?)?.also { loadedScripts.add(it) }
    }

    override fun loadScript(script: String, name: String?, silent: Boolean): ScriptBase? {
        var result: ResultWithDiagnostics<EvaluationResult>
        val time: Long = measureTimeMillis {
            result = scriptingHost.eval(script.toScriptSource(name), compilationConfiguration, null)
        }

        if (!silent) {
            if (result.isError()) {
                logger.severe("Compilation of script ${if (name != null) "'$name' " else ""}failed.")
                result.reports.forEach { if (it.severity == ScriptDiagnostic.Severity.ERROR) logger.severe(it.render()) }
            } else {
                logger.info("Compiled and executed script ${if (name != null) "'$name' " else ""}in ${time / 1000} seconds.")
            }
        }

        return (result.valueOrNull()?.returnValue?.scriptInstance as ScriptBase?)?.also { loadedScripts.add(it) }
    }
}