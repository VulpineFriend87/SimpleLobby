package top.vulpine.simpleLobby;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import top.vulpine.simpleLobby.command.SimpleLobbyCommand;
import top.vulpine.simpleLobby.command.SpawnCommand;
import top.vulpine.simpleLobby.config.impl.Config;
import top.vulpine.simpleLobby.listener.PlayerListener;
import top.vulpine.simpleLobby.listener.WorldListener;
import top.vulpine.simpleLobby.scheduler.BukkitSchedulerAdapter;
import top.vulpine.simpleLobby.scheduler.FoliaScheduler;
import top.vulpine.simpleLobby.scheduler.SchedulerAdapter;
import top.vulpine.simpleLobby.utils.ActionParser;
import top.vulpine.simpleLobby.utils.logger.LogLevel;
import top.vulpine.simpleLobby.utils.logger.Logger;

import java.io.File;

/**
 * Main class for the SimpleLobby plugin.
 * This class initializes the plugin, sets up logging, and registers commands and event listeners.
 * It also provides access to the ActionParser for executing actions defined in the configuration.
 */
@Getter
public final class SimpleLobby extends JavaPlugin {

    private Config configuration;

    private ActionParser actionParser;
    private SchedulerAdapter scheduler;

    private static final int PLUGIN_ID = 28227;

    @Override
    public void onEnable() {

        try {
            configuration = ConfigManager.create(Config.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                it.withBindFile(new File(this.getDataFolder(), "config.yml"));
                it.saveDefaults();
                it.load(true);
            });
        } catch (Exception e) {
            Logger.error("Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        LogLevel logLevel;
        try {
            logLevel = configuration.logLevel;
        } catch (IllegalArgumentException e) {
            Logger.warn("Invalid log level in config, defaulting to INFO");
            logLevel = LogLevel.INFO;
        }
        Logger.init(logLevel);

        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        this.scheduler = folia ? new FoliaScheduler(this) : new BukkitSchedulerAdapter(this);
        Logger.debug("Detected " + (folia ? "Folia" : "Bukkit/Spigot") + " server, using "
                + scheduler.getClass().getSimpleName());

        String[] message = {
                "",
                "<white>     _____ <green>__",
                "<white>    |   __<green>|  |",
                "<white>    |__   <green>|  |__",
                "<white>    |_____<green>|_____|",
                "",
                "<white>    By <green>" + String.join(", ", getDescription().getAuthors()),
                "<white>    Version: <green>" + getDescription().getVersion(),
                ""
        };

        for (String line : message) {
            Logger.system(line);
        }

        Logger.debug("Loading Action Parser...");
        actionParser = new ActionParser(this);

        Logger.debug("Registering commands and listeners...");
        getCommand("simplelobby").setExecutor(new SimpleLobbyCommand(this));

        SpawnCommand spawnCommand = new SpawnCommand(this);
        getCommand("spawn").setExecutor(spawnCommand);
        getServer().getPluginManager().registerEvents(spawnCommand, this);

        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        Logger.debug("Initializing metrics...");
        new Metrics(this, PLUGIN_ID);

        new UpdateNotifier(this, "simplelobby",
                "<gray>[<b><white>S<green>L<gray></b>] <white>A new version of SimpleLobby is available! <gray>(<st>%current%</st> <green>%new%<gray>)");

        Logger.system("SimpleLobby has been enabled successfully.");
    }

}
