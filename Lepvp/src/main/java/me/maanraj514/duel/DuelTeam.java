package me.maanraj514.duel;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelTeam {

    @Getter
    private final List<UUID> playerList = new ArrayList<>();

    @Getter
    private final List<UUID> alivePlayers = new ArrayList<>();


    public boolean isPlayer(Player player) {
        return playerList.contains(player.getUniqueId());
    }
}
