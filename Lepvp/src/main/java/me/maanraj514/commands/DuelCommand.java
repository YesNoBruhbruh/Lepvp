package me.maanraj514.commands;

import lombok.AllArgsConstructor;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class DuelCommand implements CommandExecutor {

    private Lepvp plugin;
    private Arena arena;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Colorize.sendMessage(sender, "&cyou must be player to use this command");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            Colorize.sendMessage(player, "&cCant be empty bozo");
            Colorize.sendMessage(player, "&c/duels { randomJoin/rj | quit/q | join/j <arenaName> }");
            return true;
        }

        String args0 = args[0];

        switch (args0) {
            case "randomJoin":
            case "rj":
                Arena availableArenas = plugin.getArenaManager().findOpenDupArena();
                if (availableArenas == null) {
                    Colorize.sendMessage(player, "&cThat arena doesn't exist or there isn't a available arena");
                    return true;
                }

                if (plugin.getArenaManager().findPlayerName(player) != null) {
                    Colorize.sendMessage(player, "&cYou are already in that arena bozo");
                    return true;
                }

                availableArenas.addPlayer(player, plugin);
                break;
            case "quit":
            case "q":
                Arena playerArena = plugin.getArenaManager().findPlayerName(player);
                if (playerArena == null) {
                    Colorize.sendMessage(player, "&cYou're not in an arena");
                    return true;
                }

                playerArena.removePlayer(player, plugin);
                Colorize.sendMessage(player, "&cYou left the arena");
                break;
        }

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("j")) {
                String arenaName = arena.arenaNameFromArgs(args);
                Arena specificOpenArena = plugin.getArenaManager().findSpecificOpenDupArena(arenaName);
                if (specificOpenArena == null) {
                    Colorize.sendMessage(player, "&cThat arena doesn't exist or is not available");
                    return true;
                }

                if (plugin.getArenaManager().findPlayerName(player) != null) {
                    Colorize.sendMessage(player, "&cYou are already in that arena bozo");
                    return true;
                }
                specificOpenArena.addPlayer(player, plugin);
                Colorize.sendMessage(player, "&aYou are being sent to " + specificOpenArena.getDisplayName());
            }
        }
        return true;
    }
}