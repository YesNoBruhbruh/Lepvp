package me.maanraj514.Arena.State;

import lombok.Getter;
import me.maanraj514.Arena.ItemStacks.*;
import me.maanraj514.Lepvp;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActiveGameState extends ArenaState {

    @Getter
    private List<UUID> alivePlayers;
    private Lepvp plugin;
    private boolean isOver = false;

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        this.plugin = plugin;

        alivePlayers = new ArrayList<>(getArena().getPlayers());

        int lastSpawnId = 0;

        for (UUID playerUUID : alivePlayers) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null || !player.isOnline()) continue;

            if (lastSpawnId == 0) {
                player.teleport(getArena().getSpawnLocationOne());
                lastSpawnId = 1;
            } else {
                player.teleport(getArena().getSpawnLocationTwo());
                lastSpawnId = 0;
            }

            player.getInventory().setHelmet(NetheriteHelmet.NetheriteHelmet);
            player.getInventory().setChestplate(NetheriteChestplate.NetheriteChestplate);
            player.getInventory().setLeggings(NetheriteLeggings.NetheriteLeggings);
            player.getInventory().setBoots(NetheriteBoots.NetheriteBoots);

            player.getInventory().setItem(0, NetheriteSword.NetheriteSword);
            player.getInventory().setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
            player.getInventory().setItem(4, new ItemStack(Material.GOLDEN_APPLE, 64));
            player.getInventory().setItem(2, new ItemStack(Material.OBSIDIAN, 64));
            player.getInventory().setItem(3, new ItemStack(Material.END_CRYSTAL, 64));
            player.getInventory().setItem(5, Crossbowweapon.Crossbow);
            player.getInventory().setItem(6, new ItemStack(Material.TOTEM_OF_UNDYING));
            Potion speed = new Potion(PotionType.SPEED, 2);
            speed.setSplash(true);
            ItemStack item = speed.toItemStack(1);
            player.getInventory().setItem(7, item);
            ItemStack result = new ItemStack(Material.TIPPED_ARROW, 64);
            PotionMeta resultMeta = (PotionMeta) result.getItemMeta();
            resultMeta.setBasePotionData(new PotionData(PotionType.SLOW_FALLING));
            resultMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1), true);
            result.setItemMeta(resultMeta);
            Potion strength = new Potion(PotionType.STRENGTH, 2);
            strength.setSplash(true);
            ItemStack strengthItem = strength.toItemStack(1);
            player.getInventory().setItem(8, strengthItem);
            player.getInventory().setItem(9, result);
            player.getInventory().setItem(10, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
            player.getInventory().setItem(11, NetheritePickaxe.NetheritePickaxe);
            player.getInventory().setItem(12, item);
            player.getInventory().setItem(13, item);
            player.getInventory().setItem(14, item);
            player.getInventory().setItem(15, item);
            player.getInventory().setItem(16, item);
            player.getInventory().setItem(17, item);
            player.getInventory().setItem(18, new ItemStack(Material.ENDER_PEARL, 16));
            player.getInventory().setItem(19, new ItemStack(Material.ENDER_PEARL, 16));
            player.getInventory().setItem(20, new ItemStack(Material.TOTEM_OF_UNDYING));
            player.getInventory().setItem(21, strengthItem);
            player.getInventory().setItem(22, strengthItem);
            player.getInventory().setItem(23, strengthItem);
            player.getInventory().setItem(24, strengthItem);
            player.getInventory().setItem(25, strengthItem);
            player.getInventory().setItem(26, strengthItem);
            player.getInventory().setItem(27, new ItemStack(Material.ENDER_PEARL, 16));
            player.getInventory().setItem(28, new ItemStack(Material.ENDER_PEARL, 16));
            player.getInventory().setItem(29, new ItemStack(Material.TOTEM_OF_UNDYING));
            player.getInventory().setItem(30, item);
            player.getInventory().setItem(31, strengthItem);
            player.getInventory().setItem(32, item);
            player.getInventory().setItem(33, strengthItem);
            Potion turtle = new Potion(PotionType.TURTLE_MASTER, 2);
            turtle.setSplash(true);
            ItemStack turtleItem = turtle.toItemStack(1);
            player.getInventory().setItem(34, turtleItem);
            player.getInventory().setItem(35, turtleItem);
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (alivePlayers.size() > 1 || isOver) return;

            this.isOver = true;

            if (alivePlayers.size() == 1) {
                UUID winnerUUID = alivePlayers.get(0);
                Player winner = Bukkit.getPlayer(winnerUUID);
                if (winner == null || !winner.isOnline()) {
                    getArena().sendMessage("&cGame Over, but winner could not be found");
                } else {
                    getArena().sendMessage("&a" + winner.getDisplayName() + " has won!");
                }
            } else {
                getArena().sendMessage("&cNo alive players? Game Over anyway");
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> getArena().setState(new WaitingArenaState(), plugin), 20 * 5);
        }, 0, 4);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!getArena().isPlayer(player)) return;

        if (isOver) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(false);

        if (player.getHealth() - event.getFinalDamage() <= 0 && !(player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING || player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING)) {
            alivePlayers.remove(player.getUniqueId());
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setGameMode(GameMode.SPECTATOR);
            getArena().sendMessage("&a" + player.getDisplayName() + " died!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        if (!getArena().isPlayer(event.getPlayer())) return;

        alivePlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!getArena().isPlayer(player)) return;

        alivePlayers.remove(player.getUniqueId());

        player.getInventory().clear();
        player.getActivePotionEffects().clear();

        player.setLevel(0);
        player.setExp(0);
    }
}