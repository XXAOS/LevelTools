package me.Hayden.LevelTools.events.blockbreak;

import me.Hayden.LevelTools.Main;
import me.Hayden.LevelTools.handlers.BlockBreakHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BlockBreakEvent_LOW implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public static void blockbreak(org.bukkit.event.block.BlockBreakEvent event) {
        if (event.isCancelled() == true) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getPlayer().getItemInHand().getType().toString().contains("_AXE") && Main.plugin.getConfig().getBoolean("axe.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "axe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_PICKAXE") && Main.plugin.getConfig().getBoolean("pickaxe.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "pickaxe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_SPADE") || player.getPlayer().getItemInHand().getType().toString().contains("_SHOVEL") && Main.plugin.getConfig().getBoolean("shovel.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "shovel");
            return;
        }
    }
}