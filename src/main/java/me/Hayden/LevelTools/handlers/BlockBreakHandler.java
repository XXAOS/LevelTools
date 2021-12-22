package me.Hayden.LevelTools.handlers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.vk2gpz.tokenenchant.api.CEHandler;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.Hayden.LevelTools.LevelTools;
import me.Hayden.LevelTools.other.ProgressBar;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockBreakHandler {
    private static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void handle(Block block, Player player, String nameinconfig) {
        Map<Enchantment, Integer> enchantstoadd = new HashMap<>();
        Map<CEnchantment, Integer> ce_enchantstoadd = new HashMap<>();
        int xptonextlevel = 0;
        if (player.getItemInHand() == null)
            return;
        if (LevelTools.plugin.getConfig().getBoolean(nameinconfig + ".enabled") == true) {
            for (String s : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".disabled_worlds")) {
                if (player.getWorld().getName().equals(s))
                    return;
            }
            NBTItem nbtitem = new NBTItem(player.getItemInHand());
            nbtitem.setInteger("blocks", Integer.valueOf(nbtitem.getInteger("blocks").intValue() + 1));
            for (String s : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".blocks")) {
                String[] split = s.split(":");
                Material b = ((XMaterial)XMaterial.matchXMaterial(split[0]).get()).parseMaterial();
                if (block.getType() == b)
                    nbtitem.setInteger("xp", Integer.valueOf(nbtitem.getInteger("xp").intValue() + Integer.valueOf(split[1]).intValue()));
            }
            for (String s : LevelTools.plugin.getConfig().getConfigurationSection(nameinconfig + ".levels").getKeys(false)) {
                int xpneeded = LevelTools.plugin.getConfig().getInt(nameinconfig + ".levels." + s + ".xp-needed");
                if (nbtitem.getInteger("xp").intValue() >= xpneeded) {
                    Integer i = nbtitem.getInteger("level");
                    Integer j = Integer.valueOf(s);
                    if (i.equals(j))
                        continue;
                    if (i.intValue() > j.intValue())
                        continue;
                    nbtitem.setInteger("level", Integer.valueOf(s));
                    for (String reward : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".levels." + s + ".rewards")) {
                        String[] splits = reward.split(" ", 2);
                        String prefix = splits[0];
                        if (prefix.equalsIgnoreCase("[cmd]"))
                            Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), splits[1]
                                    .replace("%player%", player.getName()));
                        if (prefix.equalsIgnoreCase("[message]"))
                            player.sendMessage(color(splits[1].replace("%player%", player.getName())));
                        if (prefix.equalsIgnoreCase("[enchant]")) {
                            String[] splitench = splits[1].split(" ");
                            enchantstoadd.put(((XEnchantment)XEnchantment.matchXEnchantment(splitench[0]).get()).parseEnchantment(), Integer.valueOf(splitench[1]));
                        }
                    }
                }
            }
            if (LevelTools.plugin.getConfig().contains(nameinconfig + ".levels." + (nbtitem.getInteger("level").intValue() + 1) + ".xp-needed"))
                xptonextlevel = LevelTools.plugin.getConfig().getInt(nameinconfig + ".levels." + (nbtitem.getInteger("level").intValue() + 1) + ".xp-needed");
            ItemMeta meta = nbtitem.getItem().getItemMeta();
            List<String> newlore = new ArrayList<>();
            if (LevelTools.tokenenchant_hook == true) {
                Map<CEHandler, Integer> enchantments = LevelTools.tokenenchant_api.getEnchantments(player.getItemInHand());
                List<String> telore = new ArrayList<>();
                enchantments.forEach((enc, level) -> {
                    if (enc.isVanilla())
                        return;
                    String lore = enc.getLoreEntry(level.intValue(), true, false, "");
                    newlore.add(color(LevelTools.plugin.getConfig().getString("settings.tokenenchant-hook.lore.prefix") + lore));
                });
            }
            Map<CEnchantment, Integer> crazyenchant = new HashMap<>();
            if (LevelTools.crazyenchant_hook == true) {
                Map<CEnchantment, Integer> enchantments = LevelTools.crazyenchant_api.getEnchantments(player.getItemInHand());
                List<String> telore = new ArrayList<>();
                enchantments.forEach((enc, level) -> {
                    String lore = enc.getCustomName();
                    crazyenchant.put(enc, Integer.valueOf(enc.getLevel(player.getItemInHand())));
                    newlore.add(color(LevelTools.plugin.getConfig().getString("settings.crazyenchant-hook.lore.prefix") + lore));
                });
            }
            for (String s : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".lore")) {
                s = s.replace("%blocks%", Integer.toString(nbtitem.getInteger("blocks").intValue()));
                s = s.replace("%xp%", Integer.toString(nbtitem.getInteger("xp").intValue()));
                s = s.replace("%progressbar%", ProgressBar.getProgressBar(nbtitem.getInteger("xp").intValue(), xptonextlevel));
                s = s.replace("%level%", Integer.toString(nbtitem.getInteger("level").intValue()));
                Integer xp = nbtitem.getInteger("xp");
                if (xptonextlevel == 0) {
                    s = s.replace("%xp_needed%", color(LevelTools.plugin.getConfig().getString("settings.maxlevel")));
                } else {
                    s = s.replace("%xp_needed%", Integer.toString(xptonextlevel));
                }
                if (xptonextlevel != 0) {
                    int percentage = (xp.intValue() * 100 + (xptonextlevel >> 1)) / xptonextlevel;
                    s = s.replace("%percentage%", Integer.toString(percentage));
                } else {
                    s = s.replace("%percentage%", Integer.toString(100));
                }
                newlore.add(color(s));
            }
            meta.setLore(newlore);
            for (Map.Entry<Enchantment, Integer> entry : enchantstoadd.entrySet()) {
                Enchantment e = entry.getKey();
                Integer currentench = Integer.valueOf(player.getItemInHand().getEnchantmentLevel(e));
                meta.addEnchant(e, currentench.intValue() + ((Integer)entry.getValue()).intValue(), true);
            }
            nbtitem.getItem().setItemMeta(meta);
            nbtitem.applyNBT(player.getItemInHand());
            for (Map.Entry<CEnchantment, Integer> entry : crazyenchant.entrySet()) {
                CEnchantment e = entry.getKey();
                Integer currentench = Integer.valueOf(LevelTools.crazyenchant_api.getLevel(player.getItemInHand(), e));
                LevelTools.crazyenchant_api.addEnchantment(player.getItemInHand(), e, currentench.intValue() + ((Integer)entry.getValue()).intValue());
            }
        }
    }

    public static void handle(List<Block> blocks, Player player, String nameinconfig) {
        Map<Enchantment, Integer> enchantstoadd = new HashMap<>();
        int xptonextlevel = 0;
        int blocksbroken = 1;
        if (player.getItemInHand() == null)
            return;
        if (LevelTools.plugin.getConfig().getBoolean(nameinconfig + ".enabled") == true) {
            for (String s : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".disabled_worlds")) {
                if (player.getWorld().getName().equals(s))
                    return;
            }
            for (Block block : blocks)
                blocksbroken++;
            NBTItem nbtitem = new NBTItem(player.getItemInHand());
            nbtitem.setInteger("blocks", Integer.valueOf(nbtitem.getInteger("blocks").intValue() + blocksbroken));
            for (Block b : blocks) {
                for (String s : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".blocks")) {
                    String[] split = s.split(":");
                    Material block = ((XMaterial)XMaterial.matchXMaterial(split[0]).get()).parseMaterial();
                    if (block == b.getType())
                        nbtitem.setInteger("xp", Integer.valueOf(nbtitem.getInteger("xp").intValue() + Integer.valueOf(split[1]).intValue()));
                }
            }
            for (String s : LevelTools.plugin.getConfig().getConfigurationSection(nameinconfig + ".levels").getKeys(false)) {
                int xpneeded = LevelTools.plugin.getConfig().getInt(nameinconfig + ".levels." + s + ".xp-needed");
                if (nbtitem.getInteger("xp").intValue() >= xpneeded) {
                    Integer i = nbtitem.getInteger("level");
                    Integer j = Integer.valueOf(s);
                    if (i.equals(j))
                        continue;
                    if (i.intValue() > j.intValue())
                        continue;
                    nbtitem.setInteger("level", Integer.valueOf(s));
                    for (String reward : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".levels." + s + ".rewards")) {
                        String[] splits = reward.split(" ", 2);
                        String prefix = splits[0];
                        if (prefix.equalsIgnoreCase("[cmd]"))
                            Bukkit.dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), splits[1]
                                    .replace("%player%", player.getName()));
                        if (prefix.equalsIgnoreCase("[message]"))
                            player.sendMessage(color(splits[1].replace("%player%", player.getName())));
                        if (prefix.equalsIgnoreCase("[enchant]")) {
                            String[] splitench = splits[1].split(" ");
                            enchantstoadd.put(((XEnchantment)XEnchantment.matchXEnchantment(splitench[0]).get()).parseEnchantment(), Integer.valueOf(splitench[1]));
                        }
                    }
                }
            }
            if (LevelTools.plugin.getConfig().contains(nameinconfig + ".levels." + (nbtitem.getInteger("level").intValue() + 1) + ".xp-needed"))
                xptonextlevel = LevelTools.plugin.getConfig().getInt(nameinconfig + ".levels." + (nbtitem.getInteger("level").intValue() + 1) + ".xp-needed");
            ItemMeta meta = nbtitem.getItem().getItemMeta();
            List<String> newlore = new ArrayList<>();
            if (LevelTools.tokenenchant_hook == true) {
                Map<CEHandler, Integer> enchantments = LevelTools.tokenenchant_api.getEnchantments(player.getItemInHand());
                List<String> telore = new ArrayList<>();
                enchantments.forEach((enc, level) -> {
                    if (enc.isVanilla())
                        return;
                    String lore = enc.getLoreEntry(level.intValue(), true, false, "");
                    newlore.add(color(LevelTools.plugin.getConfig().getString("settings.tokenenchant-hook.lore.prefix") + lore));
                });
            }
            Map<CEnchantment, Integer> crazyenchant = new HashMap<>();
            if (LevelTools.crazyenchant_hook == true) {
                Map<CEnchantment, Integer> enchantments = LevelTools.crazyenchant_api.getEnchantments(player.getItemInHand());
                List<String> telore = new ArrayList<>();
                enchantments.forEach((enc, level) -> {
                    String lore = enc.getCustomName();
                    crazyenchant.put(enc, Integer.valueOf(enc.getLevel(player.getItemInHand())));
                });
            }
            for (String s : LevelTools.plugin.getConfig().getStringList(nameinconfig + ".lore")) {
                s = s.replace("%blocks%", Integer.toString(nbtitem.getInteger("blocks").intValue()));
                s = s.replace("%xp%", Integer.toString(nbtitem.getInteger("xp").intValue()));
                s = s.replace("%progressbar%", ProgressBar.getProgressBar(nbtitem.getInteger("xp").intValue(), xptonextlevel));
                s = s.replace("%level%", Integer.toString(nbtitem.getInteger("level").intValue()));
                Integer xp = nbtitem.getInteger("xp");
                if (xptonextlevel == 0) {
                    s = s.replace("%xp_needed%", color(LevelTools.plugin.getConfig().getString("settings.maxlevel")));
                } else {
                    s = s.replace("%xp_needed%", Integer.toString(xptonextlevel));
                }
                if (xptonextlevel != 0) {
                    int percentage = (xp.intValue() * 100 + (xptonextlevel >> 1)) / xptonextlevel;
                    s = s.replace("%percentage%", Integer.toString(percentage));
                } else {
                    s = s.replace("%percentage%", Integer.toString(100));
                }
                newlore.add(color(s));
            }
            meta.setLore(newlore);
            for (Map.Entry<Enchantment, Integer> entry : enchantstoadd.entrySet()) {
                Enchantment e = entry.getKey();
                Integer currentench = Integer.valueOf(player.getItemInHand().getEnchantmentLevel(e));
                meta.addEnchant(e, currentench.intValue() + ((Integer)entry.getValue()).intValue(), true);
            }
            nbtitem.getItem().setItemMeta(meta);
            nbtitem.applyNBT(player.getItemInHand());
            for (Map.Entry<CEnchantment, Integer> entry : crazyenchant.entrySet()) {
                CEnchantment e = entry.getKey();
                Integer currentench = Integer.valueOf(LevelTools.crazyenchant_api.getLevel(player.getItemInHand(), e));
                LevelTools.crazyenchant_api.addEnchantment(player.getItemInHand(), e, currentench.intValue() + ((Integer)entry.getValue()).intValue());
            }
        }
    }
}
