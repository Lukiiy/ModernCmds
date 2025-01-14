package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.Defaults
import me.lukiiy.modernCmds.Utils
import me.lukiiy.modernCmds.Utils.COORD_AXIS
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Setblock : CommandExecutor {
    private val usage = "setblock <x> <y> <z> <block_id> [mode]"

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        if (strings.size != 4) return Defaults.argFail(commandSender, usage)

        var location = (commandSender as? Player)?.location ?: Location(Bukkit.getServer().worlds.first(), 0.0, 0.0, 0.0)

        val x = strings[0]?.let { Utils.parseCoordinates(it, location, COORD_AXIS.X) }
        val y = strings[1]?.let { Utils.parseCoordinates(it, location, COORD_AXIS.Y) }
        val z = strings[2]?.let { Utils.parseCoordinates(it, location, COORD_AXIS.Z) }
        if (x != null && y != null && z != null) location = Location(location.world, x, y, z)

        val item = Utils.basicItem(strings[3].toString()) ?: run {
            commandSender.sendMessage("§cUnknown item '${strings[3]}'")
            return true
        }

        if (!item.type.isBlock) {
            commandSender.sendMessage("§cUnknown block type '${strings[3]}'")
            return true
        }

        val block = location.block
        if (block.type == item.type && block.data == (item?.data?.data ?: 0)) {
            commandSender.sendMessage("§cCould not set the block")
            return true
        }

        commandSender.sendMessage("Changed the block at ${location.blockX} ${location.blockY} ${location.blockZ}")
        block.setTypeIdAndData(item.typeId, item?.data?.data ?: 0, true)
        return true
    }
}