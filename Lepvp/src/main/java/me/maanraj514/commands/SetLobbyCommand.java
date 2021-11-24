package me.maanraj514.commands;

import me.maanraj514.Lepvp;
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
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (command.getName().equalsIgnoreCase("setLobby")) {
            if (!p.isOp()) {
                return true;
            }
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