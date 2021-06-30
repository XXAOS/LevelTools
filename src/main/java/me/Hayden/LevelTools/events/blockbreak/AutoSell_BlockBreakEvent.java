package me.Hayden.LevelTools.events.blockbreak;

import me.Hayden.LevelTools.Main;
import me.Hayden.LevelTools.handlers.BlockBreakHandler;
import me.clip.autosell.events.DropsToInventoryEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AutoSell_BlockBreakEvent implements Listener {

    @EventHandler
    public static void event(DropsToInventoryEvent event) {
        Player player = event.getPlayer();
        if (player.getPlayer().getItemInHand().getType().toString().contains("_AXE") && Main.plugin.getConfig().getBoolean("axe.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "axe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_PICKAXE") && Main.plugin.getConfig().getBoolean("pickaxe.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "pickaxe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_SPADE") && Main.plugin.getConfig().getBoolean("shovel.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "shovel");
            return;
        }
    }
}
