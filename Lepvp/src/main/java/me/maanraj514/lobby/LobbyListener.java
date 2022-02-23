package me.maanraj514.lobby;

import me.maanraj514.Arena.Arena;
import me.maanraj514.Lepvp;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyListener implements Listener {
    private final Lepvp plugin;
    private final Arena arena;

    public LobbyListener(Arena arena, Lepvp plugin) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        player.getInventory().clear();
        player.getActivePotionEffects().clear();

        if(plugin.getConfig().get("player-location") != null){
            player.teleport((Location) plugin.getConfig().get("player-location"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if(!p.hasPermission("lepvp.admin")){
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if(!p.hasPermission("lepvp.admin")){
            //fix this to get lobby world name in config
            if(p.getWorld().getName().equalsIgnoreCase("lobby")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDrop(BlockDropItemEvent event){
        Player p = event.getPlayer();
        if(!p.hasPermission("lepvp.admin")){
            //fix this to get lobby world name in config
            if(p.getWorld().getName().equalsIgnoreCase("lobby")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDmg(EntityDamageEvent event){
        //fix this to get lobby world name in config
        if (event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase("lobby")) {
            Player player = (Player) event.getEntity();
            if (event.getEntity() instanceof Player) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onFood(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        if(!p.hasPermission("lepvp.admin")){
            //fix this by checking the lobby world in config
            if(p.getWorld().getName().equalsIgnoreCase("lobby")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}