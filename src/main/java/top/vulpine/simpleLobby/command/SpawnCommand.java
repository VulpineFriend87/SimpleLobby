package top.vulpine.simpleLobby.command;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import top.vulpine.commons.log.Logger;
import top.vulpine.commons.text.Colorize;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.command.annotation.RequiresPermission;
import top.vulpine.simpleLobby.util.ActionParser;
import top.vulpine.simpleLobby.util.PlayerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class SpawnCommand implements Listener {

    private final SimpleLobby plugin;
    private final ActionParser actionParser;
    private final Map<UUID, WrappedTask> tasks = new ConcurrentHashMap<>();
    private final Map<UUID, Location> locations = new ConcurrentHashMap<>();

    public SpawnCommand(SimpleLobby plugin) {
        this.plugin = plugin;
        this.actionParser = new ActionParser(plugin);
    }

    @Command("spawn")
    @RequiresPermission("command.spawn")
    @Description("Teleports the executor to the spawn (if enabled)")
    public void spawn(Player player) {

        if (!plugin.getConfiguration().spawn.command.enabled) {
            return;
        }

        Location spawn = plugin.getConfiguration().spawn.location;
        if (spawn == null || spawn.getWorld() == null) {
            player.sendMessage(Colorize.color(plugin.getConfiguration().messages.spawnNotSet));
            return;
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

            long ticks = Math.max(1L, seconds * 20L);

            if (requireStill) {

                UUID uuid = player.getUniqueId();
                locations.put(uuid, player.getLocation().clone());

                WrappedTask task = plugin.getScheduler().runAtEntityLater(player, () -> {
                    PlayerUtils.teleportPlayer(plugin, player);
                    tasks.remove(uuid);
                    locations.remove(uuid);

                    List<String> teleportActions = plugin.getConfiguration().spawn.actions.teleported;
                    actionParser.executeActions(teleportActions, player, 0, new HashMap<>());
                }, ticks);

                // player was gone
                if (task != null) {
                    tasks.put(uuid, task);
                } else {
                    locations.remove(uuid);
                }

            } else {

                plugin.getScheduler().runAtEntityLater(player,
                        () -> PlayerUtils.teleportPlayer(plugin, player),
                        ticks);

            }

        } else {

            PlayerUtils.teleportPlayer(plugin, player);

            List<String> teleportActions = plugin.getConfiguration().spawn.actions.teleported;
            actionParser.executeActions(teleportActions, player, 0, new HashMap<>());

        }

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

            WrappedTask task = tasks.remove(uuid);
            if (task != null) task.cancel();
            locations.remove(uuid);

            List<String> cancelActions = plugin.getConfiguration().spawn.actions.teleportCanceled;
            actionParser.executeActions(cancelActions, player, 0, new HashMap<>());

            Logger.debug("Player " + player.getName() + " moved while waiting for spawn teleport, teleport canceled.");

        }
    }
}
