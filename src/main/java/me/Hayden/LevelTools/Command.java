package me.Hayden.LevelTools;

import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import static org.bukkit.ChatColor.*;

public class Command implements CommandExecutor {
    static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    static FancyMessage about() {
        return new FancyMessage("   [VERSION]")
                .color(GREEN)
                .style(BOLD)
                .tooltip(chat("&7Version &2" + Main.plugin.getDescription().getVersion()))
                .then("            [HOOKS]")
                .color(RED)
                .style(BOLD)
                .tooltip(chat("&cAutoSell: " + Main.autosell_hook), chat("&eTokenEnchant " + Main.tokenenchant_hook), chat( "&bCrazyEnchant: " + Main.crazyenchant_hook));
    }
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String s, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("leveltools.reload")) {
            Main.plugin.reloadConfig();
            sender.sendMessage(chat("&aConfig reloaded"));
            return true;
        }

        sender.sendMessage(chat("                 &a&lLevelTools+ "));
        about().send(sender);
        return false;
    }
}
