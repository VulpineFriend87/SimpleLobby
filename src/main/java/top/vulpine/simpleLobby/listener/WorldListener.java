package top.vulpine.simpleLobby.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.utils.logger.Logger;

public class WorldListener implements Listener {

    private final SimpleLobby plugin;

    public WorldListener(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLungerLoss(FoodLevelChangeEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (plugin.getConfig().getBoolean("options.disable_lunger_loss")) {
            event.setCancelled(true);
            Logger.debug("Hunger loss prevented for player: " + player.getName());
        }

    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {

        if (plugin.getConfig().getBoolean("options.disable_mob_spawning")
                && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
            Logger.debug("Mob spawn prevented in world: " + event.getLocation().getWorld().getName());
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (plugin.getConfig().getBoolean("options.disable_damage")) {
            event.setCancelled(true);
            Logger.debug("Damage prevented for player: " + player.getName());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (plugin.getConfig().getBoolean("options.disable_block_placing.enabled")) {

            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE
                    && plugin.getConfig().getBoolean("options.disable_block_placing.creative_bypass"))) {
                event.setCancelled(true);
            }

        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (plugin.getConfig().getBoolean("options.disable_block_breaking.enabled")) {

            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE
                    && plugin.getConfig().getBoolean("options.disable_block_breaking.creative_bypass"))) {
                event.setCancelled(true);
            }

        }

    }

}
