package top.vulpine.simpleLobby.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Abstraction over the Bukkit and Folia schedulers.
 * Implementations route work to the correct thread for the running server flavor.
 */
public interface SchedulerAdapter {

    /** Run on the global region (Folia) or the main thread (Bukkit). */
    void runGlobal(Runnable task);

    /** Run on the entity's region (Folia) or the main thread (Bukkit). */
    void runEntity(Entity entity, Runnable task);

    /** Schedule a task on the entity's region after the given tick delay. */
    Cancellable runEntityLater(Entity entity, Runnable task, long ticks);

    /** Schedule a task on the global region after the given tick delay. */
    Cancellable runGlobalLater(Runnable task, long ticks);

    /** Runs a task asynchronously. */
    Cancellable runAsync(Runnable task);

    /** Runs a delayed asynchronous task. */
    Cancellable runAsyncLater(Runnable task, long delay, TimeUnit unit);

    /** Runs a repeating asynchronous task. */
    Cancellable runAsyncRepeating(Runnable task, long initialDelay, long period, TimeUnit unit);

    /** Teleport a player in a way that is safe on both Bukkit and Folia. */
    void teleport(Player player, Location location);
}
