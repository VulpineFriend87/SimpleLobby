package top.vulpine.simpleLobby.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import top.vulpine.commons.text.Colorize;
import top.vulpine.simpleLobby.util.logger.Logger;
import top.vulpine.simpleLobby.SimpleLobby;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Parses and executes a list of string-based actions for a player.
 * Handles placeholder replacement and supports targeting individual players, arenas, or all online players.
 */
public class ActionParser {

    private final SimpleLobby plugin;

    /**
     * Constructs a new ActionParser.
     *
     * @param plugin the MiniEngine plugin instance
     */
    public ActionParser(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes a list of actions for a player, starting from the given index, with placeholder support.
     *
     * @param actions the list of action strings to execute
     * @param player the player to target
     * @param currentIndex the index to start execution from
     * @param placeholders a map of placeholders to replace in messages
     */
    public void executeActions(List<String> actions, Player player, int currentIndex, Map<String, String> placeholders) {

        for (int i = currentIndex; i < actions.size(); i++) {

            String actionString = actions.get(i);

            try {

                if (!actionString.startsWith("[")) {
                    Logger.warn("Invalid action format (missing opening bracket): " + actionString);
                    continue;
                }

                int closingBracketIndex = actionString.indexOf("]");
                if (closingBracketIndex == -1) {
                    Logger.warn("Invalid action format (missing closing bracket): " + actionString);
                    continue;
                }

                String action = actionString.substring(1, closingBracketIndex).trim();
                String params = actionString.substring(closingBracketIndex + 1).trim();

                switch (action) {

                    case "COMMAND":
                        executeCommand(params, player);
                        break;

                    case "GAMEMODE":
                        executeGamemode(params, player);
                        break;

                    case "TITLE":
                        executeTitle(params, player, placeholders);
                        break;

                    case "ACTIONBAR":
                        executeActionBar(params, player, placeholders);
                        break;

                    case "MESSAGE":
                        executeMessage(params, player, placeholders);
                        break;

                    case "SOUND":
                        executeSound(params, player);
                        break;

                    case "DELAY":
                        executeDelay(params, actions, player, i + 1, placeholders);
                        return;

                    default:
                        Logger.warn("Unknown action: " + action);
                }

            } catch (Exception e) {

                Logger.warn("Failed to execute action: " + actionString);
                e.printStackTrace();

            }

        }

    }

    /**
     * Executes a command as either the console or the player.
     *
     * @param params the command parameters (target;command)
     * @param player the player to use for placeholder replacement and as command sender if target is player
     */
    private void executeCommand(String params, Player player) {

        if (player == null) {
            Logger.warn("Player is null, cannot execute command.");
            return;
        }

        String[] parts = params.split(";", 2);

        String target = parts[0].trim();
        String command = parts[1].trim().replace("%player%", player.getName());

        if (target.equalsIgnoreCase("console")) {
            plugin.getScheduler().runGlobal(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        } else if (target.equalsIgnoreCase("player")) {
            plugin.getScheduler().runEntity(player, () -> player.performCommand(command));
        }

    }

    /**
     * Changes the player's gamemode.
     *
     * @param params the gamemode parameters (target;gamemode)
     * @param player the player to target
     */
    private void executeGamemode(String params, Player player) {

        String[] parts = params.split(";", 2);

        String target = parts[0].trim();
        String gamemodeString = parts[1].trim().toUpperCase();

        GameMode gamemode;
        try {
            gamemode = GameMode.valueOf(gamemodeString);
        } catch (IllegalArgumentException e) {
            Logger.warn("Invalid gamemode: " + gamemodeString);
            return;
        }

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getScheduler().runEntity(p, () -> p.setGameMode(gamemode));
            }
        } else if (target.equalsIgnoreCase("player")) {
            plugin.getScheduler().runEntity(player, () -> player.setGameMode(gamemode));
        }

    }

