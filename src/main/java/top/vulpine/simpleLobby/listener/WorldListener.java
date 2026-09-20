package top.vulpine.simpleLobby.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.config.Config;
import top.vulpine.commons.log.LogAction;
import top.vulpine.commons.log.Logger;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Handles various world-related events in the plugin.
 * It prevents hunger loss, mob spawning, player damage, block placing, breaking, interaction,
 * trampling, fire and explosions based on the configuration settings.
 */
public class WorldListener implements Listener {

    private static final long SUMMARY_INTERVAL_NANOS = TimeUnit.MINUTES.toNanos(1);

    /** Blocks that break by being walked over rather than by being hit. */
    private static final Set<Material> TRAMPLEABLE = Set.of(Material.FARMLAND, Material.TURTLE_EGG);

    private final SimpleLobby plugin;

    private final Map<String, SpawnTally> preventedSpawns = new ConcurrentHashMap<>();

    private enum Aktion implements LogAction {
        HUNGER, SPAWNING, DAMAGE, PLACING, BREAKING, INTERACTION, TRAMPLING, FIRE, EXPLOSION
    }

    public WorldListener(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    /** @return the world options as they stand, which a reload replaces wholesale */
    private Config.Options options() {
        return plugin.getConfiguration().options;
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!options().disableHungerLoss.appliesTo(player)) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.HUNGER, "Hunger loss prevented for player: " + player.getName());
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        String world = event.getLocation().getWorld().getName();

        if (!options().disableMobSpawning.appliesIn(world)) {
            return;
        }

        event.setCancelled(true);
        tallyPreventedSpawn(world);
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

        if (!options().disableDamage.appliesTo(player)) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.DAMAGE, "Damage prevented for player: " + player.getName());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (!options().disableBlockPlacing.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.PLACING, "Block place prevented in world: " + event.getPlayer().getWorld().getName());
    }

    /**
     * A bucket does not fire a place event, so the liquid it leaves behind is not covered
     * by {@link #onBlockPlace(BlockPlaceEvent)}, and using it on a plain block is not an
     * interaction {@link #onBlockInteraction(PlayerInteractEvent)} would stop either.
     */
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {

        if (!options().disableBlockPlacing.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.PLACING, "Bucket empty prevented in world: " + event.getPlayer().getWorld().getName());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (!options().disableBlockBreaking.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.BREAKING, "Block break prevented in world: " + event.getPlayer().getWorld().getName());
    }

    /** The other half of {@link #onBucketEmpty(PlayerBucketEmptyEvent)}: taking a liquid away. */
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {

        if (!options().disableBlockBreaking.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.BREAKING, "Bucket fill prevented in world: " + event.getPlayer().getWorld().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockInteraction(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || !block.getType().isInteractable()) {
            return;
        }

        if (!options().disableBlockInteraction.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.INTERACTION, "Block interaction prevented in world: " + block.getWorld().getName());
    }

    /**
     * Stops a player breaking farmland or turtle eggs by walking over them.
     *
     * <p>Stepping on a block is a PHYSICAL interaction rather than a click, so it never
     * reaches {@link #onBlockInteraction(PlayerInteractEvent)}: farmland is not
     * interactable, and the crop on top of it is destroyed as a side effect of the
     * farmland turning back into dirt, so no break event is fired either. Pressure
     * plates and tripwires are PHYSICAL too and are deliberately left alone, since
     * lobby redstone tends to be built on them.</p>
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onTrample(PlayerInteractEvent event) {

        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || !TRAMPLEABLE.contains(block.getType())) {
            return;
        }

        if (!options().disableTrampling.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.TRAMPLING, "Trampling prevented for player: " + event.getPlayer().getName());
    }

    /**
     * The same, for anything that is not a player: mobs trample farmland as readily as
     * players do, and there is no gamemode for them to bypass with.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTrample(EntityInteractEvent event) {

        Block block = event.getBlock();
        if (!TRAMPLEABLE.contains(block.getType())) {
            return;
        }

        if (!options().disableTrampling.appliesIn(block.getWorld().getName())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.TRAMPLING, "Trampling prevented for entity: " + event.getEntityType());
    }

    /**
     * Stops fire being lit at all, whoever or whatever lit it.
     *
     * <p>Flint and steel is a right click on a block that is usually not interactable, so
     * it slips past {@link #onBlockInteraction(PlayerInteractEvent)}; lightning and fire
     * spreading from an existing block have no player behind them at all.</p>
     */
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {

        Player player = event.getPlayer();
        boolean applies = player != null
                ? options().disableFire.appliesTo(player)
                : options().disableFire.appliesIn(event.getBlock().getWorld().getName());

        if (!applies) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.FIRE, "Ignition prevented in world: " + event.getBlock().getWorld().getName());
    }

    /** Stops a block already on fire from burning away. */
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {

        if (!options().disableFire.appliesIn(event.getBlock().getWorld().getName())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.FIRE, "Burning prevented in world: " + event.getBlock().getWorld().getName());
    }

    /** Stops TNT, creepers and the like taking the lobby apart. */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        if (!options().disableExplosions.appliesIn(event.getLocation().getWorld().getName())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.EXPLOSION, "Explosion prevented in world: " + event.getLocation().getWorld().getName());
    }

    /** The same for a block that explodes on its own, such as a bed in the wrong dimension. */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {

        if (!options().disableExplosions.appliesIn(event.getBlock().getWorld().getName())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Aktion.EXPLOSION, "Explosion prevented in world: " + event.getBlock().getWorld().getName());
    }

}
