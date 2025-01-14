package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.Defaults
import me.lukiiy.modernCmds.Utils
import me.lukiiy.modernCmds.Utils.COORD_AXIS
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.CreatureType
import org.bukkit.entity.Player

class Summon : CommandExecutor {
    private val usage = "summon <entity> [x] [y] [z]"

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        if (strings.isEmpty() || (commandSender !is Player && strings.size < 4)) return Defaults.argFail(commandSender, usage)

        val typeArg = strings[0]!!.toString()
        val entityType = try {
            CreatureType.valueOf(typeArg.uppercase())
        } catch (_: IllegalArgumentException) {
            commandSender.sendMessage("§cCan't find element '${typeArg}' of type 'creature_type'")
            return true
        }

        var location = if (commandSender is Player) commandSender.location else Location(Bukkit.getServer().worlds.first(), 0.0, 0.0, 0.0)

        if (strings.size > 1) {
            val x = strings[1]?.let { Utils.parseCoordinates(it, location, COORD_AXIS.X) }
            val y = strings[2]?.let { Utils.parseCoordinates(it, location, COORD_AXIS.Y) }
            val z = strings[3]?.let { Utils.parseCoordinates(it, location, COORD_AXIS.Z) }
            if (x != null && y != null && z != null) location = Location(location.world, x, y, z)
        }

        commandSender.sendMessage("Summoned new " + entityType.name)
        location.world.spawnCreature(location, entityType)
        return true
    }
}