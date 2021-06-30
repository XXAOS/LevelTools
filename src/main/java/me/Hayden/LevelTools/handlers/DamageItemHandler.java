package me.Hayden.LevelTools.handlers;

import com.cryptomorin.xseries.XEnchantment;
import com.vk2gpz.tokenenchant.api.CEHandler;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.Hayden.LevelTools.Main;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageItemHandler {
    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void handle(Player player, double damage, String nameinconfig) {
        Map<Enchantment, Integer> enchantstoadd = new HashMap<Enchantment, Integer>();
        int damagetonextlevel = 0;
        //If a player is holding air?? stop him!!!
        if (player.getItemInHand() == null) {
            return;
        }

        if (!Main.plugin.getConfig().getBoolean(nameinconfig + ".enabled") == true) {
            return;
        }
        for (String s : Main.plugin.getConfig().getStringList(nameinconfig + ".disabled_worlds")) {
            if (player.getWorld().getName().equals(s)){ return; }
        }

        NBTItem nbtitem = new NBTItem(player.getItemInHand());
        //ADD DAMAGE
        nbtitem.setInteger("damage", nbtitem.getInteger("damage") + Integer.valueOf((int) damage));
        //LEVEL HANDLER
        for (String s : Main.plugin.getConfig().getConfigurationSection(nameinconfig + ".levels").getKeys(false)) {
            int damageneeded = Main.plugin.getConfig().getInt(nameinconfig + ".levels." + s + ".damage-needed");
            if (nbtitem.getInteger("damage") >= damageneeded) {
                Integer i = nbtitem.getInteger("level");
                Integer j = Integer.valueOf(s);
                if (i.equals(j)) {
                    continue;
                }
                if (i > j) {
                    continue;
                }
                nbtitem.setInteger("level", Integer.valueOf(s));
                for (String reward : Main.plugin.getConfig().getStringList(nameinconfig + ".levels." + s + ".rewards")) {
                    String[] splits = reward.split(" ", 2);
                    String prefix = splits[0];
                    if (prefix.equalsIgnoreCase("[cmd]")) {
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), splits[1]
                                .replace("%player%", player.getName()));
                    }
                    if (prefix.equalsIgnoreCase("[message]")) {
                        player.sendMessage(color(splits[1].replace("%player%", player.getName())));
                    }
                    if (prefix.equalsIgnoreCase("[enchant]")) {
                        String[] splitench = splits[1].split(" ");
                        enchantstoadd.put(XEnchantment.matchXEnchantment(splitench[0]).get().parseEnchantment(), Integer.valueOf(splitench[1]));
                    }
                }
            }
        }
        //APPLY NBT TO LORE
        if (Main.plugin.getConfig().contains(nameinconfig + ".levels." + (nbtitem.getInteger("level") + 1) + ".damage-needed")) {
            damagetonextlevel = Main.plugin.getConfig().getInt(nameinconfig + ".levels." + (nbtitem.getInteger("level") + 1) + ".damage-needed");
        }
        ItemMeta meta = nbtitem.getItem().getItemMeta();
        List<String> newlore = new ArrayList<>();

        //Handle tokenenchant enchants/lores and whatever
        if (Main.tokenenchant_hook == true) {
            TokenEnchantAPI api = TokenEnchantAPI.getInstance();
            Map<CEHandler, Integer> enchantments = api.getEnchantments(player.getItemInHand());
            List<String> telore = new ArrayList<>();
            enchantments.forEach((enc, level) -> {
                if (enc.isVanilla()) {
                    return;
                }
                String lore = enc.getLoreEntry(level, true, false, "");
                newlore.add(color(Main.plugin.getConfig().getString("settings.tokenenchant-hook.lore.prefix") + lore));
            });
        }
        Map<CEnchantment, Integer> crazyenchant = new HashMap<CEnchantment, Integer>();
        if (Main.crazyenchant_hook == true) {
            CrazyEnchantments api = CrazyEnchantments.getInstance();
            Map<CEnchantment, Integer> enchantments = api.getEnchantments(player.getItemInHand());
            List<String> telore = new ArrayList<>();
            enchantments.forEach((enc, level) -> {
                String lore = enc.getCustomName();
                crazyenchant.put(enc, enc.getLevel(player.getItemInHand()));
                newlore.add(color(Main.plugin.getConfig().getString("settings.crazyenchant-hook.lore.prefix") + lore));
            });
        }
        //Apply nbt to ore
        for (String s : Main.plugin.getConfig().getStringList(nameinconfig + ".lore")) {
            s = s.replace("%damage%", Integer.toString(nbtitem.getInteger("damage")));
            s = s.replace("%level%", Integer.toString(nbtitem.getInteger("level")));
            Integer dmg = nbtitem.getInteger("damage");
            if (damagetonextlevel == 0) { s = s.replace("%damage_needed%", color(Main.plugin.getConfig().getString("settings.maxlevel"))); } else { s = s.replace("%damage_needed%", Integer.toString(damagetonextlevel)); }
            if (damagetonextlevel != 0) {
                int percentage = (dmg * 100 + (damagetonextlevel >> 1)) / damagetonextlevel;
                s = s.replace("%percentage%", Integer.toString(percentage));
            } else {
                s = s.replace("%percentage%", Integer.toString(100));
            }
            newlore.add(color(s));
        }
        meta.setLore(newlore);
        //Check if a enchant level needs to be added

        for (Map.Entry<Enchantment, Integer> entry : enchantstoadd.entrySet()) {
            Enchantment e = entry.getKey();
            Integer currentench = player.getItemInHand().getEnchantmentLevel(e);
            meta.addEnchant(e, currentench + entry.getValue(), true);
        }

        //set meta and apply nbt to item in players hand
        nbtitem.getItem().setItemMeta(meta);
        nbtitem.applyNBT(player.getItemInHand());
        for (Map.Entry<CEnchantment, Integer> entry : crazyenchant.entrySet()) {
            CrazyEnchantments api = CrazyEnchantments.getInstance();
            CEnchantment e = entry.getKey();
            Integer currentench = api.getLevel(player.getItemInHand(), e);
            api.addEnchantment(player.getItemInHand(), e, currentench + entry.getValue());
        }
    }

}
