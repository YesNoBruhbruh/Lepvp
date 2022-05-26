package me.maanraj514.Arena.ItemStacks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NetheriteChestplate {

    public static ItemStack NetheriteChestplate;

    public static void init() {
        createNetheriteChestplate();
    }

    private static void createNetheriteChestplate() {
        ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Netherite Chestplate");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        item.setItemMeta(meta);
        NetheriteChestplate = item;
    }
}
