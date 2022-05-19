package me.maanraj514.slime;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;

public class SlimeGameMap implements SlimeInterface{
    private final Lepvp plugin;

    private final String worldName;
    private final String template;

    public SlimeGameMap(String worldName, String template, boolean loadOnInit, Lepvp plugin){
        this.worldName = worldName;
        this.template = template;
        this.plugin = plugin;

        if (loadOnInit) load(template, worldName);
    }

    @Override
    public void load(String templateWorldName, String newWorldName) {
        if (isLoaded()) return;
        World templateWorld = Bukkit.getWorld(templateWorldName);

        if (templateWorld == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(Colorize.format("&cTemplateWorld is null"));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try{
                SlimePropertyMap spm = new SlimePropertyMap();
                spm.setString(SlimeProperties.DIFFICULTY, "hard");
                SlimeLoader file = plugin.getSlime().getLoader("file");

                SlimeWorld world = plugin.getSlime().loadWorld(file, template, true, spm).clone(newWorldName + System.currentTimeMillis());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        plugin.getSlime().generateWorld(world);
                        System.out.println(world.getName());
                    } catch (IllegalArgumentException ex) {
                        Bukkit.getServer().getConsoleSender().sendMessage(Colorize.format("&cFailed to generate world " + worldName + ": " + ex.getMessage() + "."));
                    }
                });
            } catch (UnknownWorldException | CorruptedWorldException | IOException | NewerFormatException | WorldInUseException e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void unload() {
        if (getWorld() != null) {
            Bukkit.unloadWorld(getWorld(), false);
        }
    }

    @Override
    public void delete(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getSlime().getLoader("file").deleteWorld(name);
            } catch (UnknownWorldException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isLoaded() {
        return getWorld() != null;
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
}
