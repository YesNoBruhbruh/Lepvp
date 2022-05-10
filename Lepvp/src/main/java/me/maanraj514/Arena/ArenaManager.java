package me.maanraj514.Arena;

import lombok.Getter;
import me.maanraj514.Arena.State.WaitingArenaState;
import me.maanraj514.Lepvp;
import me.maanraj514.configuration.ConfigurationFile;
import me.maanraj514.configuration.ConfigurationUtility;
import me.maanraj514.PlayerRollBackManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaManager {

    @Getter
    public final List<Arena> arenaList;
    private final ConfigurationFile arenaConfigurationFile;

    @Getter
    private final ArenaSetupManager arenaSetupManager;

    @Getter
    private final PlayerRollBackManager rollBackManager;

    public ArenaManager(Lepvp plugin) {
        this.arenaList = new ArrayList<>();
        this.arenaConfigurationFile = new ConfigurationFile(plugin, "arenas");

        for(String arenaConfigName : this.arenaConfigurationFile.getConfiguration().getKeys(false)){
            ConfigurationSection section = this.arenaConfigurationFile.getConfiguration().getConfigurationSection(arenaConfigName);

            String displayName = section.getString("displayName");
            Location spawnLocationOne = ConfigurationUtility.readLocation(Objects.requireNonNull(section.getConfigurationSection("spawnLocationOne")));
            Location spawnLocationTwo = ConfigurationUtility.readLocation(Objects.requireNonNull(section.getConfigurationSection("spawnLocationTwo")));

            Arena arena = new Arena(arenaConfigName, displayName, spawnLocationOne, spawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
            this.arenaList.add(arena);
        }

        this.arenaSetupManager = new ArenaSetupManager(this, plugin);
        this.rollBackManager = new PlayerRollBackManager();

        plugin.getServer().getPluginManager().registerEvents(arenaSetupManager, plugin);
    }

    public void saveArenaToConfig(Arena arena){
        this.arenaList.removeIf(existing -> existing.getConfigName().equalsIgnoreCase(arena.getDisplayName()));
        this.arenaList.add(arena);

        YamlConfiguration configuration = this.arenaConfigurationFile.getConfiguration();

        configuration.set(arena.getConfigName(), null);

        configuration.set(arena.getConfigName() + ".displayName", arena.getDisplayName());
        ConfigurationUtility.saveLocation(arena.getSpawnLocationOne(), configuration.createSection(arena.getConfigName() + ".spawnLocationOne"));
        ConfigurationUtility.saveLocation(arena.getSpawnLocationTwo(), configuration.createSection(arena.getConfigName() + ".spawnLocationTwo"));

        this.arenaConfigurationFile.saveConfig();
    }

    public List<Arena> getArenas(){
        return arenaList;
    }

    public Arena findSpecificArena(String specificArenaDisplayName) {
        for (Arena arena : getArenas()) {
            if (arena.getDisplayName().equalsIgnoreCase(specificArenaDisplayName)){
                return arena;
            }
        }
        return null;
    }

    public Arena findOpenArena() {
        for (Arena arena : getArenas()) {
            if (arena.getArenaState() instanceof WaitingArenaState){
                return arena;
            }
        }
        return null;
    }

    public Arena findSpecificOpenArena(String specificOpenArenaDisplayName) {
        for (Arena arena : getArenas()) {
            if (arena.getDisplayName().equalsIgnoreCase(specificOpenArenaDisplayName) && arena.getArenaState() instanceof WaitingArenaState){
                return arena;
            }
        }
        return null;
    }

    public Arena findPlayerName(Player player) {
        for (Arena arena : getArenas()) {
            if (arena.getPlayers().contains(player.getUniqueId())){
                return arena;
            }
        }
        return null;
    }

    public void deleteArenaFromEverything(Arena arena) {
        this.arenaConfigurationFile.getConfiguration().set(arena.getConfigName(), null);
        this.arenaConfigurationFile.saveConfig();

        this.arenaList.remove(arena);
    }

    public void deleteArenaItself(Arena arena) {
        this.arenaList.remove(arena);
    }
}