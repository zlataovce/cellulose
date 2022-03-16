package me.kcra.cellulose.script.internal

import me.kcra.cellulose.CellulosePlugin
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm

internal object EvaluationConfiguration : ScriptEvaluationConfiguration(
    {
        jvm {
            baseClassLoader(CellulosePlugin::class.java.classLoader)
        }
    }
)