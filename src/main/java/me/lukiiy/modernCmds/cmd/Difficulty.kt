package me.lukiiy.modernCmds.cmd

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player

class Difficulty : CommandExecutor {
    private val intToDiff = mapOf(
        0 to "Peaceful",
        1 to "Easy",
        2 to "Normal",
        3 to "Hard"
    )

    private val diffToInt = intToDiff.entries.associate { it.value.lowercase() to it.key }

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        val world = if (commandSender is Player) commandSender.world else Bukkit.getServer().worlds[0]
        val nmsWorld = (world as CraftWorld).handle

        if (strings.isEmpty()) {
            commandSender.sendMessage("The difficulty is ${intToDiff[nmsWorld.spawnMonsters]}")
            return true
        }

        val difficulty = diffToInt[strings[0]!!.lowercase()] ?: run {
            commandSender.sendMessage("§cInvalid difficulty!")
            return true
        }

        val cannonName = intToDiff[difficulty]

        if (nmsWorld.spawnMonsters == difficulty) {
            commandSender.sendMessage("The difficulty did not change; it is already set to $cannonName")
            return true
        }

        commandSender.sendMessage("The difficulty has been set to $cannonName")
        nmsWorld.spawnMonsters = difficulty
        return true
    }
}