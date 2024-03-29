package me.maanraj514.Arena.State;

import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import lombok.Getter;
import me.maanraj514.Arena.ArenaStatus;
import me.maanraj514.Arena.ItemStacks.*;
import me.maanraj514.Lepvp;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActiveGameState extends ArenaState {

    @Getter
    private List<UUID> alivePlayers;
    private boolean isOver = false;
    private Player loser;

    private JPerPlayerScoreboard scoreboard;
    private BukkitTask task;

    @Override
    public void onEnable(Lepvp plugin) {
        super.onEnable(plugin);

        plugin.getArenaManager().setArenaStatus(ArenaStatus.PLAYING);

        alivePlayers = new ArrayList<>(getArena().getPlayers());

        int lastSpawnId = 0;

        scoreboard = new JPerPlayerScoreboard(
                (player) -> "&5&lLEPVP",
                (player) -> getActiveScoreboardLines()
        );

        for (UUID playerUUID : alivePlayers) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null || !player.isOnline()) continue;

            if (lastSpawnId == 0) {
                player.teleportAsync(getArena().getSpawnLocationOne());
                lastSpawnId = 1;
            } else {
                player.teleportAsync(getArena().getSpawnLocationTwo());
                lastSpawnId = 0;
            }
            addItems(player);
            addToScoreboard(player, scoreboard);
        }
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            scoreboard.updateScoreboard();
        }, 0, 10);

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (alivePlayers.size() > 1 || isOver) return;

            this.isOver = true;

            if (alivePlayers.size() == 1) {
                UUID winnerUUID = alivePlayers.get(0);
                Player winner = Bukkit.getPlayer(winnerUUID);
                scoreboard.destroy();
                task.cancel();
                if (winner == null || !winner.isOnline()) {
                    getArena().sendMessage("&cGame Over, but winner could not be found");
                } else {
                    scoreboard = new JPerPlayerScoreboard(
                            (player) -> "&5&lLEPVP",
                            (player) -> getWinnerScoreboardLines(winner, loser)
                    );
                    addToScoreboard(winner, scoreboard);
                    addToScoreboard(loser, scoreboard);
                    task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        scoreboard.updateScoreboard();
                    }, 0, 10);

                    getArena().sendMessage("&a" + winner.getDisplayName() + " has won!");
                }
            } else {
                getArena().sendMessage("&cNo alive players? Game Over anyway");
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (UUID uuid : alivePlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;

                    removeFromScoreboard(player, scoreboard);
                    scoreboard.destroy();
                }
                task.cancel();
            }, 20*9);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> getArena().setState(new ResetArenaState(), plugin), 20 * 10);
        }, 0, 4);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        if (!getArena().isPlayer(player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!getArena().isPlayer(player)) return;

        if (isOver) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);

        if (player.getHealth() - event.getFinalDamage() <= 0 && !(player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING || player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING)) {
            alivePlayers.remove(player.getUniqueId());
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setGameMode(GameMode.SPECTATOR);
            loser = player;
            getArena().sendMessage("&a" + player.getDisplayName() + " died!");
            player.spigot().respawn();
        }
        player.spigot().respawn();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        if (!getArena().isPlayer(event.getPlayer())) return;

        alivePlayers.remove(event.getPlayer().getUniqueId());
        loser = event.getPlayer();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!getArena().isPlayer(player)) return;

        alivePlayers.remove(player.getUniqueId());

        player.getInventory().clear();
        player.getActivePotionEffects().clear();

        player.setLevel(0);
        player.setExp(0);
    }

    private List<String> getWinnerScoreboardLines(Player winner, Player loser) {
        List<String> lines = new ArrayList<>();

        lines.add("");
        lines.add("&aThe winner is &a" + winner.getDisplayName());
        lines.add("");
        lines.add("&cThe loser is &c" + loser.getDisplayName());
        lines.add("");

        return lines;
    }

    private List<String> getActiveScoreboardLines() {
        List<String> lines = new ArrayList<>();

        lines.add("");
        int playersLeft = getAlivePlayers().size();
        lines.add("Players Left: &6" + playersLeft + "/" + getArena().getMAX_PLAYERS());
        lines.add("");
        lines.add("among &cඞ");
        lines.add("");

        return lines;
    }

    private void removeFromScoreboard(Player player, JPerPlayerScoreboard scoreboard) {
        if (scoreboard != null) {
            scoreboard.removePlayer(player);
            scoreboard.updateScoreboard();
        }
    }

    private void addToScoreboard(Player player, JPerPlayerScoreboard scoreboard) {
        if (scoreboard != null) {
            scoreboard.addPlayer(player);
            scoreboard.updateScoreboard();
        }
    }

    private void addItems(Player player) {
        // Armour
        player.getInventory().setHelmet(NetheriteHelmet.NetheriteHelmet);
        player.getInventory().setChestplate(NetheriteChestplate.NetheriteChestplate);
        player.getInventory().setLeggings(NetheriteLeggings.NetheriteLeggings);
        player.getInventory().setBoots(NetheriteBoots.NetheriteBoots);

        // Main inventory
        player.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(0, NetheriteSword.NetheriteSword);
        player.getInventory().setItem(1, new ItemStack(Material.END_CRYSTAL, 64));
        player.getInventory().setItem(2, new ItemStack(Material.OBSIDIAN, 64));
        player.getInventory().setItem(3, new ItemStack(Material.RESPAWN_ANCHOR, 64));
        player.getInventory().setItem(4, new ItemStack(Material.GLOWSTONE, 64));
        player.getInventory().setItem(5, new ItemStack(Material.ENDER_CHEST, 64));
        player.getInventory().setItem(6, new ItemStack(Material.ENDER_PEARL, 17));
        player.getInventory().setItem(7, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 64));
        player.getInventory().setItem(8, new ItemStack(Material.OBSIDIAN, 64));

        // Rest of Inventory
        // first row
        player.getInventory().setItem(9, new ItemStack(Material.END_CRYSTAL, 64));
        player.getInventory().setItem(10, new ItemStack(Material.END_CRYSTAL, 64));
        player.getInventory().setItem(11, new ItemStack(Material.OBSIDIAN, 64));
        player.getInventory().setItem(12, new ItemStack(Material.GLOWSTONE, 64));
        player.getInventory().setItem(13, NetheritePickaxe.NetheritePickaxe);
        player.getInventory().setItem(14, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(15, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(16, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(17, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));

        // second row
        player.getInventory().setItem(18, new ItemStack(Material.END_CRYSTAL, 64));
        player.getInventory().setItem(19, new ItemStack(Material.END_CRYSTAL, 64));
        player.getInventory().setItem(20, new ItemStack(Material.OBSIDIAN, 64));
        player.getInventory().setItem(21, new ItemStack(Material.RESPAWN_ANCHOR, 64));
        player.getInventory().setItem(22, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(23, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(24, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(25, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(26, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));

        // third row
        player.getInventory().setItem(27, new ItemStack(Material.END_CRYSTAL, 64));
        player.getInventory().setItem(28, new ItemStack(Material.END_CRYSTAL, 64));
        player.getInventory().setItem(29, new ItemStack(Material.OBSIDIAN, 64));
        player.getInventory().setItem(30, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(31, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(32, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(33, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(34, new ItemStack(Material.TOTEM_OF_UNDYING));
        player.getInventory().setItem(35, new ItemStack(Material.ENDER_PEARL, 16));
    }
}