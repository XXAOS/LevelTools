package me.Hayden.LevelTools.events;

import com.cryptomorin.xseries.XMaterial;
import me.Hayden.LevelTools.LevelTools;
import me.Hayden.LevelTools.handlers.DamageItemHandler;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageEvent implements Listener {
    @EventHandler
    public void damage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {return;}
        //BOW
        if (event.getDamager().getType().toString().toLowerCase().contains("arrow") && (((Arrow) event.getDamager()).getShooter()) instanceof Player) {
            Player player = (Player) ((Arrow) event.getDamager()).getShooter();
            if (player.getItemInHand().getType() == XMaterial.matchXMaterial(Material.BOW).parseMaterial()) {
                if (LevelTools.plugin.getConfig().getBoolean("bow.onlyplayerdamage") == true) {
                    if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
                        return;
                    }
                }
                DamageItemHandler.handle(player, event.getFinalDamage(), "bow");
            }
        }

        //SWORD
        if (event.getDamager().getType().equals(EntityType.PLAYER)) {
            //RUN IF ATTACKER IS PLAYER
            Player player = (Player) event.getDamager();
            if (player.getItemInHand().getType().toString().contains("_SWORD")) {
                if (LevelTools.plugin.getConfig().getBoolean("sword.onlyplayerdamage") == true) {
                    if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
                        return;
                    }
                }
                DamageItemHandler.handle(player, event.getFinalDamage(), "sword");
            }
        }

        //CROSSBOW
        if (event.getDamager().getType().toString().toLowerCase().contains("arrow") && (((Arrow) event.getDamager()).getShooter()) instanceof Player) {
            Player player = (Player) ((Arrow) event.getDamager()).getShooter();
            if (player.getItemInHand().getType().toString().contains("CROSSBOW")) {
                if (LevelTools.plugin.getConfig().getBoolean("crossbow.onlyplayerdamage") == true) {
                    if (!event.getEntity().getType().equals(EntityType.PLAYER)) {
                        return;
                    }
                }
                DamageItemHandler.handle(player, event.getFinalDamage(), "crossbow");
            }
        }

    }
}
