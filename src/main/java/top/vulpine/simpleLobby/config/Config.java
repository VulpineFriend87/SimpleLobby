package top.vulpine.simpleLobby.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import org.bukkit.Location;
import top.vulpine.simpleLobby.utils.logger.LogLevel;

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
@Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class Config extends OkaeriConfig {

    public Spawn spawn = new Spawn();

    @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Spawn extends OkaeriConfig {

        public Command command = new Command();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class Command extends OkaeriConfig {

            @Comment("If enabled, players can use the '/spawn' command to teleport to spawn")
            public boolean enabled = true;

            @Comment("If enabled, players will have to wait a certain amount of time before being teleported to spawn")
            public Delay delay = new Delay();

            @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
            public static class Delay extends OkaeriConfig {

                public boolean enabled = false;

                @Comment("Time in seconds")
                public int time = 3;

                @Comment("If true, the player must not move during the delay time, or else the teleport will be canceled")
                public boolean requirePlayerStill = true;

            }

        }

        @Comment("If true, players will be teleported to spawn when they join the server")
        public boolean tpOnJoin = false;

        @Comment("Can be set using the '/sl setspawn' command in-game")
        public Location location = new Location(null, 0, 64, 0, 0, 0);

        @Comment("Placeholders: any PlaceholderAPI placeholder (if installed)")
        public Actions actions = new Actions();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class Actions extends OkaeriConfig {

            @Comment("[actions] Placeholders: %time%; requires spawn command delay to be enabled")
            public List<String> delayStarted = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &aYou will be teleported to spawn in %time% second(s)"
            ));

            @Comment("[actions] Placeholders: %time%; requires spawn command delay to be enabled and require_player_still to be true")
            public List<String> delayStartedStill = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &aYou will be teleported to spawn in %time% second(s). Do not move."
            ));

            @Comment("[actions] Requires spawn command delay to be enabled and require_player_still to be true")
            public List<String> teleportCanceled = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &cTeleport to spawn canceled, you moved."
            ));

            @Comment("[actions]")
            public List<String> teleported = new ArrayList<>(List.of(
                    "[MESSAGE] player; &7[&f&lS&a&lL&7] &aYou have been teleported to spawn."
            ));

        }

    }

    public Actions actions = new Actions();

    @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Actions extends OkaeriConfig {

        public Join join = new Join();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class Join extends OkaeriConfig {

            @Comment("If true, the default join message will not be shown")
            public boolean suppressDefaultMessage = true;

            public boolean enabled = true;

            @Comment("[actions] Placeholders: %player%, any PlaceholderAPI placeholder (if installed)")
            public List<String> actions = new ArrayList<>(List.of(
                    "[TITLE] player; &bWelcome to the server!; &7Enjoy your stay!; 20; 60; 20",
                    "[MESSAGE] global; &b%player% &7has joined the server.",
                    "[GAMEMODE] player; adventure"
            ));

        }

        public Quit quit = new Quit();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class Quit extends OkaeriConfig {

            @Comment("If true, the default quit message will not be shown")
            public boolean suppressDefaultMessage = true;

            public boolean enabled = true;

            @Comment("[actions] Placeholders: %player%, any PlaceholderAPI placeholder (if installed)")
            public List<String> actions = new ArrayList<>(List.of(
                    "[MESSAGE] global; &b%player% &7has left the server."
            ));

        }

    }

    @Comment("General world options")
    public Options options = new Options();

    @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Options extends OkaeriConfig {

        public DisableHungerLoss disableHungerLoss = new DisableHungerLoss();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class DisableHungerLoss extends OkaeriConfig {

            public boolean enabled = true;

            public Whitelist whitelist = new Whitelist();

            @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
            public static class Whitelist extends OkaeriConfig {
                public boolean enabled = false;
                @Comment("List of worlds where hunger loss is disabled")
                public List<String> worlds = new ArrayList<>();
            }

        }

        public DisableMobSpawning disableMobSpawning = new DisableMobSpawning();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class DisableMobSpawning extends OkaeriConfig {

            public boolean enabled = true;

            public Whitelist whitelist = new Whitelist();

            @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
            public static class Whitelist extends OkaeriConfig {
                public boolean enabled = false;
                @Comment("List of worlds where mob spawning is disabled")
                public List<String> worlds = new ArrayList<>();
            }

        }

        public DisableDamage disableDamage = new DisableDamage();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class DisableDamage extends OkaeriConfig {

            public boolean enabled = true;

            public Whitelist whitelist = new Whitelist();

            @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
            public static class Whitelist extends OkaeriConfig {
                public boolean enabled = false;
                @Comment("List of worlds where damage is disabled")
                public List<String> worlds = new ArrayList<>();
            }

        }

        public ClearInventoryOnJoin clearInventoryOnJoin = new ClearInventoryOnJoin();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class ClearInventoryOnJoin extends OkaeriConfig {
            public boolean enabled = true;
        }

        public ClearEffectsOnJoin clearEffectsOnJoin = new ClearEffectsOnJoin();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class ClearEffectsOnJoin extends OkaeriConfig {
            public boolean enabled = false;
        }

        public DisableBlockPlacing disableBlockPlacing = new DisableBlockPlacing();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class DisableBlockPlacing extends OkaeriConfig {

            public boolean enabled = true;

            @Comment("If true, players in creative mode can place blocks")
            public boolean creativeBypass = true;

            public Whitelist whitelist = new Whitelist();

            @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
            public static class Whitelist extends OkaeriConfig {
                public boolean enabled = false;
                @Comment("List of worlds where block placing is disabled")
                public List<String> worlds = new ArrayList<>();
            }

        }

        public DisableBlockBreaking disableBlockBreaking = new DisableBlockBreaking();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class DisableBlockBreaking extends OkaeriConfig {

            public boolean enabled = true;

            @Comment("If true, players in creative mode can break blocks")
            public boolean creativeBypass = true;

            public Whitelist whitelist = new Whitelist();

            @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
            public static class Whitelist extends OkaeriConfig {
                public boolean enabled = false;
                @Comment("List of worlds where block breaking is disabled")
                public List<String> worlds = new ArrayList<>();
            }

        }

        public DisableBlockInteraction disableBlockInteraction = new DisableBlockInteraction();

        @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class DisableBlockInteraction extends OkaeriConfig {

            public boolean enabled = true;

            @Comment("If true, players in creative mode can interact with blocks")
            public boolean creativeBypass = true;

            public Whitelist whitelist = new Whitelist();

            @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
            public static class Whitelist extends OkaeriConfig {
                public boolean enabled = false;
                @Comment("List of worlds where block interaction is disabled")
                public List<String> worlds = new ArrayList<>();
            }

        }

    }

    public Messages messages = new Messages();

    @Names(strategy = NameStrategy.SNAKE_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Messages extends OkaeriConfig {

        public String noPermission = "&7[&f&lS&a&lL&7] &cMissing permission!";

        public String onlyPlayers = "&7[&f&lS&a&lL&7] &cThis command can only be executed by players.";

        @Comment("Placeholders: %time%")
        public String reloaded = "&7[&f&lS&a&lL&7] &aConfiguration reloaded in &f%time%ms&a.";

        @Comment("Placeholders: %world%, %x%, %y%, %z%, %yaw%, %pitch%")
        public String spawnSet = "&7[&f&lS&a&lL&7] &aSuccessfully set the spawn in &f%world%&a at &f%x%&a, &f%y%&a, &f%z%&a, &fyaw:%yaw%&a, &fpitch:%pitch%&a.";

        public String unknownCommand = "&7[&f&lS&a&lL&7] &cUnknown command!";

    }

    @Comment("Log level for the plugin. Can be: DEBUG, INFO, WARN, ERROR.")
    @Comment("Leave as it is if you don't know what to choose.")
    public LogLevel logLevel = LogLevel.INFO;

}
