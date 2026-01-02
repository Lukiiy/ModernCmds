package me.lukiiy.modernCmds.cmd

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Seed : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        var msg = "Seed: §a"

        if (commandSender is Player) msg += commandSender.world.seed
        else msg += Bukkit.getServer().worlds[0].seed

        commandSender.sendMessage(msg)
        return true
    }
}