package me.maanraj514.utility;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemBuilder {
    private ItemStack is;

    public ItemBuilder(Material m){
        this(m, 1);
    }

    public ItemBuilder(ItemStack is){
        this.is=is;
    }

    public ItemBuilder(Material m, int amount){
        is = new ItemStack(m, amount);
    }

    public ItemBuilder(Material m, int amount, byte durability){
        is = new ItemStack(m, amount, durability);
    }

    public ItemBuilder clone(){
        return new ItemBuilder(is);
    }

    public ItemBuilder setDurability(short dur){
        is.setDurability(dur);
        return this;
    }

    public ItemBuilder setName(String name){
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        is.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addPotionEffect(PotionType type,  boolean extended, boolean upgraded){
        PotionMeta potionMeta = (PotionMeta) is.getItemMeta();
        if (potionMeta != null){
            potionMeta.setBasePotionData(new PotionData(type, extended, upgraded));
        }
        is.setItemMeta(potionMeta);
        return this;
    }

    public ItemBuilder hidePotionEffects() {
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        is.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build(){
        return is;
    }
}
