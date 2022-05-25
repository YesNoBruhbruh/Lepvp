package me.maanraj514.Arena;

import lombok.Getter;
import lombok.Setter;
import me.maanraj514.Arena.State.WaitingArenaState;
import me.maanraj514.Lepvp;
import me.maanraj514.configuration.ConfigurationFile;
import me.maanraj514.configuration.ConfigurationUtility;
import me.maanraj514.PlayerRollBackManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaManager {

    private final List<Arena> dupArenaList;
    private final List<Arena> sourceArenaList;

    private final ConfigurationFile arenaConfigurationFile;

    @Getter @Setter
    private ArenaStatus arenaStatus;

    @Getter
    private final ArenaSetupManager arenaSetupManager;

    @Getter
    private final PlayerRollBackManager rollBackManager;

    public ArenaManager(Lepvp plugin) {
        this.dupArenaList = new ArrayList<>();
        this.sourceArenaList = new ArrayList<>();
        this.arenaConfigurationFile = new ConfigurationFile(plugin, "arenas");

        for(String arenaConfigName : this.arenaConfigurationFile.getConfiguration().getKeys(false)){
            ConfigurationSection section = this.arenaConfigurationFile.getConfiguration().getConfigurationSection(arenaConfigName);

            String displayName = section.getString("displayName");
            Location spawnLocationOne = ConfigurationUtility.readLocation(Objects.requireNonNull(section.getConfigurationSection("spawnLocationOne")));
            Location spawnLocationTwo = ConfigurationUtility.readLocation(Objects.requireNonNull(section.getConfigurationSection("spawnLocationTwo")));

            Arena arena = new Arena(arenaConfigName, displayName, spawnLocationOne, spawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
            this.arenaStatus = ArenaStatus.WAITING;
            this.sourceArenaList.add(arena);
        }

        this.arenaSetupManager = new ArenaSetupManager(this, plugin);
        this.rollBackManager = new PlayerRollBackManager();

        plugin.getServer().getPluginManager().registerEvents(arenaSetupManager, plugin);
    }

    public void saveArenaToConfig(Arena arena){
        this.sourceArenaList.removeIf(existing -> existing.getConfigName().equalsIgnoreCase(arena.getDisplayName()));
        this.sourceArenaList.add(arena);

        YamlConfiguration configuration = this.arenaConfigurationFile.getConfiguration();

        configuration.set(arena.getConfigName(), null);

        configuration.set(arena.getConfigName() + ".displayName", arena.getDisplayName());
        ConfigurationUtility.saveLocation(arena.getSpawnLocationOne(), configuration.createSection(arena.getConfigName() + ".spawnLocationOne"));
        ConfigurationUtility.saveLocation(arena.getSpawnLocationTwo(), configuration.createSection(arena.getConfigName() + ".spawnLocationTwo"));

        this.arenaConfigurationFile.saveConfig();
    }

    public List<Arena> getSourceArenaList() {
        return sourceArenaList;
    }

    public List<Arena> getDupArenaList(){
        return dupArenaList;
    }

    public Arena findSpecificSourceArena(String specificArenaDisplayName) {
        for (Arena arena : getSourceArenaList()){
            if (arena.getDisplayName().equalsIgnoreCase(specificArenaDisplayName)){
                return arena;
            }
        }
        return null;
    }

    public Arena findSpecificDupArena(String specificArenaDisplayName) {
        for (Arena arena : getDupArenaList()) {
            if (arena.getDisplayName().equalsIgnoreCase(specificArenaDisplayName)){
                return arena;
            }
        }
        return null;
    }

    public Arena findOpenDupArena() {
        for (Arena arena : getDupArenaList()) {
            if (arena.getArenaState() instanceof WaitingArenaState){
                return arena;
            }
        }
        return null;
    }

    public Arena findSpecificOpenDupArena(String specificOpenArenaDisplayName) {
        for (Arena arena : getDupArenaList()) {
            if (arena.getDisplayName().equalsIgnoreCase(specificOpenArenaDisplayName) && arena.getArenaState() instanceof WaitingArenaState){
                return arena;
            }
        }
        return null;
    }

    public Arena findPlayerName(Player player) {
        for (Arena arena : getDupArenaList()) {
            if (arena.getPlayers().contains(player.getUniqueId())){
                return arena;
            }
        }
        return null;
    }

    public void addArenaToDupArenaList(Arena arena) {
        dupArenaList.add(arena);
    }

    public void deleteSourceArenaFromEverything(Arena arena) {
        this.arenaConfigurationFile.getConfiguration().set(arena.getConfigName(), null);
        this.arenaConfigurationFile.saveConfig();
        this.arenaConfigurationFile.reloadConfig();

        this.sourceArenaList.remove(arena);
    }

    public void deleteDupeArenaItself(Arena arena) {
        this.dupArenaList.remove(arena);
    }
}