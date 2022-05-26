package me.maanraj514.Arena.ItemStacks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NetheriteHelmet {

    public static ItemStack NetheriteHelmet;

    public static void init() {
        createNetheriteHelmet();
    }

    private static void createNetheriteHelmet() {
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Netherite Helmet");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        item.setItemMeta(meta);
        NetheriteHelmet = item;
    }
}
