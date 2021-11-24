package me.maanraj514.Arena.State;

import me.maanraj514.Lepvp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WaitingArenaState extends ArenaState {

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        for (UUID playerUUID : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            player.setHealth(player.getMaxHealth());
            plugin.getArenaManager().getRollBackManager().restore(player, plugin);
        }

        getArena().getPlayers().clear();
    }
}
