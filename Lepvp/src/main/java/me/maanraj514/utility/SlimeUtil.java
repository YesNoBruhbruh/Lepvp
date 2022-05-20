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
                plugin.getSlime().asyncImportWorld(worldDir, worldName, loader);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadWorld(String worldName, Lepvp plugin) {
        SlimeLoader loader = plugin.getSlime().getLoader("file");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                SlimePropertyMap spm = new SlimePropertyMap();
                spm.setValue(SlimeProperties.DIFFICULTY, "hard");

                SlimeWorld slimeWorld = plugin.getSlime().loadWorld(loader, worldName, true, spm);
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
