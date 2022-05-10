package me.maanraj514;

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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public final class Lepvp extends JavaPlugin {

    public static Lepvp plugin;
    private Arena arena;
    private MapInterface map;

    @Getter
    private ArenaManager arenaManager;

    FileConfiguration config = getConfig();

    private Arena newArena;

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

        doMapStuff();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Plugin made by Maanraj514");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (map != null){
            map.unload();
            if (newArena != null) {
                plugin.getArenaManager().deleteArenaItself(newArena);
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

    public void doMapStuff() {
        getDataFolder().mkdirs();

        File gameMapsFolder = new File(getDataFolder(), "gameMaps");
        if (!gameMapsFolder.exists()) {
            gameMapsFolder.mkdirs();
        }

        List<Arena> toAdd = new ArrayList<>();

        for (Arena a : plugin.getArenaManager().getArenas()) {
            if (a.getDisplayName().equalsIgnoreCase("worl")){
                File mapToReset = new File(gameMapsFolder, "worl");
                if (mapToReset.exists()) {
                    Bukkit.getLogger().info(Colorize.format("&aWORLD < worl > is not null, creating duplicate... "));
                    map = new LocalGameMap(gameMapsFolder, a.getDisplayName(), true);

                    newArena = new Arena(map.getWorld().getName(), map.getWorld().getName().toUpperCase(), a.getSpawnLocationOne(), a.getSpawnLocationTwo(), new WaitingArenaState(), new ArrayList<>());
                    toAdd.add(newArena);
                    Bukkit.getLogger().info(Colorize.format("&aEVERYTHING LOADED IN PROPERLY (THE MAPS)"));
                }else{
                    Bukkit.getLogger().info(Colorize.format("&cWORLD < worl > is null, cancel creating duplicate..."));
                }
            }else{
                Bukkit.getLogger().info(Colorize.format("&cThere is no arena with the worl displayname"));
            }
        }

        for (Arena a1 : toAdd){
            plugin.getArenaManager().getArenas().add(a1);
            Bukkit.getLogger().info(Colorize.format("&aEVERYTHING ADDED IN PROPERLY (THE MAPS)"));
        }
    }

    public void registerClasses() {
        plugin = this;
        arena = new Arena();
        this.arenaManager = new ArenaManager(this);
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