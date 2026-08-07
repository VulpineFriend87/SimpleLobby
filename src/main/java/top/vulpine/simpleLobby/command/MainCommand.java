package top.vulpine.simpleLobby.command;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import top.vulpine.commons.text.Colorize;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.command.annotation.RequiresPermission;
import top.vulpine.simpleLobby.config.Config;

@Getter
@Command({"simplelobby", "sl", "slobby"})
public class MainCommand {

    private final SimpleLobby plugin;

    public MainCommand(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    @Description("Main SimpleLobby command")
    public void info(CommandSender sender) {

        sender.sendMessage(Colorize.color(
                "<reset>\n<gray> This server is running\n<reset>\n<white> Simple<green>Lobby <gray>[v" + plugin.getDescription().getVersion() + "] " +
                        "\n<gray> By " + String.join(", ", plugin.getDescription().getAuthors()) +
                        "\n<reset>"
        ));

    }

    @Subcommand("reload")
    @RequiresPermission("command.reload")
    @Description("Reloads the SimpleLobby configuration")
    public void reload(CommandSender sender) {

        long startTime = System.currentTimeMillis();
        plugin.loadConfiguration();
        long duration = System.currentTimeMillis() - startTime;

        sender.sendMessage(Colorize.color(
                plugin.getConfiguration().messages.reloaded.replace("%time%", String.valueOf(duration))
        ));

    }

    @Subcommand("setspawn")
    @RequiresPermission("command.setspawn")
    @Description("Sets the lobby spawn to your current location")
    public void setSpawn(Player player) {

        Config config = plugin.getConfiguration();
        Location location = player.getLocation();

        config.spawn.location = location;
        config.save();

        player.sendMessage(Colorize.color(
                config.messages.spawnSet
                        .replace("%world%", location.getWorld().getName())
                        .replace("%x%", String.valueOf(location.getX()))
                        .replace("%y%", String.valueOf(location.getY()))
                        .replace("%z%", String.valueOf(location.getZ()))
                        .replace("%yaw%", String.valueOf(location.getYaw()))
                        .replace("%pitch%", String.valueOf(location.getPitch()))
        ));

    }

}
