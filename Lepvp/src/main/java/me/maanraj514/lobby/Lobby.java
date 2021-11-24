package me.maanraj514.lobby;

import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {
    static Lepvp plugin;

    public Lobby(Lepvp plugin) {
        this.plugin = plugin;
    }

    private List<UUID> lobby;

    public Lobby() {
        this.lobby = new ArrayList<>();
    }

    public void addLobby(Player p) {
        this.lobby.add(p.getUniqueId());
        sendMessageToPlayer("&a" + p.getDisplayName() + " joined the Lobby!");
    }

    public void removeLobby(Player p) {
        this.lobby.remove(p.getUniqueId());
        sendMessageToPlayer("&c" + p.getDisplayName() + " left the Lobby!");
    }

    public boolean inList(Player p){
        this.lobby.contains(p.getUniqueId());
        return true;
    }

    public void sendMessageToPlayer(String message) {
        for (UUID playerUUID : lobby) {
            Player p = Bukkit.getPlayer(playerUUID);
            if (p == null) continue;

            Colorize.sendMessage(p, message);
        }
    }

    public void sendMessageToWorld(String message) {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.getWorld().getName().equalsIgnoreCase("lobby")){
                Colorize.sendMessage(p, message);
            }
        }
    }
}