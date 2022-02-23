package me.maanraj514.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@UtilityClass
public class Colorize {

    public String format(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public String stripColor(String str){
        return ChatColor.stripColor(str);
    }

    public void sendMessage(CommandSender sender, String message){
        sender.sendMessage(format(message));
    }
}
