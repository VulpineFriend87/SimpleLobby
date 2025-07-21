package top.vulpine.simpleLobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.utils.PlayerUtils;
import top.vulpine.simpleLobby.utils.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {

    private final SimpleLobby plugin;

    public PlayerListener(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (plugin.getConfig().getBoolean("options.clear_inventory_on_join")) {
            event.getPlayer().getInventory().clear();
            Logger.debug("Inventory cleared for player: " + event.getPlayer().getName());
        }

        if (plugin.getConfig().getBoolean("options.clear_effects_on_join")) {
            event.getPlayer().getActivePotionEffects().forEach(effect ->
                    event.getPlayer().removePotionEffect(effect.getType())
            );
            Logger.debug("Potion effects cleared for player: " + event.getPlayer().getName());
        }

        if (plugin.getConfig().getBoolean("actions.join.enabled")) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", event.getPlayer().getName());

            plugin.getActionParser().executeActions(plugin.getConfig().getStringList("actions.join.actions"),
                    event.getPlayer(),
                    0,
                    placeholders
            );

        }

        if (plugin.getConfig().getBoolean("spawn.tp_on_join")) {

            String world = plugin.getConfig().getString("spawn.location.world");
            World worldObj = Bukkit.getWorld(world);
            if (worldObj == null) {
                Logger.warn("World '" + world + "' not found. Cannot teleport player to spawn.");
            } else {
                PlayerUtils.teleportPlayer(plugin, event.getPlayer());
            }

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (plugin.getConfig().getBoolean("actions.quit.enabled")) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", event.getPlayer().getName());

            plugin.getActionParser().executeActions(plugin.getConfig().getStringList("actions.quit.actions"),
                    event.getPlayer(),
                    0,
                    placeholders
            );

        }

    }

}
