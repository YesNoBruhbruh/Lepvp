package me.maanraj514.commands;

import lombok.var;
import me.maanraj514.Lepvp;
import me.maanraj514.Slime.SlimeInterface;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.CoolDown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnloadCommand implements CommandExecutor {
    private final SlimeInterface slimeInter;
    private final Lepvp plugin;

    public UnloadCommand(SlimeInterface slimeInter, Lepvp plugin) {
        this.slimeInter = slimeInter;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player play = (Player) sender;

        if (CoolDown.checkCoolDown(play)) {
            if (args.length == 0) {
                Colorize.sendMessage(play, "&c/unload <worldName>");
                return true;
            }
            String worldName = args[0];
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                Colorize.sendMessage(play, "&cWorld " + worldName + " &cis not loaded!!!!");
                return true;
            }
            var players = world.getPlayers();

            if (!players.isEmpty()) {
                Location spawnLocation = Bukkit.getWorld("lobby").getSpawnLocation();
                players.forEach(player -> player.teleport(spawnLocation));
            }

            if (!Bukkit.unloadWorld(world, true)) {
                Colorize.sendMessage(play, "&cFailed to unload world " + worldName + ".");
                return true;
            }
            Colorize.sendMessage(play, "&aUnloading world " + worldName + "&a.");
            Bukkit.getScheduler().runTask(plugin, () -> {
                slimeInter.unloadWorld(worldName);
            });
            play.sendMessage(ChatColor.GREEN + "World " + ChatColor.YELLOW + worldName + ChatColor.GREEN + " unloaded correctly.");
            CoolDown.setCoolDown(play, 3);
            return true;
        }
        Colorize.sendMessage(play, "&cYou can use that command again in 3 seconds!");
        return true;
    }
}