    /**
     * Sends a title to the specified target (player, arena, or global).
     *
     * @param params the title parameters (target;title;subtitle;fadeIn;stay;fadeOut)
     * @param player the player to target or use for placeholder replacement
     * @param placeholders the placeholders to replace in the title and subtitle
     */
    private void executeTitle(String params, Player player, Map<String, String> placeholders) {

        String[] parts = params.split(";");

        String target = parts[0].trim();

        String title = parts.length > 1 ? parts[1].trim() : "";
        String subtitle = parts.length > 2 ? parts[2].trim() : "";
        int fadeIn = parts.length > 3 ? Integer.parseInt(parts[3].trim()) : 10;
        int stay = parts.length > 4 ? Integer.parseInt(parts[4].trim()) : 40;
        int fadeOut = parts.length > 5 ? Integer.parseInt(parts[5].trim()) : 10;

        Title finalTitle = Title.title(
                Colorize.color(replacePlaceholders(player, title, placeholders)),
                Colorize.color(replacePlaceholders(player, subtitle, placeholders)),
                Title.Times.times(ticks(fadeIn), ticks(stay), ticks(fadeOut))
        );

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getScheduler().runEntity(p, () -> p.showTitle(finalTitle));
            }
        } else if (target.equalsIgnoreCase("player")) {
            plugin.getScheduler().runEntity(player, () -> player.showTitle(finalTitle));
        }
    }

    /**
     * Sends an action bar to the specified target (player, arena, or global).
     *
     * @param params the action bar parameters (target;message)
     * @param player the player to target or use for placeholder replacement
     * @param placeholders the placeholders to replace in the message
     */
    private void executeActionBar(String params, Player player, Map<String, String> placeholders) {

        String[] parts = params.split(";", 2);

        String target = parts[0].trim();
        Component message = Colorize.color(replacePlaceholders(player, parts[1].trim(), placeholders));

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getScheduler().runEntity(p, () -> p.sendActionBar(message));
            }
        } else if (target.equalsIgnoreCase("player")) {
            plugin.getScheduler().runEntity(player, () -> player.sendActionBar(message));
        }

    }

    /**
     * Sends a chat message to the specified target (player, arena, or global).
     *
     * @param params the message parameters (target;message)
     * @param player the player to target or use for placeholder replacement
     * @param placeholders the placeholders to replace in the message
     */
    private void executeMessage(String params, Player player, Map<String, String> placeholders) {

        String[] parts = params.split(";", 2);

        String target = parts[0].trim();
        Component finalMessage = Colorize.color(replacePlaceholders(player, parts[1].trim(), placeholders));

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getScheduler().runEntity(p, () -> p.sendMessage(finalMessage));
            }
        } else if (target.equalsIgnoreCase("player")) {
            plugin.getScheduler().runEntity(player, () -> player.sendMessage(finalMessage));
        }

    }

    /**
     * Plays a sound for the specified target (player, arena, or global).
     *
     * @param params the sound parameters (target;sound;volume;pitch)
     * @param player the player to target
     */
    private void executeSound(String params, Player player) {

        String[] parts = params.split(";", 4);

        if (parts.length < 2) {
            Logger.warn("Invalid SOUND action, expected '<global/player>; <sound>': " + params);
            return;
        }

        String target = parts[0].trim();
        String soundString = parts[1].trim();
        float volume = parts.length > 2 ? Float.parseFloat(parts[2].trim()) : 1.0f;
        float pitch = parts.length > 3 ? Float.parseFloat(parts[3].trim()) : 1.0f;

        // Sent as a key rather than an org.bukkit.Sound: that enum became an
        // interface in 1.21.3, and Sound.valueOf() no longer links from a jar
        // compiled against the older API. See SoundKeys.
        String sound = SoundKeys.resolve(soundString);

        if (sound == null) {
            Logger.warn("Unknown sound: " + soundString);
            return;
        }

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getScheduler().runEntity(p, () -> p.playSound(p.getLocation(), sound, volume, pitch));
            }
        } else if (target.equalsIgnoreCase("player")) {
            plugin.getScheduler().runEntity(player, () -> player.playSound(player.getLocation(), sound, volume, pitch));
        }

    }

    /**
     * Schedules a delay before continuing execution of the remaining actions.
     *
     * @param params the delay duration in milliseconds
     * @param actions the list of actions to execute
     * @param player the player to target
     * @param nextIndex the index of the next action to execute
     * @param placeholders the placeholders to use for later actions
     */
    private void executeDelay(String params, List<String> actions, Player player, int nextIndex, Map<String, String> placeholders) {

        int delay = Integer.parseInt(params.trim());
        long ticks = delay / 50L;

        plugin.getScheduler().runEntityLater(player,
                () -> executeActions(actions, player, nextIndex, placeholders),
                ticks);

    }

    /**
     * Replaces placeholders in a string with their values for the given player.
     * Runs before colorizing, so colors coming from a placeholder are rendered too.
     *
     * @param player the player for placeholder context
     * @param str the string to process
     * @param placeholders the map of placeholders to replace
     * @return the string with placeholders replaced
     */
    private String replacePlaceholders(Player player, String str, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            str = str.replace(entry.getKey(), entry.getValue());
        }

        return PlaceholderUtils.replace(player, str);
    }

    /**
     * Converts a tick count from the config into the duration a title expects.
     *
     * @param ticks the number of ticks
     * @return the equivalent duration
     */
    private static Duration ticks(int ticks) {
        return Duration.ofMillis(ticks * 50L);
    }

}
