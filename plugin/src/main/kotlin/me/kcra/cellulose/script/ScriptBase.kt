package me.kcra.cellulose.script

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.kotlin.MutableCommandBuilder
import me.kcra.cellulose.CellulosePlugin
import me.kcra.cellulose.script.internal.CompilationConfiguration
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import java.util.logging.Logger
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "cell.kts",
    compilationConfiguration = CompilationConfiguration::class
)
abstract class ScriptBase : Listener {
    protected val log: Logger = Logger.getLogger("Cellulose [${javaClass.simpleName.lowercase().replace("_cell", "")}]")

    protected fun command(
        name: String,
        description: ArgumentDescription = ArgumentDescription.empty(),
        aliases: Array<String> = emptyArray(),
        lambda: MutableCommandBuilder<CommandSender>.() -> Unit
    ): MutableCommandBuilder<CommandSender> = MutableCommandBuilder(
        name,
        description,
        aliases,
        CellulosePlugin.INSTANCE?.commandManager ?: throw UnsupportedOperationException("Commands cannot be registered when Cellulose is disabled"),
        lambda
    )
}