package me.maanraj514.tasks;

import lombok.AllArgsConstructor;
import me.maanraj514.Arena.Arena;
import me.maanraj514.Arena.State.ActiveGameState;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class StartCountdownTask extends BukkitRunnable {

    private final Lepvp plugin;
    private Arena arena;
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
            player.sendTitle("§a" + secondsUntilStart + Message.Color(" &aSeconds left until the game starts!!!"), ""  + Message.Color("&aCRYSTAL PVP"));
        }

        secondsUntilStart--;
    }
}
