package top.vulpine.simpleLobby.util;

import org.bukkit.entity.Player;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.util.logger.Logger;

/**
 * Utility class for player-related operations in the SimpleLobby plugin.
 */
public class PlayerUtils {

    /**
     * Teleports a player to the spawn location defined in the plugin's configuration.
     *
     * @param plugin The SimpleLobby plugin instance.
     * @param player The player to teleport.
     */
    public static void teleportPlayer(SimpleLobby plugin, Player player) {

        plugin.getScheduler().teleport(player, plugin.getConfiguration().spawn.location);

        Logger.debug("Teleported player " + player.getName() + " to spawn at " + plugin.getConfiguration().spawn.location.getWorld() + " (" +
                plugin.getConfiguration().spawn.location.getX() + ", " +
                plugin.getConfiguration().spawn.location.getY() + ", " +
                plugin.getConfiguration().spawn.location.getZ() + ")");

    }

}
