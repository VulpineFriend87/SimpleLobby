package top.vulpine.simpleLobby;

import org.bukkit.plugin.java.JavaPlugin;
import top.vulpine.simpleLobby.command.SimpleLobbyCommand;
import top.vulpine.simpleLobby.command.SpawnCommand;
import top.vulpine.simpleLobby.listener.PlayerListener;
import top.vulpine.simpleLobby.listener.WorldListener;
import top.vulpine.simpleLobby.utils.ActionParser;
import top.vulpine.simpleLobby.utils.logger.LogLevel;
import top.vulpine.simpleLobby.utils.logger.Logger;

/**
 * Main class for the SimpleLobby plugin.
 * This class initializes the plugin, sets up logging, and registers commands and event listeners.
 * It also provides access to the ActionParser for executing actions defined in the configuration.
 */
public final class SimpleLobby extends JavaPlugin {

    private ActionParser actionParser;

    private static final int PLUGIN_ID = 28227;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(getConfig().getString("log_level"));
        } catch (IllegalArgumentException e) {
            Logger.warn("Invalid log level in config, defaulting to INFO");
            logLevel = LogLevel.INFO;
        }
        Logger.init(logLevel);

        String[] message = {
                "",
                "&f&l     _____ &a&l__",
                "&f&l    |   __&a&l|  |",
                "&f&l    |__   &a&l|  |__",
                "&f&l    |_____&a&l|_____|",
                "",
                "&f    By &a" + String.join(", ", getDescription().getAuthors()),
                "&f    Version: &a" + getDescription().getVersion(),
                ""
        };

        for (String line : message) {
            Logger.system(line);
        }

        Logger.debug("Loading Action Parser...");
        actionParser = new ActionParser(this);
        Logger.debug("Action Parser loaded successfully.");

        Logger.debug("Registering commands and listeners...");
        getCommand("simplelobby").setExecutor(new SimpleLobbyCommand(this));

        SpawnCommand spawnCommand = new SpawnCommand(this);
        getCommand("spawn").setExecutor(spawnCommand);
        getServer().getPluginManager().registerEvents(spawnCommand, this);

        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Logger.debug("Commands and listeners registered successfully.");

        Logger.debug("Initializing metrics...");
        Metrics metrics = new Metrics(this, PLUGIN_ID);

        Logger.system("SimpleLobby has been enabled successfully.");
    }

    public ActionParser getActionParser() {
        return actionParser;
    }
}
