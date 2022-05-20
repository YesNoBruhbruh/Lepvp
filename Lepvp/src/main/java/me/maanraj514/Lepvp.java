package me.maanraj514;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.InvalidWorldException;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.exceptions.WorldLoadedException;
import com.grinderwolf.swm.api.exceptions.WorldTooBigException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import lombok.Getter;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.ArenaManager;
import me.maanraj514.Arena.ItemStacks.*;
import me.maanraj514.Arena.State.CommonStateListener;
import me.maanraj514.Arena.State.WaitingArenaState;
import me.maanraj514.commands.*;
import me.maanraj514.map.LocalGameMap;
import me.maanraj514.map.MapInterface;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.CoolDown;
import me.maanraj514.utility.SlimeUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
    private Arena newArena;

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

        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") != null) {
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
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") != null){
            for (World world : getArenaManager().getGameWorlds()) {
                Bukkit.unloadWorld(world, false);
            }
        }
        if (Bukkit.getWorld("test") != null) {
            SlimeUtil.unloadWorld("test");
            System.out.println("test");
        }
        if (map != null){
            map.unload();
            Bukkit.getConsoleSender().sendMessage(Colorize.format("&cUnloaded the map"));
            if (newArena != null) {
                plugin.getArenaManager().deleteDupeArenaItself(newArena);
                Bukkit.getLogger().info(Colorize.format("&aDeleted arena"));
            }
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

    public void loadGameWorlds() {
        for (Arena arena : getArenaManager().getSourceArenaList()) {
            World world = new WorldCreator(arena.getDisplayName()).createWorld();
            getArenaManager().getGameWorlds().add(world);
            if (world != null) Bukkit.unloadWorld(world, false);
        }
    }

    public void doSlimeStuff() {
        List<Arena> toAdd = new ArrayList<>();

        loadGameWorlds();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (World world : getArenaManager().getGameWorlds()) {
                SlimeUtil.importWorld(world.getName(), new File(serverFolder + File.separator + world.getName()), this);
            }
            Bukkit.getScheduler().runTaskLater(this, () -> {
                SlimeUtil.loadWorld("test", this);
                for (World world : getArenaManager().getGameWorlds()){
                    SlimeUtil.loadWorld(world.getName(), this);
                }
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    for (Arena arena : plugin.getArenaManager().getSourceArenaList()){
                        String arenaName = arena.getDisplayName();
                        for (World world : getArenaManager().getGameWorlds()) {
                            if (world.getName().equalsIgnoreCase(arenaName)){
                                Location newArenaSpawnLocationOne = new Location(world, 41.65894194008685, 64.0, 16.428342833644226, (float) -176.24797, (float) -9.477018);
                                Location newArenaSpawnLocationTwo = new Location(world, 40.81675029565928, 63.0, 8.29202435357428, (float) 174.53975, (float) 9.949412);

                                newArena = new Arena(world.getName(), world.getName().toUpperCase(), newArenaSpawnLocationOne, newArenaSpawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
                                toAdd.add(newArena);
                                Bukkit.getLogger().info(Colorize.format("&aEVERYTHING LOADED IN PROPERLY (THE SLIME MAPS)"));
                            }
                        }
                    }
                    for (Arena a1 : toAdd){
                        plugin.getArenaManager().getDupArenaList().add(a1);
                        Bukkit.getLogger().info(Colorize.format("&aEVERYTHING ADDED IN PROPERLY (THE SLIME MAP)"));
                    }
                }, 20*5);
            }, 20*5);
        }, 20*5);
    }

    public void doMapStuff() {

        List<Arena> toAdd = new ArrayList<>();

        for (Arena arena1 : plugin.getArenaManager().getSourceArenaList()) {
            String arena1Name = arena1.getDisplayName();
            File mapToReset = new File(arena1Name);
            if (mapToReset.exists()){
                map = new LocalGameMap(serverFolder, arena1Name, true);

                Location newArenaSpawnLocationOne = new Location(map.getWorld(), arena1.getSpawnLocationOne().getX(), arena1.getSpawnLocationOne().getY(), arena1.getSpawnLocationOne().getZ(), arena1.getSpawnLocationOne().getYaw(), arena1.getSpawnLocationOne().getPitch());
                Location newArenaSpawnLocationTwo = new Location(map.getWorld(), arena1.getSpawnLocationTwo().getX(), arena1.getSpawnLocationTwo().getY(), arena1.getSpawnLocationTwo().getZ(), arena1.getSpawnLocationTwo().getYaw(), arena1.getSpawnLocationTwo().getPitch());

                newArena = new Arena(map.getWorld().getName(), map.getWorld().getName().toUpperCase(), newArenaSpawnLocationOne, newArenaSpawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
                toAdd.add(newArena);
                Bukkit.getLogger().info(Colorize.format("&aEVERYTHING LOADED IN PROPERLY (THE MAPS)"));
            }
        }

        for (Arena a1 : toAdd){
            plugin.getArenaManager().getDupArenaList().add(a1);
            Bukkit.getLogger().info(Colorize.format("&aEVERYTHING ADDED IN PROPERLY (THE MAP)"));
        }
    }

    public void registerClasses() {
        plugin = this;
        arena = new Arena();
        this.arenaManager = new ArenaManager(this);
        slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
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