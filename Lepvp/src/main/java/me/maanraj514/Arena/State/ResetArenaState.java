package me.maanraj514.Arena.State;

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
import java.util.ArrayList;
import java.util.List;
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
        if (plugin.doesSWMExist()){
            if (Bukkit.getWorld(arenaWorldName) != null) {
                SlimeUtil.unloadWorld(arenaWorldName);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "TRIGGERED UNLOAD METHOD");

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    SlimeUtil.loadWorld(arenaWorldName, plugin);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "TRIGGERED LOAD METHOD");

                    Location newArenaLocationOne = new Location(Bukkit.getWorld(arenaWorldName), getArena().getSpawnLocationOne().getX(), getArena().getSpawnLocationOne().getY(), getArena().getSpawnLocationOne().getZ(), getArena().getSpawnLocationOne().getYaw(), getArena().getSpawnLocationOne().getPitch());
                    Location newArenaLocationTwo = new Location(Bukkit.getWorld(arenaWorldName), getArena().getSpawnLocationTwo().getX(), getArena().getSpawnLocationTwo().getY(), getArena().getSpawnLocationTwo().getZ(), getArena().getSpawnLocationTwo().getYaw(), getArena().getSpawnLocationTwo().getPitch());

                    Arena arena1 = new Arena(arenaWorldName, arenaWorldName.toUpperCase(), newArenaLocationOne, newArenaLocationTwo, new WaitingArenaState(), new ArrayList<>());
                    arenasToAdd.add(arena1);
                }, 20*3);
            }
            for (Arena a1 : arenasToAdd){
                plugin.getArenaManager().getDupArenaList().add(a1);
                Bukkit.getLogger().info(Colorize.format("&aTHE MAP " + getArena().getDisplayName().toLowerCase() + " HAS BEEN ADDED"));
            }

        }else{
            if (Bukkit.getWorld(arenaWorldName) != null) {
                map = new LocalGameMap(plugin.getServerFolder(), arenaWorldName, true);

                Location newArenaSpawnLocationOne = new Location(map.getWorld(), getArena().getSpawnLocationOne().getX(), getArena().getSpawnLocationOne().getY(), getArena().getSpawnLocationOne().getZ(), getArena().getSpawnLocationOne().getYaw(), getArena().getSpawnLocationOne().getPitch());
                Location newArenaSpawnLocationTwo = new Location(map.getWorld(), getArena().getSpawnLocationTwo().getX(), getArena().getSpawnLocationTwo().getY(), getArena().getSpawnLocationTwo().getZ(), getArena().getSpawnLocationTwo().getYaw(), getArena().getSpawnLocationTwo().getPitch());

                Arena newArena = new Arena(map.getWorld().getName(), map.getWorld().getName().toUpperCase(), newArenaSpawnLocationOne, newArenaSpawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
                arenasToAdd.add(newArena);
                Bukkit.getLogger().info(Colorize.format("&aTHE MAP " + map.getWorld().getName() + " HAS BEEN LOADED"));
            }

            for (Arena a1 : arenasToAdd){
                plugin.getArenaManager().getDupArenaList().add(a1);
                Bukkit.getLogger().info(Colorize.format("&aTHE MAP " + map.getWorld().getName() + " HAS BEEN ADDED"));
            }

        }
        plugin.getArenaManager().getDupArenaList().remove(getArena());
        plugin.getArenaManager().setArenaStatus(ArenaStatus.READY);

        getArena().getPlayers().clear();
    }
}