package top.vulpine.simpleLobby.utils.logger;

import java.util.logging.Level;

/**
 * An enumeration representing different levels of logging severity.
 */
public enum LogLevel {

    /**
     * DEBUG level is used for detailed debugging information.
     */
    DEBUG,

    /**
     * INFO level is used for general informational messages.
     */
    INFO,

    /**
     * WARN level is used for potentially harmful situations.
     */
    WARN,

    /**
     * ERROR level is used for error events that might still allow the application to continue running.
     */
    ERROR;

    /**
     * Converts a LogLevel to a Java Level.
     *
     * @param level the LogLevel to convert
     * @return the corresponding Java Level
     */
    private static Level toJavaLevel(LogLevel level) {

        return switch (level) {
            case ERROR -> Level.SEVERE;
            case WARN -> Level.WARNING;
            case DEBUG -> Level.FINE;
            default -> Level.INFO;
        };

    }

}
