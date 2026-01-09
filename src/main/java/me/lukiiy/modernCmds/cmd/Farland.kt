package me.lukiiy.modernCmds.cmd

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Farland : CommandExecutor {
    companion object {
        private const val DISTANCE = 30000000.0
    }

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        if (strings.isEmpty()) {
            commandSender.sendMessage("Usage: /farlands <n|s|e|w|ne|nw|se|sw> [player]")
            return true
        }

        val target: Player = when {
            strings.size >= 2 -> {
                Bukkit.getServer().getPlayer(strings[1]) ?: run {
                    commandSender.sendMessage("Player '${strings[1]}' not found.")
                    return true
                }
            }
            commandSender is Player -> commandSender
            else -> {
                commandSender.sendMessage("Specify a player!") // TODO
                return true
            }
        }

        val (x, z) = when (strings[0]!!.lowercase()) {
            "n", "north" -> 0.0 to -DISTANCE
            "s", "south" -> 0.0 to DISTANCE
            "e", "east" -> DISTANCE to 0.0
            "w", "west" -> -DISTANCE to 0.0
            "ne", "northeast" -> DISTANCE to -DISTANCE
            "nw", "northwest" -> -DISTANCE to -DISTANCE
            "se", "southeast" -> DISTANCE to DISTANCE
            "sw", "southwest" -> -DISTANCE to DISTANCE
            else -> {
                commandSender.sendMessage("§cInvalid direction. Use n/s/e/w/ne/nw/se/sw.")
                return true
            }
        }

        val world = target.world
        val y = world.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble() + 1

        target.teleport(Location(world, x, y, z, target.location.yaw, target.location.pitch))

        if (commandSender != target) commandSender.sendMessage("Teleported ${target.name} to the far lands.")
        target.sendMessage("You have been teleported to the Far Lands ($x, $z).")
        return true

    }
}