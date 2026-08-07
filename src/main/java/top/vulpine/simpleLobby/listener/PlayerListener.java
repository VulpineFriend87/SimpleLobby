package top.vulpine.simpleLobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.util.PlayerUtils;
import top.vulpine.commons.log.LogAction;
import top.vulpine.commons.log.Logger;

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
            plugin.getActions().run(event.getPlayer(), plugin.getConfiguration().actions.join.actions);
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

        // Anything still waiting on a delay belongs to a player who is no longer here.
        plugin.getActions().cancel(event.getPlayer());

        if (plugin.getConfiguration().actions.quit.enabled) {
            plugin.getActions().run(event.getPlayer(), plugin.getConfiguration().actions.quit.actions);
        }

    }

}
