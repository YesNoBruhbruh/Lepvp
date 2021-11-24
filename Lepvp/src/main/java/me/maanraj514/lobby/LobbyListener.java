package me.maanraj514.lobby;

import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.State.ActiveGameState;
import me.maanraj514.Arena.State.StartingArenaState;
import me.maanraj514.Arena.State.WaitingArenaState;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Message;
import org.bukkit.Bukkit;
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
    private Lepvp plugin;
    private Arena arena;

    public LobbyListener(Arena arena, Lepvp plugin) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        if(plugin.getConfig().get("player-location") != null){
            Player player = event.getPlayer();
            player.teleport((Location) plugin.getConfig().get("player-location"));

            player.getInventory().clear();
            player.getActivePotionEffects().clear();

            if (arena.getArenaState() instanceof WaitingArenaState) {
                Bukkit.getConsoleSender().sendMessage(Message.Color("&aThe Player Is in the list yay but he left waiting arena state"));
            }
            if (arena.getArenaState() instanceof StartingArenaState) {
                Bukkit.getConsoleSender().sendMessage(Message.Color("&aThe Player Is in the list yay but he left starting arena state"));
            }
            if (arena.getArenaState() instanceof ActiveGameState) {
                Bukkit.getConsoleSender().sendMessage(Message.Color("&aThe Player Is in the list yay but he left active game state"));
            }
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
            if(p.getWorld().getName().equalsIgnoreCase("lobby")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDrop(BlockDropItemEvent event){
        Player p = event.getPlayer();
        if(!p.hasPermission("lepvp.admin")){
            if(p.getWorld().getName().equalsIgnoreCase("lobby")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDmg(EntityDamageEvent event){
        if (event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase("lobby")) {
            Player player = (Player) event.getEntity();
            if (event.getEntity() instanceof Player) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onFood(FoodLevelChangeEvent event) {
        Player p = (Player) event.getEntity();
        if(!p.hasPermission("lepvp.admin")){
            if(p.getWorld().getName().equalsIgnoreCase("lobby")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
    }
}