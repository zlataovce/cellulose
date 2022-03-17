package me.kcra.cellulose.script.internal

import kotlinx.coroutines.runBlocking
import me.kcra.cellulose.CellulosePlugin
import me.kcra.cellulose.CellulosePlugin.Companion.pluginContext
import me.kcra.cellulose.script.ScriptBase
import me.kcra.cellulose.script.annotation.CompilerOptions
import me.kcra.cellulose.script.annotation.Import
import java.security.MessageDigest
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.compilationCache
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache

private val resolver = CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver())

internal object CompilationConfiguration : ScriptCompilationConfiguration(
    {
        defaultImports(DependsOn::class, Repository::class)
        defaultImports.append(
            "me.kcra.cellulose.script.annotation.*",
            "me.kcra.cellulose.script.extension.*",
            "cloud.commandframework.kotlin.extension.*"
        )

        baseClass(KotlinType(ScriptBase::class))

        jvm {
            dependenciesFromClassloader(classLoader = CellulosePlugin::class.java.classLoader, wholeClasspath = true)
        }

        refineConfiguration {
            onAnnotations(DependsOn::class, Repository::class, Import::class, CompilerOptions::class) { context ->
                val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
                    ?: return@onAnnotations context.compilationConfiguration.asSuccess()

                val scriptsFolder = pluginContext { it.scriptsFolder }
                val importedSources = annotations.flatMap {
                    (it.annotation as? Import)?.paths?.map { sourceName ->
                        FileScriptSource(scriptsFolder.resolve(sourceName))
                    } ?: emptyList()
                }
                val compileOptions = annotations.flatMap {
                    (it.annotation as? CompilerOptions)?.options?.toList() ?: emptyList()
                }

                return@onAnnotations runBlocking { resolver.resolveFromScriptSourceAnnotations(annotations) }.onSuccess { deps ->
                    context.compilationConfiguration.with {
                        dependencies.append(JvmDependency(deps))
                        if (importedSources.isNotEmpty()) {
                            importScripts.append(importedSources)
                        }
                        if (compileOptions.isNotEmpty()) {
                            compilerOptions.append(compileOptions)
                        }
                    }.asSuccess()
                }
            }
        }

        hostConfiguration(ScriptingHostConfiguration {
            jvm {
                compilationCache(
                    CompiledScriptJarsCache { script, scriptCompilationConfiguration ->
                        pluginContext { it.compiledCacheFolder }.resolve(
                            compiledScriptUniqueName(script, scriptCompilationConfiguration) + ".jar"
                        )
                    }
                )
            }
        })
    }
)

private fun compiledScriptUniqueName(script: SourceCode, scriptCompilationConfiguration: ScriptCompilationConfiguration): String {
    val digestWrapper = MessageDigest.getInstance("MD5")
    digestWrapper.update(script.text.toByteArray())
    scriptCompilationConfiguration.notTransientData.entries
        .sortedBy { it.key.name }
        .forEach {
            digestWrapper.update(it.key.name.toByteArray())
            digestWrapper.update(it.value.toString().toByteArray())
        }
    return digestWrapper.digest().toHexString()
}

private fun ByteArray.toHexString(): String = joinToString("", transform = { "%02x".format(it) })
