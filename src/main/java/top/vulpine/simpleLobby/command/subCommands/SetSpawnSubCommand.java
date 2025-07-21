package top.vulpine.simpleLobby.command.subCommands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import top.vulpine.simpleLobby.command.SimpleLobbyCommand;
import top.vulpine.simpleLobby.instance.SubCommand;
import top.vulpine.simpleLobby.utils.Colorize;

import java.util.List;

public class SetSpawnSubCommand implements SubCommand {

    private final SimpleLobbyCommand command;

    public SetSpawnSubCommand(SimpleLobbyCommand command) {
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Colorize.color(
                    "&7[&f&lS&a&lL&7] &cThis command can only be executed by a player."
            ));
            return;
        }

        Location location = player.getLocation();
        FileConfiguration config = command.getPlugin().getConfig();

        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        config.set("spawn.location.world", world);
        config.set("spawn.location.x", x);
        config.set("spawn.location.y", y);
        config.set("spawn.location.z", z);
        config.set("spawn.location.yaw", yaw);
        config.set("spawn.location.pitch", pitch);

        command.getPlugin().saveConfig();

        player.sendMessage(Colorize.color(
                "&7[&f&lS&a&lL&7] &aSuccessfully set the spawn in &f" + world + "&a to &f" + x + "&a, &f" + y + "&a, &f" + z + "&a, &fyaw:" + yaw + "&a, &fpitch:" + pitch + "&a!"
        ));

    }

    public List<String> executeTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    public SimpleLobbyCommand getCommand() {
        return command;
    }
}
