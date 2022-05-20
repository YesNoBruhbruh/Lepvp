package me.maanraj514.utility;

import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lombok.experimental.UtilityClass;
import me.maanraj514.Lepvp;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class SlimeUtil {

    public void importWorld(String worldName, File worldDir, SlimeLoader loader, Lepvp plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Note that this method should be called asynchronously
                plugin.getSlime().importWorld(worldDir, worldName, loader);
                Bukkit.getConsoleSender().sendMessage(Colorize.format("&aSuccessfully imported the world named " + "worl"));
            } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException | WorldTooBigException | IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void loadWorld(String worldName, SlimeLoader loader, Lepvp plugin) {
        SlimePropertyMap spm = new SlimePropertyMap();
        spm.setString(SlimeProperties.DIFFICULTY, "hard");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {

            }catch () {
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
