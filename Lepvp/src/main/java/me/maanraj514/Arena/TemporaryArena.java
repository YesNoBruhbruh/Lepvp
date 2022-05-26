package me.maanraj514.Arena;

import lombok.Data;
import me.maanraj514.Arena.State.WaitingArenaState;
import org.bukkit.Location;

import java.util.ArrayList;


@Data
public class TemporaryArena {

    private String displayName;
    private String configName;
    private Location spawnLocationOne;
    private Location spawnLocationTwo;

    public TemporaryArena(String displayName) {
        this.configName = displayName.replace(" ", "_").toUpperCase();
        this.displayName = displayName;
        this.spawnLocationOne = null;
        this.spawnLocationTwo = null;
    }

    public TemporaryArena(Arena arena) {
        this.configName = arena.getConfigName();
        this.displayName = arena.getDisplayName();
        this.spawnLocationOne = arena.getSpawnLocationOne();
        this.spawnLocationTwo = arena.getSpawnLocationTwo();
    }

    public Arena toArena() {
        if(spawnLocationOne == null || spawnLocationTwo == null){
            return null;
        }

        return new Arena(displayName, configName, spawnLocationOne, spawnLocationTwo, new WaitingArenaState());
    }
}
