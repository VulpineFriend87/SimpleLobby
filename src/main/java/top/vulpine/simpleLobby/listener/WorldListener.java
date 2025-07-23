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

import java.util.List;

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

        boolean globalEnabled = plugin.getConfig().getBoolean("options.disable_hunger_loss.enabled");
        boolean whitelistEnabled = plugin.getConfig().getBoolean("options.disable_hunger_loss.whitelist.enabled");
        List<String> worlds = plugin.getConfig().getStringList("options.disable_hunger_loss.whitelist.worlds");
        String world = player.getWorld().getName();
        if (globalEnabled && (!whitelistEnabled || worlds.contains(world))) {
            event.setCancelled(true);
            Logger.debug("Hunger loss prevented for player: " + player.getName());
        }

    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {

        boolean globalSpawn = plugin.getConfig().getBoolean("options.disable_mob_spawning.enabled");
        boolean spawnWhitelist = plugin.getConfig().getBoolean("options.disable_mob_spawning.whitelist.enabled");
        List<String> spawnWorlds = plugin.getConfig().getStringList("options.disable_mob_spawning.whitelist.worlds");
        String spawnWorld = event.getLocation().getWorld().getName();
        if (globalSpawn && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                && (!spawnWhitelist || spawnWorlds.contains(spawnWorld))) {
            event.setCancelled(true);
            Logger.debug("Mob spawn prevented in world: " + spawnWorld);
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean antiDamageEnabled = plugin.getConfig().getBoolean("options.disable_damage.enabled");
        boolean whitelistEnabled = plugin.getConfig().getBoolean("options.disable_damage.whitelist.enabled");
        List<String> whitelistedWorlds = plugin.getConfig().getStringList("options.disable_damage.whitelist.worlds");
        String dmgWorld = player.getWorld().getName();
        if (antiDamageEnabled && (!whitelistEnabled || whitelistedWorlds.contains(dmgWorld))) {
            event.setCancelled(true);
            Logger.debug("Damage prevented for player: " + player.getName());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        boolean placeEnabled = plugin.getConfig().getBoolean("options.disable_block_placing.enabled");
        if (placeEnabled) {
            boolean creativeBypass = plugin.getConfig().getBoolean("options.disable_block_placing.creative_bypass");
            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE && creativeBypass)) {
                boolean placeWhite = plugin.getConfig().getBoolean("options.disable_block_placing.whitelist.enabled");
                List<String> placeWorlds = plugin.getConfig().getStringList("options.disable_block_placing.whitelist.worlds");
                String pw = event.getPlayer().getWorld().getName();
                if (!placeWhite || placeWorlds.contains(pw)) {
                    event.setCancelled(true);
                    Logger.debug("Block place prevented in world: " + pw);
                }
            }
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        boolean breakEnabled = plugin.getConfig().getBoolean("options.disable_block_breaking.enabled");
        if (breakEnabled) {
            boolean creativeBypass = plugin.getConfig().getBoolean("options.disable_block_breaking.creative_bypass");
            if (!(event.getPlayer().getGameMode() == GameMode.CREATIVE && creativeBypass)) {
                boolean breakWhite = plugin.getConfig().getBoolean("options.disable_block_breaking.whitelist.enabled");
                List<String> breakWorlds = plugin.getConfig().getStringList("options.disable_block_breaking.whitelist.worlds");
                String bw = event.getPlayer().getWorld().getName();
                if (!breakWhite || breakWorlds.contains(bw)) {
                    event.setCancelled(true);
                    Logger.debug("Block break prevented in world: " + bw);
                }
            }
        }

    }

}
