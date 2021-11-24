package me.maanraj514.map;

import org.bukkit.World;

public interface MapInterface {
    boolean load();
    void unload();
    boolean restoreFromSource();

    boolean isLoaded();
    World getWorld();
}
