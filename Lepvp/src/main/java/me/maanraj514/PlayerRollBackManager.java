package me.maanraj514;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRollBackManager {

    private final Map<UUID, Location> previousLocationMap = new HashMap<>();
    private final Map<UUID, GameMode> previousGameModeMap = new HashMap<>();
    private final Map<UUID, ItemStack[]> previousInventoryContents = new HashMap<>();
    private final Map<UUID, ItemStack[]> previousArmourContents = new HashMap<>();
    private final Map<UUID, Integer> previousHungerValue = new HashMap<>();
    private final Map<UUID, Integer> previousLevelMap = new HashMap<>();

    public void save(Player player) {
        previousLocationMap.put(player.getUniqueId(), player.getLocation());
        previousGameModeMap.put(player.getUniqueId(), player.getGameMode());
        previousInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
        previousArmourContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
        previousHungerValue.put(player.getUniqueId(), player.getFoodLevel());
        previousLevelMap.put(player.getUniqueId(), player.getLevel());
        player.getInventory().clear();
    }

    public void restore(Player player, JavaPlugin plugin) {
        player.getInventory().clear();

        ItemStack[] inventoryContent = previousInventoryContents.get(player.getUniqueId());
        if(inventoryContent != null){
            player.getInventory().setContents(inventoryContent);
        }

        ItemStack[] armorContents = previousArmourContents.get(player.getUniqueId());
        if(armorContents != null){
            player.getInventory().setArmorContents(armorContents);
        }

        GameMode previousGameMode = previousGameModeMap.get(player.getUniqueId());
        if (previousGameMode != null){
            player.setGameMode(previousGameMode);
        }

        Location previousLocation = previousLocationMap.get(player.getUniqueId());
        if (previousLocation != null) {
            player.teleport(previousLocation);
        }
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        player.setFoodLevel(previousHungerValue.getOrDefault(player.getUniqueId(), 20));

        player.setLevel(previousLevelMap.getOrDefault(player.getUniqueId(), 0));

        previousHungerValue.remove(player.getUniqueId());
        previousLocationMap.remove(player.getUniqueId());
        previousInventoryContents.remove(player.getUniqueId());
        previousArmourContents.remove(player.getUniqueId());
        previousGameModeMap.remove(player.getUniqueId());
        previousLevelMap.remove(player.getUniqueId());

        if (plugin == null) return;
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> player.setFireTicks(0), 2);
    }
}
