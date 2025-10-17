package top.vulpine.simpleLobby.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for rendering colored text in Minecraft.
 */
public class Colorize {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer AMP = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer SEC = LegacyComponentSerializer.legacySection();

    private static final Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern MINI_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern AMP_CODE = Pattern.compile("&([0-9A-FK-ORa-fk-or])");

    /**
     * Colorizes a single string.
     *
     * @param message The message to colorize.
     * @return The colorized message.
     */
    public static String color(final String message) {
        return SEC.serialize(toComponent(message));
    }

    /**
     * Colorizes an array of strings.
     *
     * @param message The array of messages to colorize.
     * @return An array of colorized messages.
     */
    public static String[] color(final String[] message) {
        String[] out = new String[message.length];
        for (int i = 0; i < message.length; i++) {
            out[i] = color(message[i]);
        }
        return out;
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

    public static Component toComponent(final String message) {
        if (looksLikeMini(message)) {
            String normalized = convertLegacyToMini(message);
            return MINI.deserialize(normalized);
        }
        if (message.indexOf('§') >= 0) {
            return SEC.deserialize(message);
        }
        String legacy = translateLegacyHexToSection(message);
        legacy = ChatColor.translateAlternateColorCodes('&', legacy);
        return SEC.deserialize(legacy);
    }

    private static boolean looksLikeMini(final String s) {
        return MINI_TAG.matcher(s).find();
    }

    private static String convertLegacyToMini(final String s) {
        String out = HEX.matcher(s).replaceAll("<#$1>");
        Matcher m = AMP_CODE.matcher(out);
        StringBuilder sb = new StringBuilder(out.length());
        while (m.find()) {
            char c = Character.toLowerCase(m.group(1).charAt(0));
            String rep = switch (c) {
                case '0' -> "<black>";
                case '1' -> "<dark_blue>";
                case '2' -> "<dark_green>";
                case '3' -> "<dark_aqua>";
                case '4' -> "<dark_red>";
                case '5' -> "<dark_purple>";
                case '6' -> "<gold>";
                case '7' -> "<gray>";
                case '8' -> "<dark_gray>";
                case '9' -> "<blue>";
                case 'a' -> "<green>";
                case 'b' -> "<aqua>";
                case 'c' -> "<red>";
                case 'd' -> "<light_purple>";
                case 'e' -> "<yellow>";
                case 'f' -> "<white>";
                case 'l' -> "<bold>";
                case 'n' -> "<underlined>";
                case 'o' -> "<italic>";
                case 'm' -> "<strikethrough>";
                case 'k' -> "<obfuscated>";
                case 'r' -> "<reset>";
                default -> "";
            };
            m.appendReplacement(sb, Matcher.quoteReplacement(rep));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String translateLegacyHexToSection(final String message) {
        Matcher matcher = HEX.matcher(message);
        StringBuilder sb = new StringBuilder(message.length());
        while (matcher.find()) {
            String group = matcher.group(1);
            char cc = ChatColor.COLOR_CHAR;
            String replacement = String.valueOf(cc) + 'x' +
                    cc + group.charAt(0) +
                    cc + group.charAt(1) +
                    cc + group.charAt(2) +
                    cc + group.charAt(3) +
                    cc + group.charAt(4) +
                    cc + group.charAt(5);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}