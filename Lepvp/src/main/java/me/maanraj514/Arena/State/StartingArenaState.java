package me.maanraj514.Arena.State;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.maanraj514.Lepvp;
import me.maanraj514.tasks.StartCountdownTask;

@RequiredArgsConstructor
public class StartingArenaState extends ArenaState {
    @Getter
    private StartCountdownTask startCountdownTask;

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        this.startCountdownTask = new StartCountdownTask(plugin, getArena(), 5);
        this.startCountdownTask.runTaskTimer(plugin, 0, 20);
    }

    public void onDisable(Lepvp plugin) {
        super.onDisable();

        startCountdownTask.cancel();
    }
}
