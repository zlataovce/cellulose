# cellulose
A Bukkit plugin for scripting mini-plugins in Kotlin.

## Usage
Put a file with a `.cell.kts` extension in the plugins/Cellulose/scripts folder and start the server.  

**Example:**
```kotlin
@file:Repository("https://repo.kcra.me/snapshots")
@file:DependsOn("me.kcra.acetylene:srgutils:0.0.2-SNAPSHOT")
@file:Import("other_script_in_scripts_folder.kts")
@file:CompilerOptions("-verbose")

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import cloud.commandframework.arguments.standard.StringArgument.StringMode

load {
    log.info("Testing script loaded")
}

enable {
    log.info("Testing script enabled")
}

disable {
    log.info("Testing script disabled")
}

listen<PlayerJoinEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = true) { event ->
    log.info("A player joined: ${event.player.name}")
}

command(name = "test", description = "A testing command", aliases = ["testalias"]) {
    arguments {
        string("arg1", description = "First argument", optional = false, default = null, mode = StringMode.QUOTED) {
            listOf("first argument suggestion", "second argument suggestion")
        }
        integer("arg2", description = "Second argument", optional = false, default = null, min = 0, max = 1) {
            listOf("0", "1")
        }
        short("arg3", description = "Third argument", optional = false, default = null, min = 0, max = 1) {
            listOf("0", "1")
        }
        double("arg4", description = "Fourth argument", optional = false, default = null, min = 0, max = 1) {
            listOf("0.1", "0.9", "1")
        }
        long("arg5", description = "Fifth argument", optional = true, default = 1, min = 0, max = 1) {
            listOf("0", "1")
        }
        player("player", description = "Sixth argument", optional = false, default = null) {
            Bukkit.getOnlinePlayers().map { it.name }
        }
    }

    handler { context ->
        schedule(delay = 20, period = 0) {
            context.get<Player>("player").sendMessage("boo")
        }
        scheduleAsync(delay = 40, period = 0) {
            context.get<Player>("player").sendMessage("second boo")
        }
        scheduleSuspend(delay = 60, period = 0) {
            context.get<Player>("player").sendMessage("third boo")
        }
    }
}
```