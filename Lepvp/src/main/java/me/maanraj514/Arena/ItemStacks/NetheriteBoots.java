package me.maanraj514.Arena.ItemStacks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NetheriteBoots {

    public static ItemStack NetheriteBoots;

    public static void init() {
        createNetheriteBoots();
    }

    private static void createNetheriteBoots() {
        ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Netherite Boots");
        meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
        meta.addEnchant(Enchantment.PROTECTION_FALL, 4, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        item.setItemMeta(meta);
        NetheriteBoots = item;
    }
}
