package me.maanraj514.slime;

import org.bukkit.World;

public interface SlimeInterface {
    void load(String templateWorldName, String newWorldName);
    void unload();
    void delete(String name);

    boolean isLoaded();
    World getWorld();
}
