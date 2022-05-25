package me.maanraj514;

import com.grinderwolf.swm.api.SlimePlugin;
import lombok.Getter;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.ArenaManager;
import me.maanraj514.Arena.ArenaStatus;
import me.maanraj514.Arena.ItemStacks.*;
import me.maanraj514.Arena.State.CommonStateListener;
import me.maanraj514.Arena.State.WaitingArenaState;
import me.maanraj514.commands.*;
import me.maanraj514.map.LocalGameMap;
import me.maanraj514.map.MapInterface;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.CoolDown;
import me.maanraj514.utility.FileUtil;
import me.maanraj514.utility.SlimeUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;


public final class Lepvp extends JavaPlugin {

    public static Lepvp plugin;
    private Arena arena;
    @Getter
    private MapInterface map;

    @Getter
    private SlimePlugin slime;

    @Getter
    private ArenaManager arenaManager;

    FileConfiguration config = getConfig();

    @Getter
    List<String> gameWorldsName;
    @Getter
    List<World> slimeWorldsToUnload;

    @Getter
    private File serverFolder;

    @Override
    public void onEnable() {
        super.onEnable();

        config.options().copyDefaults();
        saveDefaultConfig();

        CoolDown.setupCoolDown();

        registerClasses();

        initItems();

        registerCommands();
        registerListeners();

        serverFolder = new File(getServer().getWorldContainer().getAbsolutePath());

        cleanUpMaps();
        cleanUpArenas();

        if (doesSWMExist()) {
            doSlimeStuff();
        }else{
            doMapStuff();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Plugin made by Maanraj514");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (doesSWMExist()) {

        }else{
            cleanUpMaps();
            cleanUpArenas();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Thanks for using my plugin, -- Maanraj514");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
    }

    public void registerCommands(){
        getCommand("reset").setExecutor(new ResetWorld(map));
        getCommand("setLobby").setExecutor(new SetLobbyCommand(this));
        getCommand("arena").setExecutor(new ArenaCommand(this, arena));
        getCommand("duels").setExecutor(new DuelCommand(this, arena));
    }

    public void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CommonStateListener(arena, plugin), this);
    }

    public void doSlimeStuff() {
        List<Arena> toAdd = new ArrayList<>();

        for (Arena arena : getArenaManager().getSourceArenaList()) {
            try{
                if (!slime.getLoader("file").worldExists(arena.getDisplayName())){
                    Bukkit.getLogger().log(Level.INFO, "Attempting to load arena world: " + arena.getDisplayName());

                    SlimeUtil.importWorld(arena.getDisplayName().toLowerCase(), new File(serverFolder + File.separator + arena.getDisplayName().toLowerCase()), this);
                    Bukkit.getLogger().log(Level.INFO, "importing world " + arena.getDisplayName().toLowerCase());

                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        SlimeUtil.loadWorld(arena.getDisplayName().toLowerCase(), this);
                        gameWorldsName.add(arena.getDisplayName().toLowerCase());

                        Bukkit.getLogger().log(Level.INFO, "loading world " + arena.getDisplayName().toLowerCase());
                    }, 20*3);
                }else{
                    SlimeUtil.loadWorld(arena.getDisplayName().toLowerCase(), this);
                    gameWorldsName.add(arena.getDisplayName().toLowerCase());

                    Bukkit.getLogger().log(Level.INFO, "loading world " + arena.getDisplayName().toLowerCase());
                }
                Bukkit.getConsoleSender().sendMessage(Colorize.format("&aSuccessfully loaded " + arena.getDisplayName().toLowerCase()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Bukkit.getScheduler().runTaskLater(this, () -> {
                for (Arena arena1 : getArenaManager().getSourceArenaList()) {
                    String arenaName = arena1.getDisplayName().toLowerCase();
                    for (String w : gameWorldsName) {
                        if (arenaName.equalsIgnoreCase(w)){
                            Location newArenaLocationOne = new Location(Bukkit.getWorld(w), arena1.getSpawnLocationOne().getX(), arena1.getSpawnLocationOne().getY(), arena1.getSpawnLocationOne().getZ(), arena1.getSpawnLocationOne().getYaw(), arena1.getSpawnLocationOne().getPitch());
                            Location newArenaLocationTwo = new Location(Bukkit.getWorld(w), arena1.getSpawnLocationTwo().getX(), arena1.getSpawnLocationTwo().getY(), arena1.getSpawnLocationTwo().getZ(), arena1.getSpawnLocationTwo().getYaw(), arena1.getSpawnLocationTwo().getPitch());

                            Arena newArena = new Arena(w, w.toUpperCase(), newArenaLocationOne, newArenaLocationTwo, new WaitingArenaState(), new ArrayList<>());
                            toAdd.add(newArena);
                            slimeWorldsToUnload.add(Bukkit.getWorld(w));
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "ADDED SLIME MAP " + w);
                        }
                    }
                }
                for (Arena a : toAdd) {
                    plugin.getArenaManager().getDupArenaList().add(a);
                    getArenaManager().setArenaStatus(ArenaStatus.READY);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "LOADED SLIME MAP " + a.getDisplayName());
                }
            }, 20*10);
        }
    }

    public void doMapStuff() {

        List<Arena> toAdd = new ArrayList<>();

        for (Arena arena1 : plugin.getArenaManager().getSourceArenaList()) {
            String arena1Name = arena1.getDisplayName().toLowerCase();
            File mapToReset = new File(arena1.getDisplayName().toLowerCase());
            if (mapToReset.exists()){
                map = new LocalGameMap(serverFolder, arena1Name, true);

                Location newArenaSpawnLocationOne = new Location(map.getWorld(), arena1.getSpawnLocationOne().getX(), arena1.getSpawnLocationOne().getY(), arena1.getSpawnLocationOne().getZ(), arena1.getSpawnLocationOne().getYaw(), arena1.getSpawnLocationOne().getPitch());
                Location newArenaSpawnLocationTwo = new Location(map.getWorld(), arena1.getSpawnLocationTwo().getX(), arena1.getSpawnLocationTwo().getY(), arena1.getSpawnLocationTwo().getZ(), arena1.getSpawnLocationTwo().getYaw(), arena1.getSpawnLocationTwo().getPitch());

                Arena newArena = new Arena(map.getWorld().getName(), map.getWorld().getName().toUpperCase(), newArenaSpawnLocationOne, newArenaSpawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
                toAdd.add(newArena);
                Bukkit.getLogger().info(Colorize.format("&aEVERYTHING LOADED IN PROPERLY (THE MAPS)"));
            }
        }

        for (Arena a1 : toAdd){
            plugin.getArenaManager().getDupArenaList().add(a1);
            getArenaManager().setArenaStatus(ArenaStatus.READY);
            Bukkit.getLogger().info(Colorize.format("&aEVERYTHING ADDED IN PROPERLY (THE MAP)"));
        }
    }

    public void registerClasses() {
        plugin = this;
        arena = new Arena();
        this.arenaManager = new ArenaManager(this);
        if (doesSWMExist()) {
            slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        }
        gameWorldsName = new ArrayList<>();
        slimeWorldsToUnload = new ArrayList<>();
    }

    public boolean doesSWMExist() {
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") != null) {
            return true;
        }
        return false;
    }

    public void cleanUpMaps() {
        for (File file : Objects.requireNonNull(serverFolder.listFiles())){
            if (file.getName().contains(".active.")){
                Bukkit.getConsoleSender().sendMessage(Colorize.format("&eDeleting map " + file.getName()));
                Bukkit.unloadWorld(file.getName(), false);
                FileUtil.delete(file);
            }
        }
    }

    public void cleanUpArenas() {
        if (getArenaManager().getDupArenaList() != null) {
            for (Arena arena : getArenaManager().getDupArenaList()) {
                Bukkit.getConsoleSender().sendMessage(Colorize.format("&eDeleting arena " + arena.getDisplayName()));
                getArenaManager().deleteDupeArenaItself(arena);
            }
        }
    }

    public void initItems() {
        NetheriteBoots.init();
        NetheriteLeggings.init();
        NetheriteHelmet.init();
        NetheriteChestplate.init();
        NetheritePickaxe.init();
        NetheriteSword.init();
        Crossbowweapon.init();
    }
}