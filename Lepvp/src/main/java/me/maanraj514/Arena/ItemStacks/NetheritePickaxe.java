package me.maanraj514.Arena.ItemStacks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NetheritePickaxe {

    public static ItemStack NetheritePickaxe;

    public static void init() {
        createNetheritePickaxe();
    }

    private static void createNetheritePickaxe() {
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Netherite Pickaxe");
        List<String> lore = new ArrayList<>();
        lore.add("it's just a pickaxe >:)");
        meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setLore(lore);
        item.setItemMeta(meta);
        NetheritePickaxe = item;
    }
}
