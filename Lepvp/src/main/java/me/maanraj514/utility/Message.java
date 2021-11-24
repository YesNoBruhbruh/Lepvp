package me.maanraj514.utility;

import org.bukkit.ChatColor;

public class Message {

    public static String MUST_BE_PLAYER_ERROR = "&you must be player to use this command";

    public static String ARENA_SETUP = "&c/arena [setup|delete|list]";

    public static String NO_EXISTING_ARENA = "&cNo arena with that name exists";

    public static String NO_PERMISSION = "&cYou don't have permission to use this specific command lmao get gud.";

    public static String MOVE_SETUP_MODE = "&aMoved to setup mode for ";

    public static String NO_ARENA_SETUP = "&cNo arenas have been setup.";

    public static String Color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
