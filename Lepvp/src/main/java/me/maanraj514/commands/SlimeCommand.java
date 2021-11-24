package me.maanraj514.commands;

import me.maanraj514.Slime.SlimeInterface;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.CoolDown;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlimeCommand implements CommandExecutor {
    private final SlimeInterface slimeInter;

    public SlimeCommand(SlimeInterface slimeInter) {
        this.slimeInter = slimeInter;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        if (CoolDown.checkCoolDown(player)) {
            if (args.length == 0) {
                Colorize.sendMessage(player, "&c/slimeCommandLoad <templateWorldName> <worldName>");
                return true;
            }
            if (args.length == 1) {
                Colorize.sendMessage(player, "&c/slimeCommandLoad <templateWorldName> <worldName>");
                return true;
            }
            String template = args[0];
            World w = Bukkit.getWorld(template);

            String worldName = args[1];
            if (w == null) {
                Colorize.sendMessage(player, "&cThat world doesnt exist bozo L walking L life");
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("slimeCommandLoad") || args[0].equalsIgnoreCase(template) || args[1].equalsIgnoreCase(worldName)) {
                slimeInter.load(template, worldName);
                player.sendMessage("successfully made " + worldName);
                CoolDown.setCoolDown(player, 3);
                return true;
            }
            Colorize.sendMessage(player, "&cYou can use that command again in 3 seconds!");
            return true;
        }
        return true;
    }
}
