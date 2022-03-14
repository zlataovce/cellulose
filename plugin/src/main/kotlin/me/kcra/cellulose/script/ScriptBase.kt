package me.kcra.cellulose.script

import me.kcra.cellulose.script.internal.CompilationConfiguration
import java.util.logging.Logger
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "cell.kts",
    compilationConfiguration = CompilationConfiguration::class
)
abstract class ScriptBase {
    protected val log: Logger = Logger.getLogger("Cellulose [${javaClass.simpleName.replace("_cell", "", ignoreCase = true)}]")
}