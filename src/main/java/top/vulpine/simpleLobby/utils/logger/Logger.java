package top.vulpine.simpleLobby.utils.logger;

import org.bukkit.Bukkit;
import top.vulpine.simpleLobby.utils.Colorize;

/**
 * A utility class for logging messages to the console with different log levels.
 */
public class Logger {

    private static LogLevel logLevel = LogLevel.INFO;
    private static final String prefix = "&8[&7&lSimple&a&lLobby&8] &r";

    /**
     * Initializes the Logger with a specified log level.
     *
     * @param loggingLogLevel The log level to set for the Logger.
     */
    public static void init(LogLevel loggingLogLevel) {

        logLevel = loggingLogLevel;

    }

    /**
     * Gets the class calling a logging method.
     *
     * @return The name of the class or "Unknown" if it cannot be determined.
     */
    private static String getCallerClass() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (int i = 2; i < stackTrace.length; i++) {

            String className = stackTrace[i].getClassName();

            if (!className.equals(Logger.class.getName())) {

                return className.substring(className.lastIndexOf('.') + 1);

            }

        }

        return "Unknown";
    }

    /**
     * Builds a path string for logging, including the prefix and the calling class.
     *
     * @return A formatted string containing the prefix and the calling class name.
     */
    private static String buildPathString() {

        return prefix + "&7[" + getCallerClass() + "&7] &r";

    }

    /**
     * Logs an informational message to the console.
     *
     * @param message The message to log.
     */
    public static void info(String message) {

        if (logLevel.ordinal() > LogLevel.INFO.ordinal()) return;

        String pathString = buildPathString();
        Bukkit.getConsoleSender().sendMessage(Colorize.color(pathString + "&7" + message));

    }

    /**
     * Logs an error message to the console.
     *
     * @param message The error message to log.
     */
    public static void error(String message) {

        if (logLevel.ordinal() > LogLevel.ERROR.ordinal()) return;

        String pathString = buildPathString();
        Bukkit.getConsoleSender().sendMessage(Colorize.color(pathString + "&c" + message));

    }

    /**
     * Logs a warning message to the console.
     *
     * @param message The warning message to log.
     */
    public static void warn(String message) {

        if (logLevel.ordinal() > LogLevel.WARN.ordinal()) return;

        String pathString = buildPathString();
        Bukkit.getConsoleSender().sendMessage(Colorize.color(pathString + "&e" + message));

    }

    /**
     * Logs a debug message to the console.
     *
     * @param message The debug message to log.
     */
    public static void debug(String message) {

        if (logLevel.ordinal() > LogLevel.DEBUG.ordinal()) return;

        String pathString = "&7[&9DEBUG&7]" + buildPathString();
        Bukkit.getConsoleSender().sendMessage(Colorize.color(pathString + "&9" + message));

    }

    /**
     * Logs a system message to the console.
     *
     * @param message The system message to log.
     */
    public static void system(String message) {

        String pathString = buildPathString();
        Bukkit.getConsoleSender().sendMessage(Colorize.color(pathString + "&f" + message));

    }

    public static String getPrefix() {
        return prefix;
    }

}
