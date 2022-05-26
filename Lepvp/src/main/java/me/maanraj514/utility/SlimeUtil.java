package me.maanraj514.utility;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lombok.experimental.UtilityClass;
import me.maanraj514.Lepvp;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@UtilityClass
public class SlimeUtil {
    private final Random random = new Random();

    public void importWorld(String worldName, File worldDir, Lepvp plugin) {
        SlimeLoader loader = plugin.getSlime().getLoader("file");
        try{
            if (!loader.worldExists(worldName)){
                plugin.getSlime().asyncImportWorld(worldDir, worldName, loader);
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
