package me.kcra.cellulose.script.extension

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.kotlin.MutableCommandBuilder
import me.kcra.cellulose.CellulosePlugin
import org.bukkit.command.CommandSender

fun command(
    name: String,
    description: String = "",
    aliases: Array<String> = emptyArray(),
    block: MutableCommandBuilder<CommandSender>.() -> Unit
) {
    CellulosePlugin.INSTANCE?.registerCommand {
        MutableCommandBuilder(
            name,
            ArgumentDescription.of(description),
            aliases,
            it,
            block
        ).build()
    }
}