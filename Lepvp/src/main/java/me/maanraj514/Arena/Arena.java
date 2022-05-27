package me.maanraj514.Arena;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import lombok.Data;
import me.maanraj514.Arena.State.*;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Arena {

    private String displayName;
    private String configName;
    private Location spawnLocationOne;
    private Location spawnLocationTwo;

    private ArenaState arenaState;

    private JPerPlayerScoreboard scoreboard;

    private List<UUID> players;

    public Arena() {
        this.players = new ArrayList<>();
    }

    public Arena(String displayName, String configName, Location spawnLocationOne, Location spawnLocationTwo, ArenaState arenaState, List<UUID> players) {
        this.displayName = displayName;
        this.configName =configName;
        this.spawnLocationOne = spawnLocationOne;
        this.spawnLocationTwo = spawnLocationTwo;
        this.arenaState = arenaState;
        this.players = players;
    }

    private final int MAX_PLAYERS = 2;
    private final int ONE_PLAYER = 1;

    public void setState(ArenaState arenaState, Lepvp plugin) {
        if(this.arenaState.getClass() == arenaState.getClass()) return;
        if (plugin.getArenaManager().getArenaStatus() == ArenaStatus.READY || plugin.getArenaManager().getArenaStatus() == ArenaStatus.PLAYING || plugin.getArenaManager().getArenaStatus() == ArenaStatus.WAITING) {
            this.arenaState.onDisable();
            this.arenaState = arenaState;
            this.arenaState.setArena(this);
            this.arenaState.onEnable(plugin);
        }else{
            Bukkit.getConsoleSender().sendMessage(Colorize.format("&cThe arena isnt ready yet or some other random error"));
        }
    }

    public void addPlayer(Player player, Lepvp plugin) {
        if (plugin.getArenaManager().getArenaStatus() == ArenaStatus.READY){
            this.players.add(player.getUniqueId());
            sendMessage("&a" + player.getDisplayName() + " joined the match.");

            plugin.getArenaManager().getRollBackManager().save(player);
            player.setGameMode(GameMode.SURVIVAL);

            if (players.size() == 1){
                player.teleportAsync(spawnLocationOne);
            } else {
                player.teleportAsync(spawnLocationTwo);
            }

            if (players.size() == MAX_PLAYERS) {
                setState(new StartingArenaState(), plugin);
            }

            if (players.size() == ONE_PLAYER) {
                setState(new WaitingArenaState(), plugin);
            }

            scoreboard = new JPerPlayerScoreboard(
                    (player1) -> "&5&lLEPVP",
                    (player1) -> {
                        if (arenaState instanceof WaitingArenaState){
                            System.out.println("arenastate is instanceof waiting arena state");
                            return getWaitingScoreboardLines();
                        }
                        removeFromScoreboard(player);
                        scoreboard.destroy();
                        return null;
                    }
            );
            if (arenaState instanceof WaitingArenaState) {
                addToScoreboard(player);
            }
        }
    }

    public void removePlayer(Player player, Lepvp plugin) {
        this.players.remove(player.getUniqueId());
        sendMessage("&c" + player.getDisplayName() + " has left the match.");
        removeFromScoreboard(player);

        plugin.getArenaManager().getRollBackManager().restore(player, plugin);

        if (arenaState instanceof ActiveGameState) {
            ((ActiveGameState) arenaState).getAlivePlayers().remove(player.getUniqueId());
        } else if (arenaState instanceof StartingArenaState) {
            setState(new WaitingArenaState(), plugin);
        }
    }

    public boolean isPlayer(Player player) {
        return player.getUniqueId() == player.getUniqueId();
    }

    public boolean inList(Player p){
        return this.players.contains(p.getUniqueId());
    }

    public void sendMessage(String message) {
        for (UUID playerUUID : players) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) {
                continue;
            }

            Colorize.sendMessage(player, message);
        }
    }

    public void removeFromScoreboard(Player player) {
        if (scoreboard != null) {
            scoreboard.removePlayer(player);
            scoreboard.updateScoreboard();
        }
    }

    public void addToScoreboard(Player player) {
        if (scoreboard != null) {
            scoreboard.addPlayer(player);
            scoreboard.updateScoreboard();
        }
    }

    public List<String> getWaitingScoreboardLines() {
        List<String> lines = new ArrayList<>();

        lines.add("");
        lines.add("Players: &6" + players.size() + "/" + getMAX_PLAYERS());
        lines.add("");
        lines.add("Waiting...");
        lines.add("");

        return lines;
    }

    public List<String> getActiveScoreboardLines() {
        List<String> lines = new ArrayList<>();

        lines.add("");
        int playersAlive = ((ActiveGameState) arenaState).getAlivePlayers().size();
        lines.add("Players: &6" + playersAlive + "/" + getMAX_PLAYERS());
        lines.add("");
        lines.add("&aYou can do this paul!!");
        lines.add("");

        return lines;
    }

    public String arenaNameFromArgs(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(i == 0) continue;

            stringBuilder.append(arg);
            if(i != args.length - 1){
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }
}
