package me.maanraj514.Arena.ItemStacks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NetheriteSword {

    public static ItemStack NetheriteSword;

    public static void init() {
        createNetheriteSword();
    }

    private static void createNetheriteSword() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Netherite Sword");
        List<String> lore = new ArrayList<>();
        lore.add("it's just a sword >:)");
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setLore(lore);
        item.setItemMeta(meta);
        NetheriteSword = item;
    }
}
