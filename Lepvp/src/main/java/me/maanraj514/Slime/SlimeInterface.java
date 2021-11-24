package me.maanraj514.Slime;

import me.maanraj514.Arena.Arena;

public interface SlimeInterface {
    void load(String template, String world);

    void unload();

    void unloadWorld(String world);

    void cloneArena(String name1, String name2);

    void deleteWorld(String name);

    boolean isWorld(String name);
}
