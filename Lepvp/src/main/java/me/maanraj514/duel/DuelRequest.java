package me.maanraj514.duel;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelRequest {

    private UUID requestorUUID;
    private List<UUID> requested = new ArrayList<>();

    public DuelRequest(Player player, Player otherPlayer) {
        requestorUUID = player.getUniqueId();
        requested.add(otherPlayer.getUniqueId());
    }

    public void accept(Player player) {

    }

    public void deny(Player player) {

    }
}
