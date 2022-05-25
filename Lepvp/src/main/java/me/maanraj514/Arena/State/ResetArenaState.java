package me.maanraj514.Arena.State;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.ArenaStatus;
import me.maanraj514.Lepvp;
import me.maanraj514.map.LocalGameMap;
import me.maanraj514.map.MapInterface;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.SlimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ResetArenaState extends ArenaState{

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        plugin.getArenaManager().setArenaStatus(ArenaStatus.WAITING);

        MapInterface map = plugin.getMap();

        List<Arena> arenasToAdd = new ArrayList<>();

        String arenaWorldName = getArena().getDisplayName().toLowerCase();

        for (UUID playerUUID : getArena().getPlayers()){
            Player bukkitPlayer = Bukkit.getPlayer(playerUUID);
            if (bukkitPlayer == null) continue;

            bukkitPlayer.setHealth(bukkitPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            plugin.getArenaManager().getRollBackManager().restore(bukkitPlayer, plugin);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equalsIgnoreCase(arenaWorldName)){
                player.teleportAsync(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
            }
        }
        if (plugin.doesSWMExist()){
            SlimeLoader slimeLoader = plugin.getSlime().getLoader("file");
            try{
                if (slimeLoader.worldExists(arenaWorldName)) {
                    if (Bukkit.getWorld(arenaWorldName) != null) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            SlimeUtil.unloadWorld(arenaWorldName);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "TRIGGERED UNLOAD METHOD");

                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                SlimeUtil.loadWorld(arenaWorldName, plugin);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "TRIGGERED LOAD METHOD");

                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    Location newArenaLocationOne = new Location(Bukkit.getWorld(arenaWorldName), getArena().getSpawnLocationOne().getX(), getArena().getSpawnLocationOne().getY(), getArena().getSpawnLocationOne().getZ(), getArena().getSpawnLocationOne().getYaw(), getArena().getSpawnLocationOne().getPitch());
                                    Location newArenaLocationTwo = new Location(Bukkit.getWorld(arenaWorldName), getArena().getSpawnLocationTwo().getX(), getArena().getSpawnLocationTwo().getY(), getArena().getSpawnLocationTwo().getZ(), getArena().getSpawnLocationTwo().getYaw(), getArena().getSpawnLocationTwo().getPitch());

                                    Arena arena1 = new Arena(arenaWorldName, arenaWorldName.toUpperCase(), newArenaLocationOne, newArenaLocationTwo, new WaitingArenaState(), new ArrayList<>());
                                    arenasToAdd.add(arena1);
                                    plugin.getArenaManager().addArenaToDupArenaList(arena1);
                                    plugin.getArenaManager().deleteDupeArenaItself(getArena());
                                }, 20*3);
                            }, 20*3);
                        }, 20*2);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            for (Arena arena1 : plugin.getArenaManager().getSourceArenaList()) {
                String arena1Name = arena1.getDisplayName().toLowerCase();
                System.out.println(arenaWorldName + "" + arena1Name);
                if (arenaWorldName.startsWith(arena1Name)){
                    map = new LocalGameMap(new File(Bukkit.getServer().getWorldContainer().getAbsolutePath()), arena1Name, true);

                    Location newArenaSpawnLocationOne = new Location(map.getWorld(), arena1.getSpawnLocationOne().getX(), arena1.getSpawnLocationOne().getY(), arena1.getSpawnLocationOne().getZ(), arena1.getSpawnLocationOne().getYaw(), arena1.getSpawnLocationOne().getPitch());
                    Location newArenaSpawnLocationTwo = new Location(map.getWorld(), arena1.getSpawnLocationTwo().getX(), arena1.getSpawnLocationTwo().getY(), arena1.getSpawnLocationTwo().getZ(), arena1.getSpawnLocationTwo().getYaw(), arena1.getSpawnLocationTwo().getPitch());

                    Arena newArena = new Arena(map.getWorld().getName(), map.getWorld().getName().toUpperCase(), newArenaSpawnLocationOne, newArenaSpawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
                    arenasToAdd.add(newArena);
                    Bukkit.getLogger().info(Colorize.format("&aTHE MAP " + map.getWorld().getName() + " HAS BEEN LOADED"));
                }
            }

            for (Arena a1 : arenasToAdd){
                plugin.getArenaManager().getDupArenaList().add(a1);
                Bukkit.getLogger().info(Colorize.format("&aTHE MAP" + map.getWorld().getName() + " HAS BEEN ADDED"));
            }

            if (plugin.getArenaManager().getDupArenaList() != null) {
                for (Arena arena : plugin.getArenaManager().getDupArenaList()) {
                    if (arena.getDisplayName().toLowerCase().equalsIgnoreCase(arenaWorldName)){
                        map.delete(arenaWorldName);
                        Bukkit.getConsoleSender().sendMessage(Colorize.format("&aThe map " + "&7" + arena.getDisplayName() + " &ahas been unloaded"));

                        plugin.getArenaManager().deleteDupeArenaItself(arena);
                        Bukkit.getConsoleSender().sendMessage(Colorize.format("&aThe arena " + "&7" + arena.getDisplayName() + " &ahas been deleted"));
                    }
                }
            }
        }
        plugin.getArenaManager().setArenaStatus(ArenaStatus.READY);

        getArena().getPlayers().clear();
    }
}