package me.maanraj514.duel;

import me.maanraj514.utility.Colorize;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DuelManager {

    private List<Duel> duelList = new ArrayList<>();

    public void sendRequest(Player player, Player otherPlayer, DuelType duelType) {
        if (duelFor(player).isPresent()){
            Colorize.sendMessage(player, "&cYou can't enter a duel while you're in one. /duel quit");
            return;
        }

        if (duelFor(otherPlayer).isPresent()){
            Colorize.sendMessage(player, "&cThat player is already in a duel");
            return;
        }
    }

    public Optional<Duel> duelFor(Player player) {
        return duelList.stream().filter(duel -> duel.isPlayer(player)).findAny();
    }
}
