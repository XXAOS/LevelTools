package me.Hayden.LevelTools.events.blockbreak;

import me.Hayden.LevelTools.LevelTools;
import me.Hayden.LevelTools.handlers.BlockBreakHandler;
import me.clip.autosell.events.DropsToInventoryEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class AutoSell_BlockBreakEvent implements Listener {

    @EventHandler
    public static void event(DropsToInventoryEvent event) {
        Player player = event.getPlayer();
        if (player.getPlayer().getItemInHand().getType().toString().contains("_AXE") && LevelTools.plugin.getConfig().getBoolean("axe.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "axe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_PICKAXE") && LevelTools.plugin.getConfig().getBoolean("pickaxe.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "pickaxe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_SPADE") && LevelTools.plugin.getConfig().getBoolean("shovel.enabled") == true) {
            BlockBreakHandler.handle(event.getBlock(), player, "shovel");
            return;
        }
    }
}
