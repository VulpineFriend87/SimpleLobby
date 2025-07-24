package top.vulpine.simpleLobby.command.subCommands;

import org.bukkit.command.CommandSender;
import top.vulpine.simpleLobby.command.SimpleLobbyCommand;
import top.vulpine.simpleLobby.instance.SubCommand;
import top.vulpine.simpleLobby.utils.Colorize;

import java.util.List;

public class ReloadSubCommand implements SubCommand {

    private final SimpleLobbyCommand command;

    public ReloadSubCommand(SimpleLobbyCommand command) {
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        long startTime = System.currentTimeMillis();
        command.getPlugin().reloadConfig();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        sender.sendMessage(Colorize.color(
                command.getPlugin().getConfig().getString("messages.reloaded")
                        .replace("%time%", String.valueOf(duration))
        ));

    }

    public List<String> executeTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    public SimpleLobbyCommand getCommand() {
        return command;
    }

}
