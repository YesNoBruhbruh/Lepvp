package me.maanraj514.utility;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Colorize {

    public static String format(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String stripColor(String str){
        return ChatColor.stripColor(str);
    }

    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(format(message));
    }
}
