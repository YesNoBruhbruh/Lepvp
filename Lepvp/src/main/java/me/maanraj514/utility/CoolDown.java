package me.maanraj514.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@UtilityClass
public class CoolDown {
    public HashMap<UUID, Long> cooldowns;

    public void setupCoolDown() {
        cooldowns = new HashMap<>();
    }

    public void setCoolDown(Player player, int seconds) {
        long delay = (System.currentTimeMillis() + (seconds * 1000));
        cooldowns.put(player.getUniqueId(), delay);
    }

    public boolean checkCoolDown(Player player) {
        if(!cooldowns.containsKey(player.getUniqueId()) || cooldowns.get(player.getUniqueId()) <= System.currentTimeMillis()){
            return true;
        }
        return false;
    }

    public long timeLeft(Player player) {
        return (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }
}