package top.vulpine.simpleLobby.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Folia implementation of {@link SchedulerAdapter}, using reflection so the plugin
 * still compiles against and runs on plain Spigot.
 *
 * This class is only loaded when Folia is detected at startup, so the missing
 * Paper/Folia classes never trigger a {@link NoClassDefFoundError} on Spigot.
 */
public class FoliaScheduler implements SchedulerAdapter {

    private final Plugin plugin;

    private final Method entityGetScheduler;
    private final Method entitySchedulerRun;
    private final Method entitySchedulerRunDelayed;
    private final Method bukkitGetGlobalScheduler;
    private final Method globalSchedulerExecute;
    private final Method globalSchedulerRunDelayed;
    private final Method scheduledTaskCancel;
    private final Method playerTeleportAsync;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
        try {
            this.entityGetScheduler = Entity.class.getMethod("getScheduler");

            Class<?> entitySchedulerCls = Class.forName(
                    "io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            this.entitySchedulerRun = entitySchedulerCls.getMethod(
                    "run", Plugin.class, Consumer.class, Runnable.class);
            this.entitySchedulerRunDelayed = entitySchedulerCls.getMethod(
                    "runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class);

            this.bukkitGetGlobalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
            Class<?> globalSchedulerCls = Class.forName(
                    "io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            this.globalSchedulerExecute = globalSchedulerCls.getMethod(
                    "execute", Plugin.class, Runnable.class);
            this.globalSchedulerRunDelayed = globalSchedulerCls.getMethod(
                    "runDelayed", Plugin.class, Consumer.class, long.class);

            Class<?> scheduledTaskCls = Class.forName(
                    "io.papermc.paper.threadedregions.scheduler.ScheduledTask");
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
