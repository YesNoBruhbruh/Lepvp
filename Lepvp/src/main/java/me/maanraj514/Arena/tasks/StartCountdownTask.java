package me.maanraj514.Arena.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.State.ActiveGameState;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class StartCountdownTask extends BukkitRunnable {

    private final Lepvp plugin;
    private Arena arena;
    @Getter
    private int secondsUntilStart = 5;

    @Override
    public void run() {
        if (secondsUntilStart <= 0) {
            arena.setState(new ActiveGameState(), plugin);
            cancel();
            return;
        }

        for (UUID playerUUID : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.sendTitle(Colorize.format("&a" + secondsUntilStart + " &aSeconds left until the game starts!!!"), Colorize.format("&aCRYSTAL PVP"), 0, 20, 0);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1f);
        }
        secondsUntilStart--;
    }
}
