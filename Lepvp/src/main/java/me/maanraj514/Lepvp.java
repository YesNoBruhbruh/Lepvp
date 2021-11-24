package me.maanraj514;

import lombok.Getter;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.ArenaManager;
import me.maanraj514.Arena.ItemStacks.*;
import me.maanraj514.Arena.State.CommonStateListener;
import me.maanraj514.Slime.SlimeGameMap;
import me.maanraj514.Slime.SlimeInterface;
import me.maanraj514.commands.*;
import me.maanraj514.lobby.LobbyListener;
import me.maanraj514.map.LocalGameMap;
import me.maanraj514.map.MapInterface;
import me.maanraj514.utility.CoolDown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class Lepvp extends JavaPlugin {

    public static Lepvp plugin;
    private Arena arena;
    private MapInterface map;
    private SlimeInterface slimeInter;

    @Getter
    private ArenaManager arenaManager;

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        File gameMapsFolder = new File(getDataFolder(), "gameMaps");
        if (!gameMapsFolder.exists()) {
            gameMapsFolder.mkdirs();
        }
        map = new LocalGameMap(gameMapsFolder, "worl", true);

        super.onEnable();
        config.options().copyDefaults(true);
        saveConfig();

        CoolDown.setupCoolDown();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Plugin made by Maanraj514");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-----------------------");

        plugin = this;
        arena = new Arena();
        slimeInter = new SlimeGameMap(this);

        this.arenaManager = new ArenaManager(this);

        slimeInter = new SlimeGameMap("arena1", "arena1_clone", true, plugin);
        slimeInter = new SlimeGameMap("arena2", "arena2_clone", true, plugin);

        NetheriteBoots.init();
        NetheriteLeggings.init();
        NetheriteHelmet.init();
        NetheriteChestplate.init();
        NetheritePickaxe.init();
        NetheriteSword.init();
        Crossbowweapon.init();

        getCommand("slimeCommandLoad").setExecutor(new SlimeCommand(slimeInter));
        getCommand("unload").setExecutor(new UnloadCommand(slimeInter, this));
        getCommand("test").setExecutor(new CloneCommand(this));
        getCommand("reset").setExecutor(new ResetWorld(map));
        getCommand("setLobby").setExecutor(new SetLobbyCommand(this));
        getCommand("arena").setExecutor(new ArenaCommand(this, arena));
        getCommand("duels").setExecutor(new DuelCommand(this, arena));

        getServer().getPluginManager().registerEvents(new CommonStateListener(arena, plugin), this);
        getServer().getPluginManager().registerEvents(new LobbyListener(arena, plugin), this);
    }

    @Override
    public void onDisable() {
        map.unload();
        slimeInter.unload();
        super.onDisable();

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Thanks for using my plugin, -- Maanraj514");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "-----------------------");
    }
}