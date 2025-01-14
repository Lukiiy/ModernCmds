package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.Defaults
import me.lukiiy.modernCmds.Utils.COORD_AXIS
import me.lukiiy.modernCmds.Utils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Fill : CommandExecutor { // TODO: What... is... this?
    private enum class Mode {
        FILL, HOLLOW, KEEP, OUTLINE, REPLACE
    }

    private val usage = "fill <from> <to> <block_id> [${Mode.entries.drop(1).joinToString("|").lowercase()}]"

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        if (strings.size < 7) return Defaults.argFail(commandSender, usage)

        val location = (commandSender as? Player)?.location ?: Location(Bukkit.getServer().worlds.first(), 0.0, 0.0, 0.0)

        val coords = strings.slice(0..5).mapIndexed { index, coord ->
            when (index % 3) {
                0 -> coord?.let { Utils.parseCoordinates(it, location, COORD_AXIS.X) }?.minus(1)
                1 -> coord?.let { Utils.parseCoordinates(it, location, COORD_AXIS.Y) }
                else -> coord?.let { Utils.parseCoordinates(it, location, COORD_AXIS.Z) }
            }
        }
        if (coords.any { it == null }) return Defaults.argFail(commandSender, usage)

        val newBlock = Utils.basicItem(strings[6].toString()) ?: run {
            commandSender.sendMessage("§cUnknown item '${strings[6]}'")
            return true
        }
        if (!newBlock.type.isBlock) {
            commandSender.sendMessage("§cUnknown block type '${strings[6]}'")
            return true
        }

        val mode = try {
            Mode.valueOf(strings.getOrNull(7)?.uppercase() ?: "FILL")
        } catch (_: IllegalArgumentException) {
            return Defaults.argFail(commandSender, usage)
        }

        val bound = Bound(coords[0]!!.toInt(), coords[1]!!.toInt(), coords[2]!!.toInt(), coords[3]!!.toInt(), coords[4]!!.toInt(), coords[5]!!.toInt())

        val blocksChanged = when (mode) {
            Mode.REPLACE -> {
                val toReplace = Utils.basicItem(strings[8].toString()) ?: run {
                    commandSender.sendMessage("§cUnknown item '${strings[8]}'")
                    return true
                }
                if (!toReplace.type.isBlock) {
                    commandSender.sendMessage("§cUnknown block type '${strings[8]}'")
                    return true
                }

                val replaceType = toReplace.type
                val replaceData = toReplace.data?.data ?: 0.toByte()
                val newBlockData = newBlock.data?.data ?: 0.toByte()

                bound.iterator(location.world) {
                    if (it.type == replaceType && it.data == replaceData) {
                        it.setTypeIdAndData(newBlock.typeId, newBlockData, true)
                        true
                    } else false
                }
            }

            else -> {
                val newBlockData = newBlock.data?.data ?: 0.toByte()

                bound.iterator(location.world) { block ->
                    when {
                        mode == Mode.KEEP && block.type != Material.AIR -> false
                        mode == Mode.HOLLOW && bound.isIn(block) -> false
                        mode == Mode.OUTLINE && bound.isIn(block) -> {
                            block.type = Material.AIR
                            false
                        }
                        block.type == newBlock.type && block.data == newBlockData -> false
                        else -> {
                            block.setTypeIdAndData(newBlock.typeId, newBlockData, false)
                            true
                        }
                    }
                }
            }
        }

        commandSender.sendMessage(if (blocksChanged > 0) "Successfully filled $blocksChanged block(s)" else "§cNo blocks were filled")
        return true
    }

    private class Bound(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
        val minX = minOf(x1, x2)
        val maxX = maxOf(x1, x2)
        val minY = minOf(y1, y2)
        val maxY = maxOf(y1, y2)
        val minZ = minOf(z1, z2)
        val maxZ = maxOf(z1, z2)

        fun isIn(block: Block): Boolean {
            return block.x in (minX + 1) until maxX && block.y in (minY + 1) until maxY && block.z in (minZ + 1) until maxZ
        }

        fun iterator(world: World, action: (Block) -> Boolean): Int {
            var changed = 0
            for (x in minX..maxX)
                for (y in minY..maxY)
                    for (z in minZ..maxZ) {
                        if (action(world.getBlockAt(x, y, z))) changed++
                    }
            return changed
        }
    }
}