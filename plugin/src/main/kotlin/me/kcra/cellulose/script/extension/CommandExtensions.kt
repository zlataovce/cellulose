@file:Suppress("unused") // for use in scripts

package me.kcra.cellulose.script.extension

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.standard.DoubleArgument
import cloud.commandframework.arguments.standard.IntegerArgument
import cloud.commandframework.arguments.standard.LongArgument
import cloud.commandframework.arguments.standard.ShortArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.arguments.standard.StringArgument.StringMode
import cloud.commandframework.bukkit.parsers.PlayerArgument
import cloud.commandframework.kotlin.MutableCommandBuilder
import me.kcra.cellulose.CellulosePlugin.Companion.ensureEnabled
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun command(
    name: String,
    description: String = "",
    aliases: Array<String> = emptyArray(),
    block: MutableCommandBuilder<CommandSender>.() -> Unit,
) = ensureEnabled { plugin ->
    MutableCommandBuilder(name, ArgumentDescription.of(description), aliases, plugin.commandManager, block).register()
}

// arguments

class CommandArgumentConfigurer {
    internal val args: MutableSet<Pair<CommandArgument<CommandSender, *>, ArgumentDescription>> = mutableSetOf()

    fun string(
        name: String,
        description: String = "",
        optional: Boolean = false,
        default: String? = null,
        mode: StringMode = StringMode.QUOTED,
        suggestionsProvider: () -> List<String> = { listOf() },
    ) = args.add(Pair(StringArgument.newBuilder<CommandSender>(name).also { builder ->
        if (optional) {
            if (default != null) {
                builder.asOptionalWithDefault(default)
            } else {
                builder.asOptional()
            }
        }
        when (mode) {
            StringMode.SINGLE -> builder.single()
            StringMode.GREEDY -> builder.greedy()
            StringMode.QUOTED -> builder.quoted()
        }
    }.withSuggestionsProvider { _, _ -> suggestionsProvider() }.build(), ArgumentDescription.of(description)))

    fun integer(
        name: String,
        description: String = "",
        optional: Boolean = false,
        default: Int? = null,
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE,
        suggestionsProvider: () -> List<String> = { listOf() },
    ) = args.add(Pair(IntegerArgument.newBuilder<CommandSender>(name).also { builder ->
        if (optional) {
            if (default != null) {
                builder.asOptionalWithDefault(default)
            } else {
                builder.asOptional()
            }
        }
    }.withMin(min).withMax(max).withSuggestionsProvider { _, _ -> suggestionsProvider() }.build(),
        ArgumentDescription.of(description)))

    fun short(
        name: String,
        description: String = "",
        optional: Boolean = false,
        default: Short? = null,
        min: Short = Short.MIN_VALUE,
        max: Short = Short.MAX_VALUE,
        suggestionsProvider: () -> List<String> = { listOf() },
    ) = args.add(Pair(ShortArgument.newBuilder<CommandSender>(name).also { builder ->
        if (optional) {
            if (default != null) {
                builder.asOptionalWithDefault(default)
            } else {
                builder.asOptional()
            }
        }
    }.withMin(min).withMax(max).withSuggestionsProvider { _, _ -> suggestionsProvider() }.build(),
        ArgumentDescription.of(description)))

    fun double(
        name: String,
        description: String = "",
        optional: Boolean = false,
        default: Double? = null,
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE,
        suggestionsProvider: () -> List<String> = { listOf() },
    ) = args.add(Pair(DoubleArgument.newBuilder<CommandSender>(name).also { builder ->
        if (optional) {
            if (default != null) {
                builder.asOptionalWithDefault(default)
            } else {
                builder.asOptional()
            }
        }
    }.withMin(min).withMax(max).withSuggestionsProvider { _, _ -> suggestionsProvider() }.build(),
        ArgumentDescription.of(description)))

    fun long(
        name: String,
        description: String = "",
        optional: Boolean = false,
        default: Long? = null,
        min: Long = Long.MIN_VALUE,
        max: Long = Long.MAX_VALUE,
        suggestionsProvider: () -> List<String> = { listOf() },
    ) = args.add(Pair(LongArgument.newBuilder<CommandSender>(name).also { builder ->
        if (optional) {
            if (default != null) {
                builder.asOptionalWithDefault(default)
            } else {
                builder.asOptional()
            }
        }
    }.withMin(min).withMax(max).withSuggestionsProvider { _, _ -> suggestionsProvider() }.build(),
        ArgumentDescription.of(description)))

    fun player(
        name: String,
        description: String = "",
        optional: Boolean = false,
        suggestionsProvider: () -> List<String> = { Bukkit.getOnlinePlayers().map { it.name } },
    ) = args.add(Pair(PlayerArgument.newBuilder<CommandSender>(name).also { if (optional) it.asOptional() }
        .withSuggestionsProvider { _, _ -> suggestionsProvider() }.build(), ArgumentDescription.of(description)))
}

fun MutableCommandBuilder<CommandSender>.arguments(block: CommandArgumentConfigurer.() -> Unit) =
    CommandArgumentConfigurer().also(block).args.forEach { argument(it.first, it.second) }