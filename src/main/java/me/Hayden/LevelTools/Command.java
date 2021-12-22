package me.Hayden.LevelTools;

import me.badbones69.crazyenchantments.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command implements CommandExecutor {
    static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String s, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("leveltools.reload")) {
            LevelTools.plugin.reloadConfig();
            sender.sendMessage(chat("&aConfig reloaded"));
            return true;
        }

        sender.sendMessage(chat("                 &a&lLevelTools+ "));
        sender.sendMessage(chat("&cAutoSell: " + LevelTools.autosell_hook)+" "+chat("&eTokenEnchant " + LevelTools.tokenenchant_hook)+" "+ chat( "&bCrazyEnchant: " + LevelTools.crazyenchant_hook) + " &2Version:" + LevelTools.plugin.getDescription().getVersion());
        return false;
    }
}
