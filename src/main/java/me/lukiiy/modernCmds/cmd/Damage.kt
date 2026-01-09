package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.Defaults
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.max

class Damage : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        if (strings.size < 2) return Defaults.argFail(commandSender, "damage [player] [amount]")

        val target = when {
            strings.isNotEmpty() -> Bukkit.getServer().getPlayer(strings.first())
            commandSender is Player -> commandSender
            else -> {
                commandSender.sendMessage(Defaults.NOT_FOUND)
                return true
            }
        }

        val damage = strings[1]?.toIntOrNull()?.coerceAtLeast(1) ?: 1

        target.damage(damage)
        commandSender.sendMessage("Applied $damage damage to ${target.name}")
        return true
    }
}