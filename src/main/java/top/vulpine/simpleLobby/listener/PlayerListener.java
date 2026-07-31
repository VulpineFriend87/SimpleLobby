package top.vulpine.simpleLobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.util.PlayerUtils;
import top.vulpine.commons.log.LogAction;
import top.vulpine.commons.log.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles player joins and quits events in the SimpleLobby plugin.
 * It clears inventory and potion effects on join, executes actions on join and quit,
 * and teleports players to the spawn location based on the configuration.
 */
public class PlayerListener implements Listener {

    private final SimpleLobby plugin;

    private enum Action implements LogAction {
        JOIN, QUIT
    }

    public PlayerListener(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (plugin.getConfiguration().actions.join.suppressDefaultMessage) {
            event.joinMessage(null);
            Logger.debug(Action.JOIN, "Join message suppressed for player: " + event.getPlayer().getName());
        }

        if (plugin.getConfiguration().options.clearInventoryOnJoin.enabled) {
            event.getPlayer().getInventory().clear();
            Logger.debug(Action.JOIN, "Inventory cleared for player: " + event.getPlayer().getName());
        }

        if (plugin.getConfiguration().options.clearEffectsOnJoin.enabled) {
            event.getPlayer().getActivePotionEffects().forEach(effect ->
                    event.getPlayer().removePotionEffect(effect.getType())
            );
            Logger.debug(Action.JOIN, "Potion effects cleared for player: " + event.getPlayer().getName());
        }

        if (plugin.getConfiguration().actions.join.enabled) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", event.getPlayer().getName());

            plugin.getActionParser().executeActions(plugin.getConfiguration().actions.join.actions,
                    event.getPlayer(),
                    0,
                    placeholders
            );

        }

        if (plugin.getConfiguration().spawn.tpOnJoin) {

            org.bukkit.Location spawn = plugin.getConfiguration().spawn.location;
            if (spawn == null || spawn.getWorld() == null) {
                Logger.warn(Action.JOIN, "tp_on_join is enabled but the spawn location is not set. Skipping teleport for "
                        + event.getPlayer().getName() + ".");
            } else {
                PlayerUtils.teleportPlayer(plugin, event.getPlayer());
            }

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (plugin.getConfiguration().actions.quit.suppressDefaultMessage) {
            event.quitMessage(null);
            Logger.debug(Action.QUIT, "Quit message suppressed for player: " + event.getPlayer().getName());
        }

        if (plugin.getConfiguration().actions.quit.enabled) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", event.getPlayer().getName());

            plugin.getActionParser().executeActions(plugin.getConfiguration().actions.quit.actions,
                    event.getPlayer(),
                    0,
                    placeholders
            );

        }

    }

}
