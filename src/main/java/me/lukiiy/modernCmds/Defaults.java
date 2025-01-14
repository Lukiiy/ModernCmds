package me.lukiiy.modernCmds;

import org.bukkit.command.CommandSender;

public class Defaults {
    public static final String NOT_FOUND = "§cPlayer not found.";

    public static boolean argFail(CommandSender sender, String usage) {
        sender.sendMessage("§cUnknown or incomplete command. Try §7/" + usage);
        return true;
    }
}
