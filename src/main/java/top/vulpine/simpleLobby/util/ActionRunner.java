package top.vulpine.simpleLobby.util;

import org.bukkit.entity.Player;
import top.vulpine.actions.action.Action;
import top.vulpine.actions.action.ActionContext;
import top.vulpine.actions.action.ActionExecutor;
import top.vulpine.simpleLobby.SimpleLobby;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Runs configured action lists and keeps track of the ones still going.
 *
 * <p>A list holding a delay outlives the event that started it, so it has to be
 * cancellable when the player leaves. Several lists can be in flight for the same
 * player at once — a join sequence and a spawn countdown, say — so they are tracked
 * together rather than replacing each other.</p>
 */
public class ActionRunner {

    private final SimpleLobby plugin;

    private final Map<UUID, Queue<ActionExecutor>> running = new ConcurrentHashMap<>();

    public ActionRunner(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs a list for a player.
     *
     * @param player the player the run belongs to
     * @param actions the list; null or empty does nothing
     */
    public void run(Player player, List<Action> actions) {
        run(player, actions, Map.of());
    }

    /**
     * Runs a list for a player with extra placeholder values.
     *
     * @param player the player the run belongs to
     * @param actions the list; null or empty does nothing
     * @param values placeholder values, keyed without percent signs
     */
    public void run(Player player, List<Action> actions, Map<String, String> values) {

        if (player == null || actions == null || actions.isEmpty()) {
            return;
        }

        ActionContext.Builder builder = ActionContext.builder(plugin.getScheduler())
                .player(player)
                .sequences(plugin.getSequences())
                .value("player", player.getName());

        values.forEach(builder::value);

        ActionExecutor executor = ActionExecutor.run(actions, builder.build());

        // A list with no delay in it has already finished by the time run() returns,
        // and there is nothing left to cancel.
        if (executor.finished()) {
            return;
        }

        Queue<ActionExecutor> queue = running.computeIfAbsent(player.getUniqueId(),
                uuid -> new ConcurrentLinkedQueue<>());

        queue.removeIf(ActionExecutor::finished);
        queue.add(executor);
    }

    /**
     * Abandons everything still pending for a player.
     *
     * @param player the player
     */
    public void cancel(Player player) {

        Queue<ActionExecutor> queue = running.remove(player.getUniqueId());

        if (queue != null) {
            queue.forEach(ActionExecutor::cancel);
        }
    }

    /**
     * Abandons everything still pending, for a shutdown or a reload.
     */
    public void cancelAll() {
        running.values().forEach(queue -> queue.forEach(ActionExecutor::cancel));
        running.clear();
    }
}
