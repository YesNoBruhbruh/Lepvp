package me.maanraj514.Arena.ItemStacks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Crossbowweapon {

    public static ItemStack Crossbow;

    public static void init() {
        createCrossbow();
    }

    private static void createCrossbow() {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Crossbow");
        List<String> lore = new ArrayList<>();
        lore.add("it's just a crossbow >:)");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.MULTISHOT, 1, true);
        meta.addEnchant(Enchantment.QUICK_CHARGE, 3, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setLore(lore);
        item.setItemMeta(meta);
        Crossbow = item;
    }
}