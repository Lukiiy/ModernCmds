package me.lukiiy.modernCmds

import org.bukkit.World
import org.bukkit.block.Block
import java.util.stream.IntStream

class Bound(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
    val minX = minOf(x1, x2)
    val maxX = maxOf(x1, x2)
    val minY = minOf(y1, y2)
    val maxY = maxOf(y1, y2)
    val minZ = minOf(z1, z2)
    val maxZ = maxOf(z1, z2)

    fun isIn(block: Block): Boolean = block.x in (minX + 1) until maxX && block.y in (minY + 1) until maxY && block.z in (minZ + 1) until maxZ

    fun iterator(world: World, action: (Block) -> Boolean): Int =
        IntStream.rangeClosed(minX, maxX).boxed().flatMap { x -> IntStream.rangeClosed(minY, maxY).boxed().flatMap { y -> IntStream.rangeClosed(minZ, maxZ).mapToObj { z -> Triple(x, y, z) } } }.mapToInt { (x, y, z) ->
            if (action(world.getBlockAt(x, y, z))) 1 else 0
        }.sum()
}