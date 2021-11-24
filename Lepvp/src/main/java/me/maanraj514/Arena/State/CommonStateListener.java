package me.maanraj514.Arena.State;

import me.maanraj514.Arena.Arena;
import me.maanraj514.Lepvp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CommonStateListener implements Listener {

    private final Lepvp plugin;

    private final Arena arena;

    public CommonStateListener(Arena arena, Lepvp plugin) {
        this.arena = arena;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if(!arena.isPlayer(event.getPlayer())) return;
        if (!p.hasPermission("lepvp.admin")) {

            if (arena.getArenaState() instanceof WaitingArenaState && arena.getArenaState() instanceof  StartingArenaState) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if(!arena.isPlayer(event.getPlayer())) return;
        if (!p.hasPermission("lepvp.admin")) {

            if (arena.getArenaState() instanceof WaitingArenaState && arena.getArenaState() instanceof  StartingArenaState) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if(!arena.isPlayer(player)) return;
        if (arena.getArenaState() instanceof ActiveGameState) {
            event.setCancelled(false);
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!arena.isPlayer(player)) return;

        arena.removePlayer(player, plugin);

        player.getInventory().clear();
        player.getActivePotionEffects().clear();

        player.setLevel(0);
        player.setExp(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!arena.isPlayer(player)) return;
        event.setCancelled(!(arena.getArenaState() instanceof ActiveGameState));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(!arena.isPlayer(player)) return;

        arena.removePlayer(player, plugin);

        player.getInventory().clear();
        player.getActivePotionEffects().clear();

        player.setLevel(0);
        player.setExp(0);
    }
}