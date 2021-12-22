package me.Hayden.LevelTools;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import me.Hayden.LevelTools.events.blockbreak.*;
import me.Hayden.LevelTools.events.DamageEvent;
import me.Hayden.LevelTools.other.Metrics;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelTools extends JavaPlugin {
    public static LevelTools plugin;
    //HOOK
    public static boolean autosell_hook;
    public static boolean tokenenchant_hook;
    public static boolean crazyenchant_hook;
    //API
    public static CrazyEnchantments crazyenchant_api;
    public static TokenEnchantAPI tokenenchant_api;

    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        int pluginId = 11581;
        Metrics metrics = new Metrics(this, pluginId);
        getCommand("leveltools").setExecutor(new Command());

        plugin = this;
        Bukkit.getPluginManager().registerEvents(new DamageEvent(), this);
        if (Bukkit.getPluginManager().getPlugin("TokenEnchant") != null && plugin.getConfig().getBoolean("settings.tokenenchant-hook.enabled") == true) {
            Bukkit.getPluginManager().registerEvents(new TokenEnchant_BlockBreakEvent(), this);
            tokenenchant_hook = true;
            tokenenchant_api = TokenEnchantAPI.getInstance();
        }
        if (Bukkit.getPluginManager().getPlugin("CrazyEnchantments") != null && plugin.getConfig().getBoolean("settings.crazyenchant-hook.enabled") == true) {
            crazyenchant_hook = true;
        }
        if (Bukkit.getPluginManager().getPlugin("AutoSell") != null && plugin.getConfig().getBoolean("settings.autosell-hook") == true) {
            Bukkit.getPluginManager().registerEvents(new AutoSell_BlockBreakEvent(), this);
            autosell_hook = true;
        } else {
            String priority = LevelTools.plugin.getConfig().getString("advanced.blockbreak-event-priority");
            if (priority.equals("HIGH")) {Bukkit.getPluginManager().registerEvents(new BlockBreakEvent_HIGH(), this);System.out.println("LevelTools block break priority set to "+ priority);}
            if (priority.equals("HIGHEST")) {Bukkit.getPluginManager().registerEvents(new BlockBreakEvent_HIGHEST(), this);System.out.println("LevelTools block break priority set to "+ priority);}
            if (priority.equals("NORMAL")) {Bukkit.getPluginManager().registerEvents(new BlockBreakEvent_NORMAL(), this);System.out.println("LevelTools block break priority set to "+ priority);}
            if (priority.equals("LOW")) {Bukkit.getPluginManager().registerEvents(new BlockBreakEvent_LOW(), this);System.out.println("LevelTools block break priority set to "+ priority);}
            if (priority.equals("LOWEST")) {Bukkit.getPluginManager().registerEvents(new BlockBreakEvent_LOWEST(), this);System.out.println("LevelTools block break priority set to "+ priority);}
            if (priority.equals("MONITOR")) {Bukkit.getPluginManager().registerEvents(new BlockBreakEvent_MONITOR(), this);System.out.println("LevelTools block break priority set to "+ priority);}

        }
    }


}
