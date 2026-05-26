package top.vulpine.simpleLobby.command.subCommands;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.command.SimpleLobbyCommand;
import top.vulpine.simpleLobby.config.Config;
import top.vulpine.simpleLobby.instance.SubCommand;
import top.vulpine.simpleLobby.utils.Colorize;

import java.util.List;

@Getter
public class SetSpawnSubCommand implements SubCommand {

    private final SimpleLobbyCommand command;

    public SetSpawnSubCommand(SimpleLobbyCommand command) {
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        SimpleLobby plugin = command.getPlugin();
        Config config = plugin.getConfiguration();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Colorize.color(config.messages.onlyPlayers));
            return;
        }

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

    public List<String> executeTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
