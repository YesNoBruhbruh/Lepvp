package me.maanraj514.Arena.ItemStacks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NetheriteLeggings {

    public static ItemStack NetheriteLeggings;

    public static void init() {
        createNetheriteLeggings();
    }

    private static void createNetheriteLeggings() {
        ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Netherite Leggings");
        List<String> lore = new ArrayList<>();
        lore.add("it's just a leggings >:)");
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setLore(lore);
        item.setItemMeta(meta);
        NetheriteLeggings = item;
    }
}
