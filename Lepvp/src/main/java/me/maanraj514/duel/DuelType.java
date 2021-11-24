package me.maanraj514.duel;

public enum DuelType {

    CRYSTAL;

    public int maxTeamSize() {
        switch (this) {
            case CRYSTAL:
                return 4;
        }

        return 1;
    }
}
