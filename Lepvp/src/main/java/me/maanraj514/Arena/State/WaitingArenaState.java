package me.maanraj514.Arena.State;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WaitingArenaState extends ArenaState {

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        for (UUID playerUUID : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.setHealth(player.getMaxHealth());
            getArena().removeFromScoreboard(player);
            plugin.getArenaManager().getRollBackManager().restore(player, plugin);
        }

        getArena().getPlayers().clear();
    }
}