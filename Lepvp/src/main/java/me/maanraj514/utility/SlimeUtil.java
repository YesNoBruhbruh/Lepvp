package me.maanraj514.utility;

import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lombok.experimental.UtilityClass;
import me.maanraj514.Lepvp;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class SlimeUtil {

    public void importWorld(String worldName, File worldDir, Lepvp plugin) {
        SlimeLoader loader = plugin.getSlime().getLoader("file");
        try{
            if (!loader.worldExists(worldName)){
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    try{
                        plugin.getSlime().importWorld(worldDir, worldName, loader);
                        System.out.println("test");
                    } catch (WorldAlreadyExistsException | WorldLoadedException | WorldTooBigException | IOException | InvalidWorldException ex){
                        ex.printStackTrace();
                    }
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadWorld(String worldName, String newWorldName, Lepvp plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                SlimeLoader loader = plugin.getSlime().getLoader("file");
                SlimePropertyMap spm = new SlimePropertyMap();

                spm.setValue(SlimeProperties.DIFFICULTY, "hard");
                spm.setValue(SlimeProperties.ALLOW_ANIMALS, false);
                spm.setValue(SlimeProperties.ALLOW_MONSTERS, false);

                SlimeWorld slimeWorld = plugin.getSlime().loadWorld(loader, worldName, true, spm).clone(newWorldName);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getSlime().generateWorld(slimeWorld);
                });
            }catch (CorruptedWorldException | NewerFormatException | WorldInUseException | UnknownWorldException | IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void unloadWorld(String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            return;
        }
        Bukkit.unloadWorld(worldName, false);
    }
}
