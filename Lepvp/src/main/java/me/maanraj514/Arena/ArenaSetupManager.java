package me.maanraj514.Arena;

import lombok.RequiredArgsConstructor;
import me.maanraj514.Lepvp;
import me.maanraj514.utility.Colorize;
import me.maanraj514.utility.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

@RequiredArgsConstructor
public class ArenaSetupManager implements Listener {

    private final ArenaManager arenaManager;
    private final Lepvp plugin;
    private final Map<UUID, TemporaryArena> playerToTempArenaMap = new HashMap<>();

    private final String SET_LOCATION_ONE_ITEM_NAME = "Set Location One";
    private final String RIGHT_CLICK = " (Right Click)";
    private final String SET_LOCATION_TWO_ITEM_NAME = "Set Location Two";
    private final String SAVE_ITEM_NAME = "Save Arena";
    private final String CANCEL_ITEM_NAME = "Cancel Arena Setup";

    public void addToSetup(Player player, TemporaryArena temporaryArena) {
        if (playerToTempArenaMap.containsKey(player.getUniqueId())) return;

        arenaManager.getRollBackManager().save(player);
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);

        playerToTempArenaMap.put(player.getUniqueId(), temporaryArena);
        Colorize.sendMessage(player, "&aMoved to setup mode for " + temporaryArena.getDisplayName());

        player.getInventory().addItem(
                new ItemBuilder(Material.STICK)
                        .setName(ChatColor.GREEN + SET_LOCATION_ONE_ITEM_NAME + ChatColor.GRAY + RIGHT_CLICK)
                        .build()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.BLAZE_ROD)
                        .setName(ChatColor.GREEN + SET_LOCATION_TWO_ITEM_NAME + ChatColor.GRAY + RIGHT_CLICK)
                        .build()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.DIAMOND)
                        .setName(ChatColor.GREEN + SAVE_ITEM_NAME + ChatColor.GRAY + RIGHT_CLICK)
                        .build()
        );
        player.getInventory().addItem(
                new ItemBuilder(Material.BARRIER)
                        .setName(ChatColor.GREEN + CANCEL_ITEM_NAME + ChatColor.GRAY + RIGHT_CLICK)
                        .build()
        );
    }

    public void removeFromSetup(Player player) {
        if (!playerToTempArenaMap.containsKey(player.getUniqueId())) return;

        player.getInventory().clear();
        playerToTempArenaMap.remove(player.getUniqueId());
        arenaManager.getRollBackManager().restore(player, plugin);
    }

    public boolean inSetupMode(Player player) {
        return playerToTempArenaMap.containsKey(player.getUniqueId());
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!inSetupMode(event.getPlayer())) return;
        if (!event.hasItem()) return;
        if (event.getItem() == null) return;
        if (event.getItem().getItemMeta() == null) return;
        if (!event.getItem().hasItemMeta()) return;

        Player player = event.getPlayer();

        TemporaryArena temporaryArena = playerToTempArenaMap.get(event.getPlayer().getUniqueId());
        String itemName = event.getItem().getItemMeta().getDisplayName();
        itemName = Colorize.stripColor(itemName);

        switch (itemName) {
            case SAVE_ITEM_NAME + RIGHT_CLICK:
                arenaManager.saveArenaToConfig(temporaryArena.toArena());
                player.sendTitle(Colorize.format("&aThe Arena has been saved!"), ".", 1, 20, 1);
                removeFromSetup(player);
                break;
            case SET_LOCATION_ONE_ITEM_NAME + RIGHT_CLICK:
                temporaryArena.setSpawnLocationOne(player.getLocation());
                player.sendTitle(Colorize.format("&aSpawnLocationOne has been set"), ".", 1, 20, 1);
                break;
            case SET_LOCATION_TWO_ITEM_NAME + RIGHT_CLICK:
                temporaryArena.setSpawnLocationTwo(player.getLocation());
                player.sendTitle(Colorize.format("&aSpawnLocationTwo has been set"), ".", 1, 20, 1);
                break;
            case CANCEL_ITEM_NAME + RIGHT_CLICK:
                player.sendTitle(Colorize.format("&cThe Arena Setup"), Colorize.format("&cHas Been Cancelled"), 1, 20, 1);
                removeFromSetup(player);
                break;
            default:
                player.sendMessage("something happened ://///");
        }
    }
}
