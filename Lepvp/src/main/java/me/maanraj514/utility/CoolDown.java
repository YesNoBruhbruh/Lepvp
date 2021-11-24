package me.maanraj514.utility;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CoolDown {
    public static HashMap<UUID, Double> cooldowns;

    public static void setupCoolDown() {
        cooldowns = new HashMap<>();
    }

    public static void setCoolDown(Player player, int seconds) {
        double delay = (double)(System.currentTimeMillis() + (seconds * 1000));
        cooldowns.put(player.getUniqueId(), delay);
    }

    public static boolean checkCoolDown(Player player) {
        if(!cooldowns.containsKey(player.getUniqueId()) || cooldowns.get(player.getUniqueId()) <= System.currentTimeMillis()){
            return true;
        }
        return false;
    }
}