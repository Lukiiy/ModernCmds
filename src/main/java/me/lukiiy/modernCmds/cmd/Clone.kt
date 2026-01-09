package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.BlockSnapshot
import me.lukiiy.modernCmds.Bound
import me.lukiiy.modernCmds.Defaults
import me.lukiiy.modernCmds.Utils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Clone : CommandExecutor { // TODO
    private enum class Mask { REPLACE, MASKED, FILTERED }
    private enum class Mode { NORMAL, FORCE, MOVE }

    private val usage = "clone <corner1> <corner2> <destination> [replace|masked|filtered] [normal|force|move] [filter material[:data]]"
    private val maxBlocks = 32768

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String?>): Boolean {
        if (args.size < 9) return Defaults.argFail(sender, usage)

        val location = (sender as? Player)?.location ?: Location(Bukkit.getServer().worlds.first(), 0.0, 0.0, 0.0)
        val world = location.world!!

        val coords = args.slice(0..8).mapIndexed { index, coord ->
            when (index % 3) {
                0 -> coord?.let { Utils.parseCoordinates(it, location, Utils.COORD_AXIS.X) }?.minus(1)
                1 -> coord?.let { Utils.parseCoordinates(it, location, Utils.COORD_AXIS.Y) }
                else -> coord?.let { Utils.parseCoordinates(it, location, Utils.COORD_AXIS.Z) }
            }
        }
        if (coords.any { it == null }) return Defaults.argFail(sender, usage)

        val x1 = coords[0]!!.toInt()
        val y1 = coords[1]!!.toInt()
        val z1 = coords[2]!!.toInt()
        val x2 = coords[3]!!.toInt()
        val y2 = coords[4]!!.toInt()
        val z2 = coords[5]!!.toInt()
        val dX = coords[6]!!.toInt()
        val dY = coords[7]!!.toInt()
        val dZ = coords[8]!!.toInt()

        val mask = runCatching {
            Mask.valueOf(args.getOrNull(9)?.uppercase() ?: "REPLACE")
        }.getOrElse { return Defaults.argFail(sender, usage) }

        val mode = runCatching {
            Mode.valueOf(args.getOrNull(10)?.uppercase() ?: "NORMAL")
        }.getOrElse { return Defaults.argFail(sender, usage) }

        val filter = if (mask == Mask.FILTERED) Utils.basicItem(args.getOrNull(11) ?: return Defaults.argFail(sender, usage)) ?: return Defaults.argFail(sender, usage) else null

        val source = Bound(x1, y1, z1, x2, y2, z2)
        val w = source.maxX - source.minX + 1
        val h = source.maxY - source.minY + 1
        val l = source.maxZ - source.minZ + 1
        val total = w * h * l

        if (total > maxBlocks) {
            sender.sendMessage("§cToo many blocks in the specified area (maximum $maxBlocks, but specified $total)")
            return true
        }

        val dest = Bound(dX, dY, dZ, dX + w - 1, dY + h - 1, dZ + l - 1)
        val snapshots = ArrayList<BlockSnapshot>(total)

        source.iterator(world) { b ->
            if (mask == Mask.MASKED && b.type == Material.AIR) return@iterator false
            if (mask == Mask.FILTERED && (b.type != filter!!.type || b.data != (filter.data?.data ?: 0))) return@iterator false

            snapshots += BlockSnapshot(b.x - source.minX, b.y - source.minY, b.z - source.minZ, b.typeId, b.data)
            false
        }

        var changed = 0
        for (snap in snapshots) {
            val b = world.getBlockAt(dest.minX + snap.dX, dest.minY + snap.dY, dest.minZ + snap.dZ)
            if (b.typeId == snap.id && b.data == snap.data) continue

            b.setTypeIdAndData(snap.id, snap.data, false)
            changed++
        }

        if (mode == Mode.MOVE) {
            source.iterator(world) { b ->
                b.type = Material.AIR
                false
            }
        }

        sender.sendMessage(if (changed > 0) "Successfully cloned $changed block(s)" else "§cNo blocks were cloned")
        return true
    }
}