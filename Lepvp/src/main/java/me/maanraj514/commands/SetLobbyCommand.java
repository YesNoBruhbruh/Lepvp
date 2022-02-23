package me.maanraj514.commands;

import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SetLobbyCommand implements CommandExecutor {
    static Lepvp plugin;

    public SetLobbyCommand(Lepvp plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("lepvp.admin")){
            Bukkit.getLogger().info(Colorize.format("&cYou don't have the permission to use this command!"));
            return true;
        }
        if (command.getName().equalsIgnoreCase("setLobby")) {
            if (plugin.getConfig().get("player-location") != null) {
                return true;
            } else {
                plugin.getConfig().set("player-location", p.getLocation());
                p.sendTitle("Lobby Location", "Has Been Set", 1, 20, 1);
                plugin.saveConfig();
                plugin.reloadConfig();
                return true;
            }
        }
        return true;
    }
}