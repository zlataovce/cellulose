package me.kcra.cellulose.script

import me.kcra.cellulose.script.internal.CompilationConfiguration
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    compilationConfiguration = CompilationConfiguration::class
)
abstract class ScriptBase