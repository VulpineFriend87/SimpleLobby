package top.vulpine.simpleLobby.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Utility class for handling PlaceholderAPI placeholders.
 */
public class PlaceholderUtils {

    /**
     * Replaces placeholders in the given input string for the specified player.
     *
     * @param player the player for whom to replace placeholders
     * @param input the input string containing placeholders
     * @return the input string with placeholders replaced, or null if input is null
     */
    public static String replace(Player player, String input) {

        if (input == null) {
            return null;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }

        return input;

    }

    /**
     * Replaces placeholders in the given input string for the specified offline player.
     *
     * @param player the offline player for whom to replace placeholders
     * @param input the input string containing placeholders
     * @return the input string with placeholders replaced, or null if input is null
     */
    public static String replace(OfflinePlayer player, String input) {

        if (input == null) {
            return null;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }

        return input;

    }

}
