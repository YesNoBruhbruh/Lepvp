package me.maanraj514.commands;

import lombok.AllArgsConstructor;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.TemporaryArena;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ArenaCommand implements CommandExecutor {

    private Lepvp plugin;
    private Arena arena;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Colorize.sendMessage(sender, "&cyou must be player to use this command");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("lepvp.admin")){
            Colorize.sendMessage(player, "&cYou don't have permission to use this specific command lmao get gud.");
            return true;
        }

        if(args.length < 1) {
            Colorize.sendMessage(sender, "&c/arena [setup|delete|list]");
            return true;
        }

        if(args[0].equalsIgnoreCase("setup")) {
            String arenaName = arena.arenaNameFromArgs(args);
            if (arenaName.isEmpty()){
                Colorize.sendMessage(player, "&cArena name can't be empty");
                return true;
            }
            Arena arena = plugin.getArenaManager().findSpecificArena(arenaName);
            TemporaryArena temporaryArena = new TemporaryArena(arenaName);

            plugin.getArenaManager().getArenaSetupManager().addToSetup(player, temporaryArena);
        }else if (args[0].equalsIgnoreCase("delete")){
            String arenaName = arena.arenaNameFromArgs(args);
            Arena arena = plugin.getArenaManager().findSpecificArena(arenaName);
            if (arena == null) {
                Colorize.sendMessage(player, "&cNo arena with that name exists");
                return true;
            }

            plugin.getArenaManager().deleteArenaFromEverything(arena);
            Colorize.sendMessage(player, "&cDeleted " + arenaName + ".");
        }else if (args[0].equalsIgnoreCase("list"))    {
            if(plugin.getArenaManager().getArenas().size() == 0) {
                Colorize.sendMessage(player, "&cNo arenas have been setup.");
                return true;
            }

            for (Arena arena : plugin.getArenaManager().getArenas()){
                Colorize.sendMessage(player, "&a" + arena.getDisplayName());
            }
        }else{
            Colorize.sendMessage(sender, "&c/arena [setup|delete|list]");
        }

        return true;
    }
}
