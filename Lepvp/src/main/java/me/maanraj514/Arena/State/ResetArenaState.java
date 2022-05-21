package me.maanraj514.Arena.State;

import me.maanraj514.Arena.Arena;
import me.maanraj514.Lepvp;
import me.maanraj514.map.LocalGameMap;
import me.maanraj514.map.MapInterface;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResetArenaState extends ArenaState{

    private String dupArenaName;
    private Player player;
    private Arena newArena;
    private String world;

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        MapInterface map = plugin.getMap();

        List<Arena> arenasToAdd = new ArrayList<>();

        newArena = plugin.getNewArena();

        for (Arena arena : plugin.getArenaManager().getDupArenaList()) {
            dupArenaName = arena.getDisplayName();
        }

        UUID playerUID = getArena().getPlayers().get(0);
        player = Bukkit.getPlayer(playerUID);

        if (player != null){
            world = player.getWorld().getName();
            System.out.println(world);
        }

        for (UUID playerUUID : getArena().getPlayers()){
            Player bukkitPlayer = Bukkit.getPlayer(playerUUID);
            if (bukkitPlayer == null) continue;

            bukkitPlayer.setHealth(bukkitPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            plugin.getArenaManager().getRollBackManager().restore(bukkitPlayer, plugin);
        }
        for (Arena arena1 : plugin.getArenaManager().getSourceArenaList()) {
            String arena1Name = arena1.getDisplayName();
            if (dupArenaName.startsWith(arena1Name)){
                if (world.equalsIgnoreCase(dupArenaName)){
                    map = new LocalGameMap(new File(player.getServer().getWorldContainer().getAbsolutePath()), arena1Name, true);

                    Location newArenaSpawnLocationOne = new Location(map.getWorld(), arena1.getSpawnLocationOne().getX(), arena1.getSpawnLocationOne().getY(), arena1.getSpawnLocationOne().getZ(), arena1.getSpawnLocationOne().getYaw(), arena1.getSpawnLocationOne().getPitch());
                    Location newArenaSpawnLocationTwo = new Location(map.getWorld(), arena1.getSpawnLocationTwo().getX(), arena1.getSpawnLocationTwo().getY(), arena1.getSpawnLocationTwo().getZ(), arena1.getSpawnLocationTwo().getYaw(), arena1.getSpawnLocationTwo().getPitch());

                    newArena = new Arena(map.getWorld().getName(), map.getWorld().getName().toUpperCase(), newArenaSpawnLocationOne, newArenaSpawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
                    arenasToAdd.add(newArena);
                    Bukkit.getLogger().info(Colorize.format("&aTHE MAP " + map.getWorld().getName() + " HAS BEEN LOADED"));
                }
            }
        }

        for (Arena a1 : arenasToAdd){
            plugin.getArenaManager().getDupArenaList().add(a1);
            Bukkit.getLogger().info(Colorize.format("&aTHE MAP" + map.getWorld().getName() + "HAS BEEN ADDED"));
        }

        for (Arena arena : plugin.getArenaManager().getDupArenaList()) {
            if (arena.getDisplayName().equalsIgnoreCase(world)){
                map.delete(dupArenaName);
                Bukkit.getConsoleSender().sendMessage(Colorize.format("&aThe map " + "&7" + arena.getDisplayName() + " &ahas been unloaded"));

                plugin.getArenaManager().deleteDupeArenaItself(arena);
                Bukkit.getConsoleSender().sendMessage(Colorize.format("&aThe arena " + "&7" + arena.getDisplayName() + " &ahas been deleted"));
            }
        }

        getArena().getPlayers().clear();
    }
}