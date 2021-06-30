package me.Hayden.LevelTools.events.blockbreak;

import com.vk2gpz.tokenenchant.event.TEBlockExplodeEvent;
import me.Hayden.LevelTools.Main;
import me.Hayden.LevelTools.handlers.BlockBreakHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TokenEnchant_BlockBreakEvent implements Listener {

    @EventHandler
    public static void blockbreak(TEBlockExplodeEvent event) {
        Player player = event.getPlayer();
        if (player.getPlayer().getItemInHand().getType().toString().contains("_AXE") && Main.plugin.getConfig().getBoolean("axe.enabled") == true) {
            BlockBreakHandler.handle(event.blockList(), player, "axe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_PICKAXE") && Main.plugin.getConfig().getBoolean("pickaxe.enabled") == true) {
            BlockBreakHandler.handle(event.blockList(), player, "pickaxe");
            return;
        }
        if (player.getPlayer().getItemInHand().getType().toString().contains("_SPADE") && Main.plugin.getConfig().getBoolean("shovel.enabled") == true) {
            BlockBreakHandler.handle(event.blockList(), player, "spade");
            return;
        }
    }
}
