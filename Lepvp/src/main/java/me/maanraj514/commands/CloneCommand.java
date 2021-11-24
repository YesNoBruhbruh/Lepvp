package me.maanraj514.commands;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CloneCommand implements CommandExecutor {
    private final Lepvp plugin;
    SlimePlugin slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    SlimePropertyMap properties = new SlimePropertyMap();

    public CloneCommand(Lepvp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (args.length == 0) {
            Colorize.sendMessage(sender, "&c/test <templateWorld> <newClone> <dataSource>");
            return true;
        }

        if (args.length > 1) {
            String worldName = args[1];
            World world = Bukkit.getWorld(worldName);

            if (world != null) {
                sender.sendMessage(ChatColor.RED + "World " + worldName + " is already loaded!");
                return true;
            }
            String templateWorldName = args[0];

            if (templateWorldName.equals(worldName)) {
                sender.sendMessage(ChatColor.RED + "The template world name cannot be the same as the cloned world one!");
                return true;
            }
            String dataSource = args[2];
            SlimeLoader loader = slime.getLoader(dataSource);

            if (loader == null) {
                sender.sendMessage(ChatColor.RED + "Unknown data source " + dataSource + "!");
                return true;
            }
            sender.sendMessage(ChatColor.GRAY + "Creating world " + ChatColor.YELLOW + worldName
                    + ChatColor.GRAY + " using " + ChatColor.YELLOW + templateWorldName + ChatColor.GRAY + " as a template...");
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    long start = System.currentTimeMillis();

                    int max = 4000;
                    int min = 1;

                    int output = (int) Math.floor(Math.random()*(max-min+1)+min);
                    SlimeWorld slimeWorld = slime.loadWorld(loader, templateWorldName, true, properties).clone(worldName + output, loader);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        try {
                            slime.generateWorld(slimeWorld);
                        } catch (IllegalArgumentException ex) {
                            sender.sendMessage(ChatColor.RED + "Failed to generate world " + worldName + ": " + ex.getMessage() + ".");

                            return;
                        }

                        sender.sendMessage(ChatColor.GREEN + "World " + ChatColor.YELLOW + worldName
                                + ChatColor.GREEN + " loaded and generated in " + (System.currentTimeMillis() - start) + "ms!");
                    });
                } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException | WorldAlreadyExistsException ex) {
                    sender.sendMessage("something sussy happened :///// >:)))))");
                    ex.printStackTrace();
                }
            });
        }
        return true;
    }
}