package top.vulpine.simpleLobby.command.subCommands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import top.vulpine.simpleLobby.command.SimpleLobbyCommand;
import top.vulpine.simpleLobby.config.impl.Config;
import top.vulpine.simpleLobby.instance.SubCommand;
import top.vulpine.simpleLobby.utils.Colorize;

import java.util.List;

@Getter
public class ReloadSubCommand implements SubCommand {

    private final SimpleLobbyCommand command;

    public ReloadSubCommand(SimpleLobbyCommand command) {
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Config config = command.getPlugin().getConfiguration();

        long startTime = System.currentTimeMillis();
        config.load();
        long duration = System.currentTimeMillis() - startTime;

        sender.sendMessage(Colorize.color(
                config.messages.reloaded.replace("%time%", String.valueOf(duration))
        ));

    }

    public List<String> executeTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
