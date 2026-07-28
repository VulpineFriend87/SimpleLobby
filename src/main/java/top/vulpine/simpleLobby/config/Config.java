package top.vulpine.simpleLobby.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import org.bukkit.Location;
import top.vulpine.simpleLobby.util.logger.LogLevel;

import java.util.ArrayList;
import java.util.List;

@Header("SimpleLobby Configuration - By Vulpine (https://vulpine.top)")
@Header("")
@Header("Wherever you see the [actions] tag, you can put in a list all the actions you want to perform in order.")
@Header("These are the actions you can use:")
@Header("")
@Header("   <required> [optional]")
@Header("")
@Header("- \"[COMMAND] <console/player>; <command without slash>\"")
@Header("- \"[GAMEMODE] <global/player>; <gamemode>\"")
@Header("- \"[TITLE] <global/player>; <title>; [subtitle]; [fadeIn]; [stay]; [fadeOut]\"")
@Header("- \"[ACTIONBAR] <global/player>; <message>\"")
@Header("- \"[MESSAGE] <global/player>; <message>\"")
@Header("- \"[SOUND] <global/player>; <sound>; [volume]; [pitch]\"")
@Header("- \"[DELAY] <milliseconds>\"")
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

            @Comment("[actions] Placeholders: %time%; requires spawn command delay to be enabled")
            @CustomKey("delay_started")
            public List<String> delayStarted = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &aYou will be teleported to spawn in %time% second(s)"
            ));

            @Comment("[actions] Placeholders: %time%; requires spawn command delay to be enabled and require_player_still to be true")
            @CustomKey("delay_started_still")
            public List<String> delayStartedStill = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &aYou will be teleported to spawn in %time% second(s). Do not move."
            ));

            @Comment("[actions] Requires spawn command delay to be enabled and require_player_still to be true")
            @CustomKey("teleport_canceled")
            public List<String> teleportCanceled = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &cTeleport to spawn canceled, you moved."
            ));

            @Comment("[actions]")
            @CustomKey("teleported")
            public List<String> teleported = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &aYou have been teleported to spawn."
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

            @Comment("[actions] Placeholders: %player%, any PlaceholderAPI placeholder (if installed)")
            @CustomKey("actions")
            public List<String> actions = new ArrayList<>(List.of(
                    "[TITLE] player; &bWelcome to the server!; &7Enjoy your stay!; 20; 60; 20",
                    "[MESSAGE] global; &b%player% &7has joined the server.",
                    "[GAMEMODE] player; adventure"
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

            @Comment("[actions] Placeholders: %player%, any PlaceholderAPI placeholder (if installed)")
            @CustomKey("actions")
            public List<String> actions = new ArrayList<>(List.of(
                    "[MESSAGE] global; &b%player% &7has left the server."
            ));

        }

    }

    @Comment("General world options")
    @CustomKey("options")
    public Options options = new Options();

    public static class Options extends OkaeriConfig {

        @CustomKey("disable_hunger_loss")
        public DisableHungerLoss disableHungerLoss = new DisableHungerLoss();

        public static class DisableHungerLoss extends OkaeriConfig {

            @CustomKey("enabled")
            public boolean enabled = true;

            @CustomKey("whitelist")
            public Whitelist whitelist = new Whitelist();
            
            public static class Whitelist extends OkaeriConfig {

                @CustomKey("enabled")
                public boolean enabled = false;

                @Comment("List of worlds where hunger loss is disabled")
                @CustomKey("worlds")
                public List<String> worlds = new ArrayList<>();

            }

        }

        @CustomKey("disable_mob_spawning")
        public DisableMobSpawning disableMobSpawning = new DisableMobSpawning();

        public static class DisableMobSpawning extends OkaeriConfig {

            @CustomKey("enabled")
            public boolean enabled = true;

            @CustomKey("whitelist")
            public Whitelist whitelist = new Whitelist();

            public static class Whitelist extends OkaeriConfig {

                @CustomKey("enabled")
                public boolean enabled = false;

                @Comment("List of worlds where mob spawning is disabled")
                @CustomKey("worlds")
                public List<String> worlds = new ArrayList<>();

            }

        }

        @CustomKey("disable_damage")
        public DisableDamage disableDamage = new DisableDamage();

        public static class DisableDamage extends OkaeriConfig {

            @CustomKey("enabled")
            public boolean enabled = true;

            @CustomKey("whitelist")
            public Whitelist whitelist = new Whitelist();

            public static class Whitelist extends OkaeriConfig {

                @CustomKey("enabled")
                public boolean enabled = false;
                @Comment("List of worlds where damage is disabled")

                @CustomKey("worlds")
                public List<String> worlds = new ArrayList<>();

            }

        }

        @CustomKey("clear_inventory_on_join")
        public ClearInventoryOnJoin clearInventoryOnJoin = new ClearInventoryOnJoin();

        public static class ClearInventoryOnJoin extends OkaeriConfig {
            @CustomKey("enabled")
            public boolean enabled = true;
        }

        @CustomKey("clear_effects_on_join")
        public ClearEffectsOnJoin clearEffectsOnJoin = new ClearEffectsOnJoin();

        public static class ClearEffectsOnJoin extends OkaeriConfig {
            @CustomKey("enabled")
            public boolean enabled = false;
        }

        @CustomKey("disable_block_placing")
        public DisableBlockPlacing disableBlockPlacing = new DisableBlockPlacing();

        public static class DisableBlockPlacing extends OkaeriConfig {

            @CustomKey("enabled")
            public boolean enabled = true;

            @Comment("If true, players in creative mode can place blocks")
            @CustomKey("creative_bypass")
            public boolean creativeBypass = true;

            @CustomKey("whitelist")
            public Whitelist whitelist = new Whitelist();

            public static class Whitelist extends OkaeriConfig {

                @CustomKey("enabled")
                public boolean enabled = false;

                @Comment("List of worlds where block placing is disabled")
                @CustomKey("worlds")
                public List<String> worlds = new ArrayList<>();

            }

        }

        @CustomKey("disable_block_breaking")
        public DisableBlockBreaking disableBlockBreaking = new DisableBlockBreaking();

        public static class DisableBlockBreaking extends OkaeriConfig {

            @CustomKey("enabled")
            public boolean enabled = true;

            @Comment("If true, players in creative mode can break blocks")
            @CustomKey("creative_bypass")
            public boolean creativeBypass = true;

            @CustomKey("whitelist")
            public Whitelist whitelist = new Whitelist();

            public static class Whitelist extends OkaeriConfig {

                @CustomKey("enabled")
                public boolean enabled = false;

                @Comment("List of worlds where block breaking is disabled")
                @CustomKey("worlds")
                public List<String> worlds = new ArrayList<>();

            }

        }

        @CustomKey("disable_block_interaction")
        public DisableBlockInteraction disableBlockInteraction = new DisableBlockInteraction();

        public static class DisableBlockInteraction extends OkaeriConfig {

            @CustomKey("enabled")
            public boolean enabled = true;

            @Comment("If true, players in creative mode can interact with blocks")
            @CustomKey("creative_bypass")
            public boolean creativeBypass = true;

            @CustomKey("whitelist")
            public Whitelist whitelist = new Whitelist();

            public static class Whitelist extends OkaeriConfig {

                @CustomKey("enabled")
                public boolean enabled = false;

                @Comment("List of worlds where block interaction is disabled")
                @CustomKey("worlds")
                public List<String> worlds = new ArrayList<>();

            }

        }

    }

    @CustomKey("messages")
    public Messages messages = new Messages();

    public static class Messages extends OkaeriConfig {

        @CustomKey("no_permission")
        public String noPermission = "&7[&f&lS&a&lL&7] &cMissing permission!";

        @CustomKey("only_players")
        public String onlyPlayers = "&7[&f&lS&a&lL&7] &cThis command can only be executed by players.";

        @Comment("Placeholders: %time%")
        @CustomKey("reloaded")
        public String reloaded = "&7[&f&lS&a&lL&7] &aConfiguration reloaded in &f%time%ms&a.";

        @Comment("Placeholders: %world%, %x%, %y%, %z%, %yaw%, %pitch%")
        @CustomKey("spawn_set")
        public String spawnSet = "&7[&f&lS&a&lL&7] &aSuccessfully set the spawn in &f%world%&a at &f%x%&a, &f%y%&a, &f%z%&a, &fyaw:%yaw%&a, &fpitch:%pitch%&a.";

        @CustomKey("spawn_not_set")
        public String spawnNotSet = "&7[&f&lS&a&lL&7] &cThe spawn has not been set yet. Contact an administrator.";

        @CustomKey("unknown_command")
        public String unknownCommand = "&7[&f&lS&a&lL&7] &cUnknown command!";

    }

    @Comment("Log level for the plugin. Can be: DEBUG, INFO, WARN, ERROR.")
    @Comment("Leave as it is if you don't know what to choose.")
    @CustomKey("log_level")
    public LogLevel logLevel = LogLevel.INFO;

}
