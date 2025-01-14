package me.lukiiy.modernCmds.cmd

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Toggledownfall : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        val world = (commandSender as? Player)?.world ?: Bukkit.getServer().worlds.first()

        world.weatherDuration = 1
        commandSender.sendMessage("Toggled downfall")
        return true
    }
}
