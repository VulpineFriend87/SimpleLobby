package top.vulpine.simpleLobby.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for rendering colored text in Minecraft.
 */
public class Colorize {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /**
     * Colorizes a single string.
     *
     * @param message The message to colorize.
     * @return The colorized message.
     */
    public static String color(final String message) {
        return translateHexColorCodes(ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Colorizes an array of strings.
     *
     * @param message The array of messages to colorize.
     * @return An array of colorized messages.
     */
    public static String[] color(final String[] message) {
        String[] colored = new String[message.length];
        for (int i = 0; i < message.length; i++) {
            colored[i] = color(colored[i]);
        }
        return colored;
    }

    /**
     * Colorizes a list of strings.
     *
     * @param message The list of messages to colorize.
     * @return A list of colorized messages.
     */
    public static List<String> color(final List<String> message) {
        message.replaceAll(Colorize::color);
        return message;
    }

    /**
     * Translates hex color codes in a message to Minecraft's color codes.
     * Hex color codes are specified as &# followed by a 6-digit hex code.
     *
     * @param message The message containing hex color codes.
     * @return The message with hex color codes translated to Minecraft's format.
     */
    public static String translateHexColorCodes(final String message) {
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

}