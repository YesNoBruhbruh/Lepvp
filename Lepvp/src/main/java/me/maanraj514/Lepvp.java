package me.maanraj514;

import com.grinderwolf.swm.api.SlimePlugin;
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

    @Getter
    List<World> gameWorlds;
    @Getter
    List<World> slimeWorldsToUnload;
    @Getter
    List<LocalGameMap> gameMapsToUnload;
    @Getter
    List<Arena> arenasToUnload;

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
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") != null) {

        }else{
            for (LocalGameMap gameMap : gameMapsToUnload) {
                map.delete(gameMap.getWorld().getName());
                Bukkit.getConsoleSender().sendMessage(Colorize.format("&cUnloaded the map " + gameMap.getWorld().getName()));
            }
            for (Arena arena : arenasToUnload) {
                plugin.getArenaManager().deleteDupeArenaItself(newArena);
                Bukkit.getLogger().info(Colorize.format("&aDeleted arena ") + arena.getDisplayName());
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

    public void doSlimeStuff() {
        List<Arena> toAdd = new ArrayList<>();

        for (Arena arena : getArenaManager().getSourceArenaList()) {
            World world = new WorldCreator(arena.getDisplayName()).createWorld();
            gameWorlds.add(world);
            if (world != null) Bukkit.unloadWorld(world, false);
        }
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (World w : gameWorlds) {
                SlimeUtil.importWorld(w.getName(), new File(serverFolder + File.separator + w.getName()), this);
            }
            Bukkit.getScheduler().runTaskLater(this, () -> {
                for (World w : gameWorlds){
                    SlimeUtil.loadWorld(w.getName(), this);
                }
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    for (Arena arena : getArenaManager().getSourceArenaList()) {
                        String arenaName = arena.getDisplayName();
                        for (World w : gameWorlds) {
                            if (arenaName.equalsIgnoreCase(w.getName())){
                                Location newArenaLocationOne = new Location(w, arena.getSpawnLocationOne().getX(), arena.getSpawnLocationOne().getY(), arena.getSpawnLocationOne().getZ(), arena.getSpawnLocationOne().getYaw(), arena.getSpawnLocationOne().getPitch());
                                Location newArenaLocationTwo = new Location(w, arena.getSpawnLocationTwo().getX(), arena.getSpawnLocationTwo().getY(), arena.getSpawnLocationTwo().getZ(), arena.getSpawnLocationTwo().getYaw(), arena.getSpawnLocationTwo().getPitch());

                                newArena = new Arena(w.getName(), w.getName().toUpperCase(), newArenaLocationOne, newArenaLocationTwo, new WaitingArenaState(), new ArrayList<>());
                                toAdd.add(newArena);
                                slimeWorldsToUnload.add(w);
                                arenasToUnload.add(newArena);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "ADDED SLIME MAP " + w.getName());
                            }
                        }
                    }
                    for (Arena a : toAdd) {
                        plugin.getArenaManager().getDupArenaList().add(a);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "LOADED SLIME MAP " + a.getDisplayName());
                    }
                }, 20*4);
            }, 20*3);
        }, 20*3);
    }

    public void doMapStuff() {

        List<Arena> toAdd = new ArrayList<>();

        for (Arena arena1 : plugin.getArenaManager().getSourceArenaList()) {
            String arena1Name = arena1.getDisplayName();
            File mapToReset = new File(arena1.getDisplayName());
            if (mapToReset.exists()){
                map = new LocalGameMap(serverFolder, arena1Name, true);

                Location newArenaSpawnLocationOne = new Location(map.getWorld(), arena1.getSpawnLocationOne().getX(), arena1.getSpawnLocationOne().getY(), arena1.getSpawnLocationOne().getZ(), arena1.getSpawnLocationOne().getYaw(), arena1.getSpawnLocationOne().getPitch());
                Location newArenaSpawnLocationTwo = new Location(map.getWorld(), arena1.getSpawnLocationTwo().getX(), arena1.getSpawnLocationTwo().getY(), arena1.getSpawnLocationTwo().getZ(), arena1.getSpawnLocationTwo().getYaw(), arena1.getSpawnLocationTwo().getPitch());

                newArena = new Arena(map.getWorld().getName(), map.getWorld().getName().toUpperCase(), newArenaSpawnLocationOne, newArenaSpawnLocationTwo, new WaitingArenaState(), new ArrayList<>());
                toAdd.add(newArena);
                gameMapsToUnload.add((LocalGameMap) map);
                arenasToUnload.add(newArena);
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
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") != null) {
            slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        }

        gameMapsToUnload = new ArrayList<>();
        arenasToUnload = new ArrayList<>();
        gameWorlds = new ArrayList<>();
        slimeWorldsToUnload = new ArrayList<>();
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