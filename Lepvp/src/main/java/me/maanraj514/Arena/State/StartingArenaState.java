package me.maanraj514.Arena.State;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.maanraj514.Lepvp;
import me.maanraj514.Arena.tasks.StartCountdownTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class StartingArenaState extends ArenaState {
    @Getter
    private StartCountdownTask startCountdownTask;
    private int secondsUntilStart = 5;

    private JPerPlayerScoreboard scoreboard;
    private BukkitTask task;

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        this.startCountdownTask = new StartCountdownTask(plugin, getArena(), secondsUntilStart);
        this.startCountdownTask.runTaskTimer(plugin, 0, 20);

        scoreboard = new JPerPlayerScoreboard(
                (player) -> "&5&lLEPVP",
                (player) -> getStartingScoreboardLines()
        );

        for (UUID playerUUID : getArena().getPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            addToScoreboard(player);
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            secondsUntilStart--;
            if (secondsUntilStart <= 0) {
                for (UUID playerUUID : getArena().getPlayers()) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player == null) continue;

                    removeFromScoreboard(player);
                    scoreboard.destroy();
                }
                task.cancel();
            }else{
                scoreboard.updateScoreboard();
            }
        }, 0, 20);
    }

    public void onDisable(Lepvp plugin) {
        super.onDisable();

        startCountdownTask.cancel();
    }

    private void removeFromScoreboard(Player player) {
        if (scoreboard != null) {
            scoreboard.removePlayer(player);
            scoreboard.updateScoreboard();
        }
    }

    private void addToScoreboard(Player player) {
        if (scoreboard != null) {
            scoreboard.addPlayer(player);
            scoreboard.updateScoreboard();
        }
    }

    private List<String> getStartingScoreboardLines() {
        List<String> lines = new ArrayList<>();

        lines.add("");
        int secondsLeft = getStartCountdownTask().getSecondsUntilStart();
        lines.add("Players: &6" + getArena().getPlayers().size() + "/" + getArena().getMAX_PLAYERS());
        lines.add("");
        lines.add("Starting in &6" + secondsLeft);
        lines.add("");

        return lines;
    }
}