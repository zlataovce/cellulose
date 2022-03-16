# cellulose
A Bukkit plugin for scripting mini-plugins in Kotlin.

## Usage
Put a file with a `.cell.kts` extension in the plugins/Cellulose/scripts folder and start the server.  

**Example:**
```kotlin
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import cloud.commandframework.bukkit.parsers.PlayerArgument

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
    argument(PlayerArgument.of("player"))

    handler { context ->
        schedule(delay = 20, period = 0) { // period is optional
            context.get<Player>("player").sendMessage("boo")
        }
        scheduleAsync(delay = 40, period = 0) { // period is optional
            context.get<Player>("player").sendMessage("second boo")
        }
        scheduleSuspend(delay = 60, period = 0) { // period is optional
            // this is a suspending lambda
            context.get<Player>("player").sendMessage("third boo")
        }
    }
}
```