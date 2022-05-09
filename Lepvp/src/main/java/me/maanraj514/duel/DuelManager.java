package me.maanraj514.duel;

import me.maanraj514.utility.Colorize;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DuelManager {

    private final List<Duel> duelList = new ArrayList<>();

    public void sendRequest(Player player, Player otherPlayer, DuelType duelType) {
        if (duelFor(player) != null){
            Colorize.sendMessage(player, "&cYou can't enter a duel while you're in one. /duel quit");
            return;
        }

        if (duelFor(otherPlayer) != null){
            Colorize.sendMessage(player, "&cThat player is already in a duel");
            return;
        }
    }

    public Duel duelFor(Player player) {
        for (Duel duel : duelList){
            if (duel.isPlayer(player)){
                return duel;
            }
        }
        return null;
    }
}
