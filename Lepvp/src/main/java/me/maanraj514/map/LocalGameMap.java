package me.maanraj514.map;

import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.FileUtil;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

public class LocalGameMap implements MapInterface{

    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private World bukkitWorld;

    public LocalGameMap(File worldFolder, String worldName, boolean loadOnInit) {
        this.sourceWorldFolder = new File(
                worldFolder,
                worldName
        );

        if (loadOnInit) load();
    }

    // Utility

    @Override
    public boolean load() {
        if (isLoaded()) return true;
        this.activeWorldFolder = new File(
                Bukkit.getWorldContainer().getParentFile(), sourceWorldFolder.getName() + ".active." + System.currentTimeMillis());
        try {
            FileUtil.copy(sourceWorldFolder, activeWorldFolder);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to load GameMap from source folder " + sourceWorldFolder.getName());
            e.printStackTrace();
            return false;
        }

        this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));

        if (bukkitWorld != null) {
            this.bukkitWorld.setDifficulty(Difficulty.HARD);
            this.bukkitWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            this.bukkitWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            this.bukkitWorld.setAutoSave(false);
        }
        return isLoaded();
    }

    @Override
    public void unload() {
        if (bukkitWorld != null){
            if (activeWorldFolder != null) {
                Bukkit.unloadWorld(bukkitWorld, false);
                FileUtil.delete(activeWorldFolder);
            }
        }
        bukkitWorld = null;
        activeWorldFolder = null;
    }

    @Override
    public void delete(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            return;
        }
        File activeWorldFolder = world.getWorldFolder();
        if (activeWorldFolder.exists()){
            Bukkit.unloadWorld(world, false);
            FileUtil.delete(activeWorldFolder);
            Bukkit.getConsoleSender().sendMessage(Colorize.format("&aSuccessfully deleted the world and the world file"));
        }
    }

    @Override
    public boolean restoreFromSource() {
        unload();
        return load();
    }

    @Override
    public boolean isLoaded() {
        return getWorld() != null;
    }

    @Override
    public World getWorld() {
        return bukkitWorld;
    }
}
