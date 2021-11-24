package me.maanraj514.commands;

import lombok.AllArgsConstructor;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class DuelCommand implements CommandExecutor {

    private Lepvp plugin;
    private Arena arena;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Colorize.sendMessage(sender, Message.MUST_BE_PLAYER_ERROR);
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            Colorize.sendMessage(player, "&cCant be empty bozo");
            return true;
        }

        String args1 = args[0];

        switch (args1) {
            case "join":
            case "j":
                Optional<Arena> optionalArenaFind = plugin.getArenaManager().findOpenArena();
                if (!optionalArenaFind.isPresent()) {
                    Colorize.sendMessage(player, "&cThat arena doesn't exist or there isn't a available arena");
                    return true;
                }

                if (plugin.getArenaManager().findPlayerName(player).isPresent()) {
                    Colorize.sendMessage(player, "&cYou are already in that arena bozo");
                    return true;
                }

                optionalArenaFind.get().addPlayer(player, plugin);
                break;
            case "quit":
            case "q":
                Optional<Arena> playerArena = plugin.getArenaManager().findPlayerName(player);
                if (!playerArena.isPresent()) {
                    Colorize.sendMessage(player, "&cYou're not in an arena");
                    return true;
                }

                Arena arena = playerArena.get();
                arena.removePlayer(player, plugin);
                Colorize.sendMessage(player, "&cYou left the arena");
                break;
        }

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("goIn")) {
                String arenaName = arena.arenaNameFromArgs(args);
                Optional<Arena> optionalArenaFindOpen = plugin.getArenaManager().findOpenArenaSpecific(arenaName);
                if (!optionalArenaFindOpen.isPresent()) {
                    Colorize.sendMessage(player, "&cThat arena doesn't exist or if you got this error message then you should do /duels join <arenaname>");
                    return true;
                }

                if (plugin.getArenaManager().findPlayerName(player).isPresent()) {
                    Colorize.sendMessage(player, "&cYou are already in that arena bozo");
                    return true;
                }

                optionalArenaFindOpen.get().addPlayer(player, plugin);
            }
        }
        return true;
    }
}