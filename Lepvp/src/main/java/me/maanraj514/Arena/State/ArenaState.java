package me.maanraj514.Arena.State;

import me.maanraj514.Arena.Arena;
import me.maanraj514.Lepvp;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ArenaState implements Listener {

    private Arena arena;

    public void onEnable(Lepvp plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onDisable(){
        HandlerList.unregisterAll(this);
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public Arena getArena(){
        return arena;
    }
}
