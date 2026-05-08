package top.vulpine.simpleLobby.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitSchedulerAdapter implements SchedulerAdapter {

    private final Plugin plugin;

    public BukkitSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runGlobal(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    @Override
    public void runEntity(Entity entity, Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    @Override
    public Cancellable runEntityLater(Entity entity, Runnable task, long ticks) {
        BukkitTask bt = Bukkit.getScheduler().runTaskLater(plugin, task, Math.max(1L, ticks));
        return bt::cancel;
    }

    @Override
    public Cancellable runGlobalLater(Runnable task, long ticks) {
        BukkitTask bt = Bukkit.getScheduler().runTaskLater(plugin, task, Math.max(1L, ticks));
        return bt::cancel;
    }

    @Override
    public void teleport(Player player, Location location) {
        if (Bukkit.isPrimaryThread()) {
            player.teleport(location);
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> player.teleport(location));
        }
    }
}
