package me.lukiiy.modernCmds.cmd

import me.lukiiy.modernCmds.Defaults
import org.bukkit.Achievement
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Achievements : CommandExecutor {
    private val usage = "achievement <give|list> <player> <achievementId|*>"

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String?>): Boolean {
        if (strings.isEmpty()) return Defaults.argFail(commandSender, usage)

        when (strings.firstOrNull()) {
            "give" -> {
                if (strings.size < 3) return Defaults.argFail(commandSender, usage)

                val target = Bukkit.getServer().getPlayer(strings[1]) ?: run {
                    commandSender.sendMessage(Defaults.NOT_FOUND)
                    return true
                }

                val id = strings[2].toString()
                if (id == "*") {
                    Achievement.entries.forEach(target::awardAchievement)
                    commandSender.sendMessage("Granted every achievement to ${target.name}")
                    return true
                }

                val achievement = try {
                    id.toIntOrNull()?.let { Achievement.getAchievement(it) } ?: Achievement.valueOf(id.uppercase())
                } catch (_: IllegalArgumentException) {
                    commandSender.sendMessage("§cUnknown achievement: $id")
                    return true
                }

                target.awardAchievement(achievement)
                commandSender.sendMessage("Granted the achievement §a[${achievement.name}]§f to ${target.name}")
            }
            "list" -> {
                commandSender.sendMessage("§2Achievement IDs list:")
                Achievement.entries.forEach { commandSender.sendMessage("${it.name} (${it.id})") }
            }
            else -> Defaults.argFail(commandSender, usage)
        }

        return true
    }
}