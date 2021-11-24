package me.maanraj514.duel;


import org.bukkit.entity.Player;

public class Duel {

    private DuelTeam teamOne;
    private DuelTeam teamTwo;

    private DuelType type;

    public void join(Player player) {

    }

    public void quit(Player player) {

    }

    public void start() {

    }

    public boolean isPlayer(Player player) {
        return teamOne.isPlayer(player) || teamTwo.isPlayer(player);
    }

    public void setType(DuelType duelType) {
        this.type = duelType;
    }
}
