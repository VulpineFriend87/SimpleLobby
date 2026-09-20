package top.vulpine.simpleLobby.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.vulpine.actions.action.Action;
import top.vulpine.actions.action.impl.GamemodeAction;
import top.vulpine.actions.action.impl.MessageAction;
import top.vulpine.actions.action.impl.TitleAction;
import top.vulpine.actions.target.Target;
import top.vulpine.commons.log.LogLevel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Header("SimpleLobby Configuration - By Vulpine (https://vulpine.top)")
@Header("")
@Header("Action lists can send messages and titles, play sounds, run commands, wait,")
@Header("and branch on conditions. Every action, target and placeholder is documented at:")
@Header("")
@Header("   https://github.com/VulpineFriend87/Actions")
@Header("")
public class Config extends OkaeriConfig {

    @CustomKey("spawn")
    public Spawn spawn = new Spawn();

    public static class Spawn extends OkaeriConfig {

        @CustomKey("command")
        public Command command = new Command();

        public static class Command extends OkaeriConfig {

            @Comment("If enabled, players can use the '/spawn' command to teleport to spawn")
            @CustomKey("enabled")
            public boolean enabled = true;

            @Comment("If enabled, players will have to wait a certain amount of time before being teleported to spawn")
            @CustomKey("delay")
            public Delay delay = new Delay();

            public static class Delay extends OkaeriConfig {

                @CustomKey("enabled")
                public boolean enabled = false;

                @Comment("Time in seconds")
                @CustomKey("time")
                public int time = 3;

                @Comment("If true, the player must not move during the delay time, or else the teleport will be canceled")
                @CustomKey("require_player_still")
                public boolean requirePlayerStill = true;

            }

        }

        @Comment("If true, players will be teleported to spawn when they join the server")
        @CustomKey("tp_on_join")
        public boolean tpOnJoin = false;

        @Comment("Can be set using the '/sl setspawn' command in-game")
        @CustomKey("location")
        public Location location = null;

        @Comment("Placeholders: any PlaceholderAPI placeholder (if installed)")
        @CustomKey("actions")
        public Actions actions = new Actions();

        public static class Actions extends OkaeriConfig {

            @Comment("Placeholders: %time%; requires spawn command delay to be enabled")
            @CustomKey("delay_started")
            public List<Action> delayStarted = new ArrayList<>(List.of(
                    MessageAction.builder()
                            .text("<gray>[<bold><white>S<green>L</bold><gray>] <green>You will be teleported to spawn in %time% second(s)")
                            .build()
            ));

            @Comment("Placeholders: %time%; requires spawn command delay to be enabled and require_player_still to be true")
            @CustomKey("delay_started_still")
            public List<Action> delayStartedStill = new ArrayList<>(List.of(
                    MessageAction.builder()
                            .text("<gray>[<bold><white>S<green>L</bold><gray>] <green>You will be teleported to spawn in %time% second(s). Do not move.")
                            .build()
            ));

            @Comment("Requires spawn command delay to be enabled and require_player_still to be true")
            @CustomKey("teleport_canceled")
            public List<Action> teleportCanceled = new ArrayList<>(List.of(
                    MessageAction.builder()
                            .text("<gray>[<bold><white>S<green>L</bold><gray>] <red>Teleport to spawn canceled, you moved.")
                            .build()
            ));

            @CustomKey("teleported")
            public List<Action> teleported = new ArrayList<>(List.of(
                    MessageAction.builder()
                            .text("<gray>[<bold><white>S<green>L</bold><gray>] <green>You have been teleported to spawn.")
                            .build()
            ));

        }

    }

    @CustomKey("actions")
    public Actions actions = new Actions();

    public static class Actions extends OkaeriConfig {

        @CustomKey("join")
        public Join join = new Join();

        public static class Join extends OkaeriConfig {

            @Comment("If true, the default join message will not be shown")
            @CustomKey("suppress_default_message")
            public boolean suppressDefaultMessage = true;

