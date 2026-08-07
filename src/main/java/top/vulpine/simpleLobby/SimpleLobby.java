package top.vulpine.simpleLobby;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import top.vulpine.actions.Actions;
import top.vulpine.actions.BuiltinActions;
import top.vulpine.actions.action.ActionRegistry;
import top.vulpine.actions.action.ActionSerdes;
import top.vulpine.actions.action.SequenceRegistry;
import top.vulpine.commons.text.Colorize;
import top.vulpine.commons.text.Dialect;
import top.vulpine.simpleLobby.command.MainCommand;
import top.vulpine.simpleLobby.command.SpawnCommand;
import top.vulpine.simpleLobby.command.annotation.RequiresPermission;
import top.vulpine.simpleLobby.command.exception.ExceptionHandler;
import top.vulpine.simpleLobby.config.LegacyActions;
import top.vulpine.simpleLobby.util.PermissionChecker;
import top.vulpine.simpleLobby.config.Config;
import top.vulpine.simpleLobby.listener.PlayerListener;
import top.vulpine.simpleLobby.listener.WorldListener;
import top.vulpine.simpleLobby.util.ActionRunner;
import top.vulpine.commons.log.LogAction;
import top.vulpine.commons.log.Logger;

import java.io.File;
import java.nio.file.Path;

/**
 * Main class for the SimpleLobby plugin.
 * This class initializes the plugin, sets up logging, and registers commands and event listeners.
 * It also provides access to the ActionParser for executing actions defined in the configuration.
 */
@Getter
public final class SimpleLobby extends JavaPlugin {

    private Config configuration;

    private ActionRunner actions;
    private SequenceRegistry sequences;
    private FoliaLib foliaLib;

    private static final int PLUGIN_ID = 28227;

    private static final String LOG_PREFIX = "<dark_gray>[<white>Simple<green>Lobby<dark_gray>] <reset>";

    private static final String MODRINTH = "https://modrinth.com/plugin/simplelobby";

    private enum Action implements LogAction {
        CONFIG, SETUP, ACTIONS
    }

    @Override
    public void onEnable() {

        if (!hasPaperApi()) {
            getLogger().severe("Spigot support was dropped in SimpleLobby 1.5, so this version will not start here.");
            getLogger().severe("1.4.1 is the last version that runs on Spigot.");
            getLogger().severe("I recommend switching to Paper, or a fork of it such as Purpur or Folia.");
            getLogger().severe("Latest version: " + MODRINTH);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Colorize.init(Dialect.LEGACY);

        Logger.builder().prefix(LOG_PREFIX).build();

        // Bad actions are warned about and skipped rather than thrown, so route those
        // warnings through the plugin's own logger before anything is read.
        Actions.logger(message -> Logger.warn(Action.ACTIONS, message));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Actions.placeholders(PlaceholderAPI::setPlaceholders);
            Logger.debug(Action.SETUP, "PlaceholderAPI found, placeholders in actions will be expanded.");
        }

        if (!loadConfiguration()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.foliaLib = new FoliaLib(this);
        Logger.debug(Action.SETUP, "Scheduling through FoliaLib, detected platform: " + foliaLib.getImplType() + ".");

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

        Logger.debug(Action.SETUP, "Preparing the action runner...");
        actions = new ActionRunner(this);

        Logger.debug(Action.SETUP, "Registering commands and listeners...");

        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this)
                .exceptionHandler(new ExceptionHandler(this))
                .permissionForAnnotation(RequiresPermission.class, annotation ->
                        actor -> PermissionChecker.hasPermission(actor.sender(), annotation.value()))
                .build();

        SpawnCommand spawnCommand = new SpawnCommand(this);

        lamp.register(new MainCommand(this), spawnCommand);

        getServer().getPluginManager().registerEvents(spawnCommand, this);

        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        Logger.debug(Action.SETUP, "Initializing metrics...");
        new Metrics(this, PLUGIN_ID);

        new UpdateNotifier(this, "simplelobby",
                "<gray>[<b><white>S<green>L<gray></b>] <white>A new version of SimpleLobby is available! <gray>(<st>%current%</st> <green>%new%<gray>)");

        Logger.system("SimpleLobby has been enabled successfully.");
    }

    @Override
    public void onDisable() {

        if (actions != null) {
            actions.cancelAll();
        }

        if (foliaLib != null) {
            foliaLib.getScheduler().cancelAllTasks();
        }

    }

    /**
     * Reads the config and rebuilds the named sequences.
     *
     * <p>A config written for 1.x is understood as it stands, and rewritten into the
     * new format on load. The old file is copied aside first, because okaeri
     * regenerates the whole document and any comments the operator added by hand do
     * not survive that.</p>
     *
     * @return false if the config could not be read
     */
    public boolean loadConfiguration() {

        ActionRegistry registry = BuiltinActions.registry();
        LegacyActions.registerSoundNames(registry);

        Path file = new File(this.getDataFolder(), "config.yml").toPath();
        boolean legacy = LegacyActions.isLegacy(file);

        if (legacy) {

            Path backup = LegacyActions.backup(file);

            if (backup == null) {
                Logger.error(Action.CONFIG, "Could not back up config.yml, so it will not be migrated. "
                        + "Check the plugin folder is writable.");
                return false;
            }

            Logger.info(Action.CONFIG, "Your config uses the 1.x action format and will be rewritten. "
                    + "A copy was saved as " + backup.getFileName() + ".");
        }

        try {
            configuration = ConfigManager.create(Config.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit(),
                        new ActionSerdes(registry).migrateShorthand(true));
                it.withBindFile(file.toFile());
                it.saveDefaults();
                it.load(true);
            });
        } catch (Exception e) {
            Logger.error(Action.CONFIG, "Failed to load configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        Logger.setLevel(configuration.logLevel);

        // Rebuilt on every load so a reload picks up edited sequences.
        sequences = new SequenceRegistry();
        sequences.putAll(configuration.sequences);

        if (legacy) {
            Logger.info(Action.CONFIG, "config.yml has been migrated. See "
                    + "https://github.com/VulpineFriend87/Actions for what the new format can do.");
        }

        return true;
    }

    public PlatformScheduler getScheduler() {
        return foliaLib.getScheduler();
    }

    private static boolean hasPaperApi() {

        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }

    }

}
