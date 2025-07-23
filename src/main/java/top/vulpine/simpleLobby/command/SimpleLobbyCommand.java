package top.vulpine.simpleLobby.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.command.subCommands.ReloadSubCommand;
import top.vulpine.simpleLobby.command.subCommands.SetSpawnSubCommand;
import top.vulpine.simpleLobby.instance.SubCommand;
import top.vulpine.simpleLobby.utils.Colorize;
import top.vulpine.simpleLobby.utils.PermissionChecker;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleLobbyCommand implements CommandExecutor, TabCompleter {

    private final SimpleLobby plugin;

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public SimpleLobbyCommand(SimpleLobby plugin) {
        this.plugin = plugin;

        subCommands.put("reload", new ReloadSubCommand(this));
        subCommands.put("setspawn", new SetSpawnSubCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        executeSubCommand(sender, args);

        return false;
    }

    private void executeSubCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {

            sender.sendMessage(Colorize.color(
                    "&r\n&7 This server is running\n&r\n&f Simple&aLobby &7[v" + plugin.getDescription().getVersion() + "] " +
                            "\n&7 By " + String.join(", ", plugin.getDescription().getAuthors()) +
                            "\n&r"
            ));

            return;

        }

        if (!PermissionChecker.hasPermission(sender, "command." + args[0].toLowerCase())) {

            sender.sendMessage(Colorize.color(
                    plugin.getConfig().getString("messages.no_permission")
            ));

            return;

        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());

        if (subCommand != null) {

            subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));

        } else {

            sender.sendMessage(Colorize.color(
                    plugin.getConfig().getString("messages.unknown_command")
            ));

        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(cmd -> PermissionChecker.hasPermission(sender, "command." + cmd.toLowerCase()))
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {

            return subCommand.executeTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return Collections.emptyList();
    }

    public SimpleLobby getPlugin() {
        return plugin;
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }
}
