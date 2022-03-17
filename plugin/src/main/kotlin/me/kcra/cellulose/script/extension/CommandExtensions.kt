package me.kcra.cellulose.script.extension

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.kotlin.MutableCommandBuilder
import me.kcra.cellulose.CellulosePlugin.Companion.ensureEnabled
import org.bukkit.command.CommandSender

fun command(
    name: String,
    description: String = "",
    aliases: Array<String> = emptyArray(),
    block: MutableCommandBuilder<CommandSender>.() -> Unit
) = ensureEnabled { plugin ->
    MutableCommandBuilder(
        name,
        ArgumentDescription.of(description),
        aliases,
        plugin.commandManager,
        block
    ).register()
}