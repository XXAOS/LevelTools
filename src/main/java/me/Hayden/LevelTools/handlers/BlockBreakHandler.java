package me.Hayden.LevelTools.handlers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.vk2gpz.tokenenchant.api.CEHandler;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import de.tr7zw.changeme.nbtapi.NBTItem;
import me.Hayden.LevelTools.Main;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockBreakHandler {
    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void handle(Block block, Player player, String nameinconfig) {
        Map<Enchantment, Integer> enchantstoadd = new HashMap<Enchantment, Integer>();
        int xptonextlevel = 0;
        //If a player is holding air?? stop him!!!
        if (player.getItemInHand() == null) {
            return;
        }

        if (Main.plugin.getConfig().getBoolean(nameinconfig + ".enabled") == true) {

            for (String s : Main.plugin.getConfig().getStringList(nameinconfig + ".disabled_worlds")) {
                if (player.getWorld().getName().equals(s)){ return; }
            }
            //ADD BLOCKS TO NBT
            NBTItem nbtitem = new NBTItem(player.getItemInHand());
            nbtitem.setInteger("blocks", nbtitem.getInteger("blocks") + 1);
            //ADD XP TO NBT


            for (String s : Main.plugin.getConfig().getStringList(nameinconfig + ".blocks")) {
                String[] split = s.split(":");
                Material b = XMaterial.matchXMaterial(split[0]).get().parseMaterial();
                if (block.getType() == b) {
                    nbtitem.setInteger("xp", nbtitem.getInteger("xp") + Integer.valueOf(split[1]));
                }
            }
            //LEVEL HANDLER
            for (String s : Main.plugin.getConfig().getConfigurationSection(nameinconfig + ".levels").getKeys(false)) {
                int xpneeded = Main.plugin.getConfig().getInt(nameinconfig + ".levels." + s + ".xp-needed");
                if (nbtitem.getInteger("xp") >= xpneeded) {
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
            if (Main.plugin.getConfig().contains(nameinconfig + ".levels." + (nbtitem.getInteger("level") + 1) + ".xp-needed")) {
                xptonextlevel = Main.plugin.getConfig().getInt(nameinconfig + ".levels." + (nbtitem.getInteger("level") + 1) + ".xp-needed");
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
                s = s.replace("%blocks%", Integer.toString(nbtitem.getInteger("blocks")));
                s = s.replace("%xp%", Integer.toString(nbtitem.getInteger("xp")));
                s = s.replace("%level%", Integer.toString(nbtitem.getInteger("level")));
                Integer xp = nbtitem.getInteger("xp");
                if (xptonextlevel == 0) { s = s.replace("%xp_needed%", color(Main.plugin.getConfig().getString("settings.maxlevel"))); } else { s = s.replace("%xp_needed%", Integer.toString(xptonextlevel)); }
                if (xptonextlevel != 0) {
                    int percentage = (xp * 100 + (xptonextlevel >> 1)) / xptonextlevel;
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

    public static void handle(List<Block> blocks, Player player, String nameinconfig) {

        Map<Enchantment, Integer> enchantstoadd = new HashMap<Enchantment, Integer>();
        int xptonextlevel = 0;
        int blocksbroken = 1;
        //If a player is holding air?? stop him!!!
        if (player.getItemInHand() == null) {
            return;
        }

        if (Main.plugin.getConfig().getBoolean(nameinconfig + ".enabled") == true) {
            for (String s : Main.plugin.getConfig().getStringList(nameinconfig + ".disabled_worlds")) {
                if (player.getWorld().getName().equals(s)){ return; }
            }
            for (Block block : blocks) {
                blocksbroken++;
            }

            //ADD BLOCKS TO NBT
            NBTItem nbtitem = new NBTItem(player.getItemInHand());
            nbtitem.setInteger("blocks", nbtitem.getInteger("blocks") + blocksbroken);
            //ADD XP TO NBT

            for (Block b : blocks) {
                for (String s : Main.plugin.getConfig().getStringList(nameinconfig + ".blocks")) {
                    String[] split = s.split(":");
                    Material block = XMaterial.matchXMaterial(split[0]).get().parseMaterial();
                    if (block == b.getType()) {
                        nbtitem.setInteger("xp", nbtitem.getInteger("xp") + Integer.valueOf(split[1]));
                    }
                }
            }
            //LEVEL HANDLER
            for (String s : Main.plugin.getConfig().getConfigurationSection(nameinconfig + ".levels").getKeys(false)) {
                int xpneeded = Main.plugin.getConfig().getInt(nameinconfig + ".levels." + s + ".xp-needed");
                if (nbtitem.getInteger("xp") >= xpneeded) {
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
            if (Main.plugin.getConfig().contains(nameinconfig + ".levels." + (nbtitem.getInteger("level") + 1) + ".xp-needed")) {
                xptonextlevel = Main.plugin.getConfig().getInt(nameinconfig + ".levels." + (nbtitem.getInteger("level") + 1) + ".xp-needed");
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
            //Handle Crazyenchant lores
            Map<CEnchantment, Integer> crazyenchant = new HashMap<CEnchantment, Integer>();
            if (Main.crazyenchant_hook == true) {
                CrazyEnchantments api = CrazyEnchantments.getInstance();
                Map<CEnchantment, Integer> enchantments = api.getEnchantments(player.getItemInHand());
                List<String> telore = new ArrayList<>();
                enchantments.forEach((enc, level) -> {
                    String lore = enc.getCustomName();
                    crazyenchant.put(enc, enc.getLevel(player.getItemInHand()));
                });
            }
            //Apply nbt to ore
            for (String s : Main.plugin.getConfig().getStringList(nameinconfig + ".lore")) {
                s = s.replace("%blocks%", Integer.toString(nbtitem.getInteger("blocks")));
                s = s.replace("%xp%", Integer.toString(nbtitem.getInteger("xp")));
                s = s.replace("%level%", Integer.toString(nbtitem.getInteger("level")));
                Integer xp = nbtitem.getInteger("xp");
                if (xptonextlevel == 0) { s = s.replace("%xp_needed%", color(Main.plugin.getConfig().getString("settings.maxlevel"))); } else { s = s.replace("%xp_needed%", Integer.toString(xptonextlevel)); }
                if (xptonextlevel != 0) {
                    int percentage = (xp * 100 + (xptonextlevel >> 1)) / xptonextlevel;
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
}
