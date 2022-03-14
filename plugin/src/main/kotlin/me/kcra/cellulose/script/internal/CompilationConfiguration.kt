package me.kcra.cellulose.script.internal

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

private val resolver = CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver())

object CompilationConfiguration : ScriptCompilationConfiguration(
    {
        defaultImports(DependsOn::class, Repository::class)

        jvm {
            dependenciesFromCurrentContext(wholeClasspath = true)
        }

        refineConfiguration {
            onAnnotations(DependsOn::class, Repository::class) { context ->
                val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
                    ?: return@onAnnotations context.compilationConfiguration.asSuccess()
                return@onAnnotations runBlocking { resolver.resolveFromScriptSourceAnnotations(annotations) }.onSuccess {
                    context.compilationConfiguration.with { dependencies.append(JvmDependency(it)) }.asSuccess()
                }
            }
        }
    }
)