package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.Defaults
import me.lukiiy.modernCmds.Utils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Clear : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        val target = when {
            strings.isNotEmpty() -> Bukkit.getServer().getPlayer(strings.first())
            commandSender is Player -> commandSender
            else -> {
                commandSender.sendMessage(Defaults.NOT_FOUND)
                return true
            }
        }

        if (strings.size > 1) {
            val item = Utils.basicItem(strings[1].toString()) ?: run {
                commandSender.sendMessage("§cUnknown item '${strings[1]}'")
                return true
            }

            var toRemove = strings.getOrNull(2)?.toIntOrNull() ?: 0
            var deleted = 0

            target.inventory.contents.filterNotNull().forEach { invItem ->
                if (invItem.type == item.type && invItem.data == item.data) {
                    if (toRemove > 0) {
                        toRemove = minOf(invItem.amount, toRemove - deleted)
                        invItem.amount -= toRemove
                        deleted += toRemove

                        if (invItem.amount <= 0) target.inventory.removeItem(invItem)
                        if (deleted >= toRemove) return@forEach
                    } else {
                        deleted += invItem.amount
                        target.inventory.removeItem(invItem)
                    }
                }
            }

            commandSender.sendMessage("Removed $deleted item(s) from ${target.name}")
            return true
        }

        var deleted = 0
        target.inventory.contents.filterNotNull().forEach {
            deleted += it.amount
            target.inventory.removeItem(it)
        }
        commandSender.sendMessage("Removed $deleted item(s) from ${target.name}")
        return true
    }
}