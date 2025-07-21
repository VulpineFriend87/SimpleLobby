package top.vulpine.simpleLobby.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.utils.Colorize;
import top.vulpine.simpleLobby.utils.PermissionChecker;
import top.vulpine.simpleLobby.utils.PlayerUtils;
import top.vulpine.simpleLobby.utils.logger.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnCommand implements CommandExecutor, TabCompleter, Listener {

    private final SimpleLobby plugin;
    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();
    private final Map<UUID, Location> locations = new ConcurrentHashMap<>();

    public SpawnCommand(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!PermissionChecker.hasPermission(sender, "simplelobby.spawn")) {
            sender.sendMessage(Colorize.color(plugin.getConfig().getString("messages.no_permission")));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Colorize.color(plugin.getConfig().getString("messages.only_players")));
            return true;
        }

        if (!plugin.getConfig().getBoolean("spawn.command.enabled")) {
            return true;
        }


        FileConfiguration config = plugin.getConfig();
        boolean delayEnabled = config.getBoolean("spawn.command.delay.enabled");
        if (delayEnabled) {

            int seconds = config.getInt("spawn.command.delay.time");
            boolean requireStill = config.getBoolean("spawn.command.delay.require_player_still");
            String key = requireStill ? "messages.spawn.delay_started_still" : "messages.spawn.delay_started";
            player.sendMessage(Colorize.color(config.getString(key).replace("%time%", String.valueOf(seconds))));

            if (requireStill) {

                UUID uuid = player.getUniqueId();
                locations.put(uuid, player.getLocation().clone());

                BukkitTask task = new BukkitRunnable() {

                    @Override
                    public void run() {
                        PlayerUtils.teleportPlayer(plugin, player);
                        tasks.remove(uuid);
                        locations.remove(uuid);
                    }

                }.runTaskLater(plugin, seconds * 20L);

                tasks.put(uuid, task);

            } else {

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PlayerUtils.teleportPlayer(plugin, player);
                    }
                }.runTaskLater(plugin, seconds * 20L);

            }

        } else {

            PlayerUtils.teleportPlayer(plugin, player);

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return List.of();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!plugin.getConfig().getBoolean("spawn.command.delay.require_player_still")) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!locations.containsKey(uuid)) return;
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {

            BukkitTask task = tasks.remove(uuid);
            if (task != null) task.cancel();
            locations.remove(uuid);
            player.sendMessage(Colorize.color(plugin.getConfig().getString("messages.spawn.teleport_canceled")));

            Logger.debug("Player " + player.getName() + " moved while waiting for spawn teleport, teleport canceled.");

        }
    }

    public SimpleLobby getPlugin() {
        return plugin;
    }
}