            @CustomKey("enabled")
            public boolean enabled = true;

            @Comment("Placeholders: %player%, any PlaceholderAPI placeholder (if installed)")
            @CustomKey("actions")
            public List<Action> actions = new ArrayList<>(List.of(

                    TitleAction.builder()
                            .title("<aqua>Welcome to the server!")
                            .subtitle("<gray>Enjoy your stay!")
                            .fadeIn("20t")
                            .stay("60t")
                            .fadeOut("20t")
                            .build(),

                    MessageAction.builder()
                            .target(Target.ALL)
                            .text("<aqua>%player% <gray>has joined the server.")
                            .build(),

                    GamemodeAction.builder()
                            .mode(GameMode.ADVENTURE)
                            .build()
            ));

        }

        @CustomKey("quit")
        public Quit quit = new Quit();

        public static class Quit extends OkaeriConfig {

            @Comment("If true, the default quit message will not be shown")
            @CustomKey("suppress_default_message")
            public boolean suppressDefaultMessage = true;

            @CustomKey("enabled")
            public boolean enabled = true;

            @Comment("Placeholders: %player%, any PlaceholderAPI placeholder (if installed)")
            @CustomKey("actions")
            public List<Action> actions = new ArrayList<>(List.of(
                    MessageAction.builder()
                            .target(Target.ALL)
                            .text("<aqua>%player% <gray>has left the server.")
                            .build()
            ));

        }

    }

    @Comment("General world options")
    @CustomKey("options")
    public Options options = new Options();

    public static class Options extends OkaeriConfig {

        @CustomKey("disable_hunger_loss")
        public Toggle disableHungerLoss = new Toggle();

        @CustomKey("disable_mob_spawning")
        public Toggle disableMobSpawning = new Toggle();

        @CustomKey("disable_damage")
        public Toggle disableDamage = new Toggle();

        @CustomKey("clear_inventory_on_join")
        public Toggle clearInventoryOnJoin = new Toggle();

        @CustomKey("clear_effects_on_join")
        public Toggle clearEffectsOnJoin = Toggle.off();

        @Comment("Also covers emptying a bucket, which does not count as placing a block")
        @CustomKey("disable_block_placing")
        public PlayerToggle disableBlockPlacing = new PlayerToggle();

        @Comment("Also covers filling a bucket, which does not count as breaking a block")
        @CustomKey("disable_block_breaking")
        public PlayerToggle disableBlockBreaking = new PlayerToggle();

        @CustomKey("disable_block_interaction")
        public PlayerToggle disableBlockInteraction = new PlayerToggle();

        @Comment("Stops farmland and turtle eggs from being destroyed by walking over them")
        @CustomKey("disable_trampling")
        public PlayerToggle disableTrampling = new PlayerToggle();

        @Comment("Stops players hitting or right-clicking mobs, armor stands, item frames and paintings")
        @CustomKey("disable_entity_interaction")
        public PlayerToggle disableEntityInteraction = new PlayerToggle();

        @CustomKey("disable_item_dropping")
        public PlayerToggle disableItemDropping = new PlayerToggle();

        @CustomKey("disable_item_pickup")
        public PlayerToggle disableItemPickup = new PlayerToggle();

        @Comment("Stops players moving items around their own inventory. Chests and plugin menus are left alone")
        @CustomKey("disable_inventory_interaction")
        public PlayerToggle disableInventoryInteraction = new PlayerToggle();

        @Comment("Stops fire being lit and stops blocks burning away")
        @CustomKey("disable_fire")
        public PlayerToggle disableFire = new PlayerToggle();

        @Comment("Stops explosions destroying blocks. Damage to players is covered by disable_damage")
        @CustomKey("disable_explosions")
        public Toggle disableExplosions = new Toggle();

    }

    /**
     * An option that can be switched off, or limited to a list of worlds.
     */
    public static class Toggle extends OkaeriConfig {

