package top.vulpine.simpleLobby.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Folia implementation of {@link SchedulerAdapter} using reflection.
 */
public class FoliaScheduler implements SchedulerAdapter {

    private final Plugin plugin;

    private final Method entityGetScheduler;
    private final Method entitySchedulerRun;
    private final Method entitySchedulerRunDelayed;
    private final Method bukkitGetGlobalScheduler;
    private final Method globalSchedulerExecute;
    private final Method globalSchedulerRunDelayed;
    private final Method asyncSchedulerRunNow;
    private final Method asyncSchedulerRunDelayed;
    private final Method asyncSchedulerRunAtFixedRate;
    private final Method scheduledTaskCancel;
    private final Method playerTeleportAsync;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
        try {
            this.entityGetScheduler = Entity.class.getMethod("getScheduler");

            Class<?> entitySchedulerCls = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            this.entitySchedulerRun = entitySchedulerCls.getMethod("run", Plugin.class, Consumer.class, Runnable.class);
            this.entitySchedulerRunDelayed = entitySchedulerCls.getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class);

            this.bukkitGetGlobalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
            Class<?> globalSchedulerCls = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            this.globalSchedulerExecute = globalSchedulerCls.getMethod("execute", Plugin.class, Runnable.class);
            this.globalSchedulerRunDelayed = globalSchedulerCls.getMethod("runDelayed", Plugin.class, Consumer.class, long.class);

            Method getAsyncScheduler = Bukkit.class.getMethod("getServer");
            Object server = getAsyncScheduler.invoke(null);
            Method serverAsync = server.getClass().getMethod("getAsyncScheduler");
            Class<?> asyncSchedulerCls = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            this.asyncSchedulerRunNow = asyncSchedulerCls.getMethod("runNow", Plugin.class, Consumer.class);
            this.asyncSchedulerRunDelayed = asyncSchedulerCls.getMethod("runDelayed", Plugin.class, Consumer.class, long.class, TimeUnit.class);
            this.asyncSchedulerRunAtFixedRate = asyncSchedulerCls.getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class);

            Class<?> scheduledTaskCls = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
            this.scheduledTaskCancel = scheduledTaskCls.getMethod("cancel");

            this.playerTeleportAsync = Player.class.getMethod("teleportAsync", Location.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to bind Folia scheduler reflection", e);
        }
    }

    @Override
    public void runGlobal(Runnable task) {
        try {
            Object globalSched = bukkitGetGlobalScheduler.invoke(null);
            globalSchedulerExecute.invoke(globalSched, plugin, task);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#runGlobal failed: " + e.getMessage());
        }
    }

    @Override
    public void runEntity(Entity entity, Runnable task) {
        try {
            Object entSched = entityGetScheduler.invoke(entity);
            Consumer<Object> consumer = ignored -> task.run();
            entitySchedulerRun.invoke(entSched, plugin, consumer, null);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#runEntity failed: " + e.getMessage());
        }
    }

    @Override
    public Cancellable runEntityLater(Entity entity, Runnable task, long ticks) {
        try {
            Object entSched = entityGetScheduler.invoke(entity);
            Consumer<Object> consumer = ignored -> task.run();
            Object scheduledTask = entitySchedulerRunDelayed.invoke(
                    entSched, plugin, consumer, null, Math.max(1L, ticks));
            return cancellableOf(scheduledTask);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#runEntityLater failed: " + e.getMessage());
            return Cancellable.NOOP;
        }
    }

    @Override
    public Cancellable runGlobalLater(Runnable task, long ticks) {
        try {
            Object globalSched = bukkitGetGlobalScheduler.invoke(null);
            Consumer<Object> consumer = ignored -> task.run();
            Object scheduledTask = globalSchedulerRunDelayed.invoke(
                    globalSched, plugin, consumer, Math.max(1L, ticks));
            return cancellableOf(scheduledTask);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#runGlobalLater failed: " + e.getMessage());
            return Cancellable.NOOP;
        }
    }

    @Override
    public Cancellable runAsync(Runnable task) {
        try {
            Object server = Bukkit.class.getMethod("getServer").invoke(null);
            Object asyncSched = server.getClass().getMethod("getAsyncScheduler").invoke(server);
            Consumer<Object> consumer = ignored -> task.run();
            Object scheduledTask = asyncSchedulerRunNow.invoke(asyncSched, plugin, consumer);
            return cancellableOf(scheduledTask);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#runAsync failed: " + e.getMessage());
            return Cancellable.NOOP;
        }
    }

    @Override
    public Cancellable runAsyncLater(Runnable task, long delay, TimeUnit unit) {
        try {
            Object server = Bukkit.class.getMethod("getServer").invoke(null);
            Object asyncSched = server.getClass().getMethod("getAsyncScheduler").invoke(server);
            Consumer<Object> consumer = ignored -> task.run();
            Object scheduledTask = asyncSchedulerRunDelayed.invoke(asyncSched, plugin, consumer, Math.max(1L, delay), unit);
            return cancellableOf(scheduledTask);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#runAsyncLater failed: " + e.getMessage());
            return Cancellable.NOOP;
        }
    }

    @Override
    public Cancellable runAsyncRepeating(Runnable task, long initialDelay, long period, TimeUnit unit) {
        try {
            Object server = Bukkit.class.getMethod("getServer").invoke(null);
            Object asyncSched = server.getClass().getMethod("getAsyncScheduler").invoke(server);
            Consumer<Object> consumer = ignored -> task.run();
            Object scheduledTask = asyncSchedulerRunAtFixedRate.invoke(asyncSched, plugin, consumer, Math.max(1L, initialDelay), Math.max(1L, period), unit);
            return cancellableOf(scheduledTask);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#runAsyncRepeating failed: " + e.getMessage());
            return Cancellable.NOOP;
        }
    }

    @Override
    public void teleport(Player player, Location location) {
        try {
            playerTeleportAsync.invoke(player, location);
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("FoliaScheduler#teleport failed: " + e.getMessage());
        }
    }

    private Cancellable cancellableOf(Object scheduledTask) {
        if (scheduledTask == null) return Cancellable.NOOP;
        return () -> {
            try {
                scheduledTaskCancel.invoke(scheduledTask);
            } catch (ReflectiveOperationException ignored) {
            }
        };
    }
}
