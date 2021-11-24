package me.maanraj514.Slime;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.maanraj514.Lepvp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;

public class SlimeGameMap implements SlimeInterface {
    private final Lepvp plugin;
    SlimePlugin slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

    public SlimeGameMap(Lepvp plugin) {
        this.plugin = plugin;
    }
    int max = 40;
    int min = 1;

    int output = (int) Math.floor(Math.random()*(max-min+1)+min);

    String worldName;
    String worldNameTemplate;
    boolean loadOnInit;

    public SlimeGameMap(String worldNameTemplate, String worldName, boolean loadOnInit, Lepvp plugin) {
        this.plugin = plugin;
        this.worldName = worldName;
        this.worldNameTemplate = worldNameTemplate;
        this.loadOnInit = loadOnInit;
        if (loadOnInit) load(worldNameTemplate, worldName);
    }

    @Override
    public void load(String template, String worldName) {
        this.worldName = worldName;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {
                SlimePropertyMap spm = new SlimePropertyMap();
                SlimeLoader file = slime.getLoader("file");

                SlimeWorld world = slime.loadWorld(file, template, true, spm).clone(worldName);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        slime.generateWorld(world);
                    } catch (IllegalArgumentException ex) {
                        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Failed to generate world " + worldName + ": " + ex.getMessage() + ".");
                    }
                });
            } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException ex) {
                plugin.getServer().getConsoleSender().sendMessage(ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void unload() {
        if (worldName != null) {
            Bukkit.unloadWorld(worldName, false);
        }
    }

    @Override
    public void unloadWorld(String world) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.unloadWorld(world, false);
        });
    }

    @Override
    public void cloneArena(String name1, String name2) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            SlimePropertyMap spm = new SlimePropertyMap();
            try {
                // Note that this method should be called asynchronously
                SlimeWorld world = slime.loadWorld(slime.getLoader("file"), name1, true, spm);
                world.clone(name2, slime.getLoader("file"));
            } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException | WorldAlreadyExistsException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void deleteWorld(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                slime.getLoader("file").deleteWorld(name);
            } catch (UnknownWorldException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isWorld(String name) {
        try {
            return slime.getLoader("file").worldExists(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
