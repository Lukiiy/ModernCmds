package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.Defaults
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Weather : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        val world = (commandSender as? Player)?.world ?: Bukkit.getServer().worlds.first()

        when (strings.firstOrNull()) {
            "clear" -> {
                world.setStorm(false)
                commandSender.sendMessage("Set the weather to clear")
            }
            "rain" -> {
                world.setStorm(true)
                commandSender.sendMessage("Set the weather to rain")
            }
            "thunder" -> {
                world.setStorm(true)
                world.isThundering = true
                commandSender.sendMessage("Set the weather to thunder")
            }
            else -> Defaults.argFail(commandSender, "weather <clear|rain|thunder> [duration]")
        }

        return true
    }
}