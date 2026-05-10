package top.vulpine.simpleLobby.scheduler;

/**
 * Handle to a scheduled task that can be cancelled.
 * Returned by {@link SchedulerAdapter} delayed-task methods.
 */
@FunctionalInterface
public interface Cancellable {

    Cancellable NOOP = () -> {};

    void cancel();
}
