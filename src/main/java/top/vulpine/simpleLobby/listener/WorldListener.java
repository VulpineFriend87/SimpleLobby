package top.vulpine.simpleLobby.listener;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.commons.log.LogAction;
import top.vulpine.commons.log.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles various world-related events in the plugin.
 * It prevents hunger loss, mob spawning, player damage, block placing, breaking, and interaction
 * based on the configuration settings.
 */
public class WorldListener implements Listener {

    private static final long SUMMARY_INTERVAL_NANOS = TimeUnit.MINUTES.toNanos(1);

    private final SimpleLobby plugin;

    private final Map<String, SpawnTally> preventedSpawns = new ConcurrentHashMap<>();

    private enum Aktion implements LogAction {
        HUNGER, SPAWNING, DAMAGE, PLACING, BREAKING, INTERACTION
    }

    public WorldListener(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLungerLoss(FoodLevelChangeEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean enabled = plugin.getConfiguration().options.disableHungerLoss.enabled;
        boolean whitelistEnabled = plugin.getConfiguration().options.disableHungerLoss.whitelist.enabled;
        List<String> whitelistedWorlds = plugin.getConfiguration().options.disableHungerLoss.whitelist.worlds;
        String world = player.getWorld().getName();
        if (enabled && (!whitelistEnabled || whitelistedWorlds.contains(world))) {
            event.setCancelled(true);
            Logger.debug(Aktion.HUNGER, "Hunger loss prevented for player: " + player.getName());
        }

    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {

        boolean enabled = plugin.getConfiguration().options.disableMobSpawning.enabled;
        boolean whitelistEnabled = plugin.getConfiguration().options.disableMobSpawning.whitelist.enabled;
        List<String> whitelistedWorlds = plugin.getConfiguration().options.disableMobSpawning.whitelist.worlds;
        String world = event.getLocation().getWorld().getName();
        if (enabled && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                && (!whitelistEnabled || whitelistedWorlds.contains(world))) {
            event.setCancelled(true);
            tallyPreventedSpawn(world);
        }

    }

    /**
     * Counts a prevented spawn, reporting a total at most once a minute per world.
     *
     * <p>A line per spawn is unusable: the server attempts natural spawns constantly
     * across every loaded chunk, and on a live test this produced 49,000 lines and
     * 5.4 MB of log in a quarter of an hour — enough to bury whatever the operator
     * turned DEBUG on to find.</p>
     *
     * @param world the world the spawn was prevented in
     */
    private void tallyPreventedSpawn(final String world) {

        SpawnTally tally = preventedSpawns.computeIfAbsent(world, key -> new SpawnTally());
        long now = System.nanoTime();
        long total;

        // Spawns arrive on region threads on Folia, so the tally is not ours alone.
        synchronized (tally) {

            tally.count++;

            if (now - tally.lastReport < SUMMARY_INTERVAL_NANOS) {
                return;
            }

            total = tally.count;
            tally.count = 0;
            tally.lastReport = now;
        }

        Logger.debug(Aktion.SPAWNING, "Prevented " + total + " natural mob spawn(s) in world '"
                + world + "' in the last minute.");
    }

    /** How many spawns have been prevented in one world since the last report. */
    private static final class SpawnTally {

        private long count;
        private long lastReport = System.nanoTime();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean enabled = plugin.getConfiguration().options.disableDamage.enabled;
        boolean whitelistEnabled = plugin.getConfiguration().options.disableDamage.whitelist.enabled;
        List<String> whitelistedWorlds = plugin.getConfiguration().options.disableDamage.whitelist.worlds;
        String world = player.getWorld().getName();
        if (enabled && (!whitelistEnabled || whitelistedWorlds.contains(world))) {
            event.setCancelled(true);
            Logger.debug(Aktion.DAMAGE, "Damage prevented for player: " + player.getName());
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        boolean enabled = plugin.getConfiguration().options.disableBlockPlacing.enabled;
        if (enabled) {
            boolean creativeBypass = plugin.getConfiguration().options.disableBlockPlacing.creativeBypass;
            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE && creativeBypass)) {
                boolean whitelistEnabled = plugin.getConfiguration().options.disableBlockPlacing.whitelist.enabled;
                List<String> whitelistedWorlds = plugin.getConfiguration().options.disableBlockPlacing.whitelist.worlds;
                String world = event.getPlayer().getWorld().getName();
                if (!whitelistEnabled || whitelistedWorlds.contains(world)) {
                    event.setCancelled(true);
                    Logger.debug(Aktion.PLACING, "Block place prevented in world: " + world);
                }
            }
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        boolean enabled = plugin.getConfiguration().options.disableBlockBreaking.enabled;
        if (enabled) {
            boolean creativeBypass = plugin.getConfiguration().options.disableBlockBreaking.creativeBypass;
            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE && creativeBypass)) {
                boolean whitelistEnabled = plugin.getConfiguration().options.disableBlockBreaking.whitelist.enabled;
                List<String> whitelistedWorlds = plugin.getConfiguration().options.disableBlockBreaking.whitelist.worlds;
                String world = event.getPlayer().getWorld().getName();
                if (!whitelistEnabled || whitelistedWorlds.contains(world)) {
                    event.setCancelled(true);
                    Logger.debug(Aktion.BREAKING, "Block break prevented in world: " + world);
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockInteraction(PlayerInteractEvent event) {

        boolean enabled = plugin.getConfiguration().options.disableBlockInteraction.enabled;
        if (enabled) {
            boolean creativeBypass = plugin.getConfiguration().options.disableBlockInteraction.creativeBypass;
            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE && creativeBypass)) {
                boolean whitelistEnabled = plugin.getConfiguration().options.disableBlockInteraction.whitelist.enabled;
                List<String> whitelistedWorlds = plugin.getConfiguration().options.disableBlockInteraction.whitelist.worlds;
                String world = event.getPlayer().getWorld().getName();
                if (!whitelistEnabled || whitelistedWorlds.contains(world)) {

                    if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }

                    Block block = event.getClickedBlock();
                    if (block == null) {
                        return;
                    }

                    if (!block.getType().isInteractable()) {
                        return;
                    }

                    event.setCancelled(true);
                    Logger.debug(Aktion.INTERACTION, "Block interaction prevented in world: " + world);
                }
            }
        }

    }

}
