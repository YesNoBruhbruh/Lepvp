package me.maanraj514.commands;

import lombok.AllArgsConstructor;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.TemporaryArena;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class ArenaCommand implements CommandExecutor {

    private Lepvp plugin;
    private Arena arena;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Colorize.sendMessage(sender, Message.MUST_BE_PLAYER_ERROR);
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("lepvp.admin")){
            Colorize.sendMessage(player, Message.NO_PERMISSION);
            return true;
        }

        if(args.length < 1) {
            Colorize.sendMessage(sender, Message.ARENA_SETUP);
            return true;
        }

        if(args[0].equalsIgnoreCase("setup")) {
            String arenaName = arena.arenaNameFromArgs(args);
            if (arenaName.isEmpty()){
                Colorize.sendMessage(player, "&cArena name can't be empty");
                return true;
            }
            Optional<Arena> optionalArena = plugin.getArenaManager().findArena(arenaName);
            TemporaryArena temporaryArena = optionalArena.map(TemporaryArena::new).orElseGet(() -> new TemporaryArena(arenaName));

            plugin.getArenaManager().getArenaSetupManager().addToSetup(player, temporaryArena);
        }else if (args[0].equalsIgnoreCase("delete")){
            String arenaName = arena.arenaNameFromArgs(args);
            Optional<Arena> optionalArena = plugin.getArenaManager().findArena(arenaName);
            if (!optionalArena.isPresent()) {
                Colorize.sendMessage(player, Message.NO_EXISTING_ARENA);
                return true;
            }

            plugin.getArenaManager().deleteArena(optionalArena.get());
            Colorize.sendMessage(player, "&cDeleted " + arenaName + ".");
        }else if (args[0].equalsIgnoreCase("list"))    {
            if(plugin.getArenaManager().getArenas().size() == 0) {
                Colorize.sendMessage(player, Message.NO_ARENA_SETUP);
                return true;
            }

            plugin.getArenaManager().getArenas().forEach(arena -> Colorize.sendMessage(player, "&a" + arena.getDisplayName()));

        }else{
            Colorize.sendMessage(sender, Message.ARENA_SETUP);
        }

        return true;
    }
}
