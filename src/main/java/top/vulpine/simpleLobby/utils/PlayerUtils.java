package top.vulpine.simpleLobby.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.utils.logger.Logger;

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

        FileConfiguration config = plugin.getConfig();

        World world = Bukkit.getWorld(config.getString("spawn.location.world"));
        if (world == null) {
            Logger.warn("World does not exist");
            return;
        }

        double x = config.getDouble("spawn.location.x");
        double y = config.getDouble("spawn.location.y");
        double z = config.getDouble("spawn.location.z");
        float yaw = (float) config.getDouble("spawn.location.yaw");
        float pitch = (float) config.getDouble("spawn.location.pitch");

        player.teleport(new Location(world, x, y, z, yaw, pitch));

        Logger.debug("Teleported player " + player.getName() + " to spawn at " + world + " (" + x + ", " + y + ", " + z + ")");

    }

}