        @CustomKey("enabled")
        public boolean enabled = true;

        @CustomKey("whitelist")
        public Whitelist whitelist = new Whitelist();

        /**
         * @return a toggle that starts out switched off
         */
        public static Toggle off() {
            Toggle toggle = new Toggle();
            toggle.enabled = false;
            return toggle;
        }

        /**
         * @param player the player the event belongs to
         * @return true if this option should take effect for them
         */
        public boolean appliesTo(Player player) {
            return appliesIn(player.getWorld().getName());
        }

        /**
         * @param world name of the world the event happened in
         * @return true if this option should take effect there
         */
        public boolean appliesIn(String world) {
            return this.enabled && this.whitelist.covers(world);
        }

    }

    /**
     * A {@link Toggle} for something a player does, which creative mode can be let through.
     */
    public static class PlayerToggle extends OkaeriConfig {

        @CustomKey("enabled")
        public boolean enabled = true;

        @Comment("If true, players in creative mode are not affected by this option")
        @CustomKey("creative_bypass")
        public boolean creativeBypass = true;

        @CustomKey("whitelist")
        public Whitelist whitelist = new Whitelist();

        /**
         * @param player the player the event belongs to
         * @return true if this option should take effect for them
         */
        public boolean appliesTo(Player player) {

            if (this.creativeBypass && player.getGameMode() == GameMode.CREATIVE) {
                return false;
            }

            return appliesIn(player.getWorld().getName());
        }

        /**
         * For the same rule applied to something that is not a player, where there is no
         * gamemode to bypass with.
         *
         * @param world name of the world the event happened in
         * @return true if this option should take effect there
         */
        public boolean appliesIn(String world) {
            return this.enabled && this.whitelist.covers(world);
        }

    }

    /**
     * The worlds an option is limited to. Switched off, the option applies everywhere.
     */
    public static class Whitelist extends OkaeriConfig {

        @CustomKey("enabled")
        public boolean enabled = false;

        @Comment("List of worlds the option above applies in")
        @CustomKey("worlds")
        public List<String> worlds = new ArrayList<>();

        /**
         * @param world name of the world the event happened in
         * @return true if the option reaches that world
         */
        public boolean covers(String world) {
            return !this.enabled || this.worlds.contains(world);
        }

    }

    @CustomKey("messages")
    public Messages messages = new Messages();

    public static class Messages extends OkaeriConfig {

        @CustomKey("only_players")
        public String onlyPlayers = "<gray>[<bold><white>S<green>L</bold><gray>] <red>This command can only be executed by players.";

        @Comment("Placeholders: %time%")
        @CustomKey("reloaded")
        public String reloaded = "<gray>[<bold><white>S<green>L</bold><gray>] <green>Configuration reloaded in <white>%time%ms<green>.";

        @Comment("Placeholders: %world%, %x%, %y%, %z%, %yaw%, %pitch%")
        @CustomKey("spawn_set")
        public String spawnSet = "<gray>[<bold><white>S<green>L</bold><gray>] <green>Successfully set the spawn in <white>%world%<green> at <white>%x%<green>, <white>%y%<green>, <white>%z%<green>, <white>yaw:%yaw%<green>, <white>pitch:%pitch%<green>.";

        @CustomKey("spawn_not_set")
        public String spawnNotSet = "<gray>[<bold><white>S<green>L</bold><gray>] <red>The spawn has not been set yet. Contact an administrator.";

    }

    @Comment("Action lists you can call from anywhere with the 'run' action, so a")
    @Comment("sequence written once is shared instead of copied into every event.")
    @CustomKey("sequences")
    public Map<String, List<Action>> sequences = new LinkedHashMap<>();

    @Comment("Log level for the plugin. Can be: DEBUG, INFO, WARN, ERROR.")
    @Comment("Leave as it is if you don't know what to choose.")
    @CustomKey("log_level")
    public LogLevel logLevel = LogLevel.INFO;

}
