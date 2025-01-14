package me.lukiiy.modernCmds;

import me.lukiiy.modernCmds.cmd.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ModernCmds extends JavaPlugin {
    private static ModernCmds instance;
    public static ModernCmds getInstance() {return instance;}

    @Override
    public void onEnable() {
        instance = this;

        getCommand("achievement").setExecutor(new Achievements());
        getCommand("clear").setExecutor(new Clear());
        getCommand("damage").setExecutor(new Damage());
        getCommand("fill").setExecutor(new Fill());
        getCommand("setblock").setExecutor(new Setblock());
        getCommand("summon").setExecutor(new Summon());
        getCommand("toggledownfall").setExecutor(new Toggledownfall());
        getCommand("weather").setExecutor(new Weather());
    }

    @Override public void onDisable() {}
}
