package top.vulpine.simpleLobby.command;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.scheduler.Cancellable;
import top.vulpine.simpleLobby.util.ActionParser;
import top.vulpine.commons.text.Colorize;
import top.vulpine.simpleLobby.util.PermissionChecker;
import top.vulpine.simpleLobby.util.PlayerUtils;
import top.vulpine.simpleLobby.util.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SpawnCommand implements CommandExecutor, TabCompleter, Listener {

    private final SimpleLobby plugin;
    private final ActionParser actionParser;
    private final Map<UUID, Cancellable> tasks = new ConcurrentHashMap<>();
    private final Map<UUID, Location> locations = new ConcurrentHashMap<>();

    public SpawnCommand(SimpleLobby plugin) {
        this.plugin = plugin;
        this.actionParser = new ActionParser(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!PermissionChecker.hasPermission(sender, "command.spawn")) {
            sender.sendMessage(Colorize.color(plugin.getConfiguration().messages.noPermission));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Colorize.color(plugin.getConfiguration().messages.onlyPlayers));
            return true;
        }

        if (!plugin.getConfiguration().spawn.command.enabled) {
            return true;
        }

        Location spawn = plugin.getConfiguration().spawn.location;
        if (spawn == null || spawn.getWorld() == null) {
            player.sendMessage(Colorize.color(plugin.getConfiguration().messages.spawnNotSet));
            return true;
        }

        if (plugin.getConfiguration().spawn.command.delay.enabled) {

            int seconds = plugin.getConfiguration().spawn.command.delay.time;
            boolean requireStill = plugin.getConfiguration().spawn.command.delay.requirePlayerStill;

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(seconds));

            List<String> actions = requireStill ? 
                plugin.getConfiguration().spawn.actions.delayStartedStill :
                plugin.getConfiguration().spawn.actions.delayStarted;

            actionParser.executeActions(actions, player, 0, placeholders);

            if (requireStill) {

                UUID uuid = player.getUniqueId();
                locations.put(uuid, player.getLocation().clone());

                Cancellable task = plugin.getScheduler().runEntityLater(player, () -> {
                    PlayerUtils.teleportPlayer(plugin, player);
                    tasks.remove(uuid);
                    locations.remove(uuid);

                    List<String> teleportActions = plugin.getConfiguration().spawn.actions.teleported;
                    actionParser.executeActions(teleportActions, player, 0, new HashMap<>());
                }, seconds * 20L);

                tasks.put(uuid, task);

            } else {

                plugin.getScheduler().runEntityLater(player,
                        () -> PlayerUtils.teleportPlayer(plugin, player),
                        seconds * 20L);

            }

        } else {

            PlayerUtils.teleportPlayer(plugin, player);

            List<String> teleportActions = plugin.getConfiguration().spawn.actions.teleported;
            actionParser.executeActions(teleportActions, player, 0, new HashMap<>());

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        return List.of();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!plugin.getConfiguration().spawn.command.delay.requirePlayerStill) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!locations.containsKey(uuid)) return;
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {

            Cancellable task = tasks.remove(uuid);
            if (task != null) task.cancel();
            locations.remove(uuid);

            List<String> cancelActions = plugin.getConfiguration().spawn.actions.teleportCanceled;
            actionParser.executeActions(cancelActions, player, 0, new HashMap<>());

            Logger.debug("Player " + player.getName() + " moved while waiting for spawn teleport, teleport canceled.");

        }
    }
}
