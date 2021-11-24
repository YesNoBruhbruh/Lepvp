package me.maanraj514.commands;

import me.maanraj514.map.MapInterface;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.CoolDown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetWorld implements CommandExecutor {
    private final MapInterface map;

    public ResetWorld(MapInterface map) {
        this.map = map;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("reset")) {
            if (CoolDown.checkCoolDown(player)) {
                map.unload();
                map.load();
                System.out.println(map.getWorld());

                Location loc = new Location(Bukkit.getWorld("worl"), 1, 1, 1);
                player.teleport(loc);
                Colorize.sendMessage(player, "&cThe world is resetting...");
                CoolDown.setCoolDown(player, 3);
            }
            Colorize.sendMessage(player, "&cYou can use that command again in 3 seconds!");
            return true;
        }
        return true;
    }
}
