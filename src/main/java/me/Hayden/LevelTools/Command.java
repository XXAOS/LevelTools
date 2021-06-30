package me.Hayden.LevelTools;

import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class Command implements CommandExecutor {
    static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    static JSONMessage about() {
        return JSONMessage.create("   [VERSION]")
                .color(GREEN)
                .style(BOLD)
                .tooltip(chat("&7Version &2" + Main.plugin.getDescription().getVersion()))
                .then("            [HOOKS]")
                .color(RED)
                .style(BOLD)
                .tooltip(chat("&cAutoSell: " + Main.autosell_hook)+"\n"+chat("&eTokenEnchant " + Main.tokenenchant_hook)+"\n"+ chat( "&bCrazyEnchant: " + Main.crazyenchant_hook));
    }
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String s, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("leveltools.reload")) {
            Main.plugin.reloadConfig();
            sender.sendMessage(chat("&aConfig reloaded"));
            return true;
        }

        sender.sendMessage(chat("                 &a&lLevelTools+ "));
        about().send((Player) sender);
        return false;
    }
}
