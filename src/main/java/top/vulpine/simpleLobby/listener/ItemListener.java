package top.vulpine.simpleLobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.InventoryView;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.config.Config;
import top.vulpine.commons.log.LogAction;
import top.vulpine.commons.log.Logger;

/**
 * Keeps the kit a player joins with where it was put.
 *
 * <p>Covers dropping, picking up, and moving items around an inventory, which between
 * them are every way an item leaves the slot it was given in.</p>
 */
public class ItemListener implements Listener {

    private final SimpleLobby plugin;

    private enum Action implements LogAction {
        DROPPING, PICKUP, INVENTORY
    }

    public ItemListener(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    /** @return the world options as they stand, which a reload replaces wholesale */
    private Config.Options options() {
        return plugin.getConfiguration().options;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {

        if (!options().disableItemDropping.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.DROPPING, "Item drop prevented for player: " + event.getPlayer().getName());
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!options().disableItemPickup.appliesTo(player)) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.PICKUP, "Item pickup prevented for player: " + player.getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player) || !isOwnInventory(event.getView())) {
            return;
        }

        if (!options().disableInventoryInteraction.appliesTo(player)) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.INVENTORY, "Inventory click prevented for player: " + player.getName());
    }

    /** Dragging a stack across slots is its own event, and would otherwise slip through. */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!(event.getWhoClicked() instanceof Player player) || !isOwnInventory(event.getView())) {
            return;
        }

        if (!options().disableInventoryInteraction.appliesTo(player)) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.INVENTORY, "Inventory drag prevented for player: " + player.getName());
    }

    /** So is swapping the two hands, which needs no inventory open at all. */
    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {

        if (!options().disableInventoryInteraction.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.INVENTORY, "Hand swap prevented for player: " + event.getPlayer().getName());
    }

    /**
     * Whether the player has nothing open but their own inventory.
     *
     * <p>With no container open, the top half of the view is the 2x2 crafting grid, and
     * that is the one case this option is meant for. Anything else is either a block the
     * disable_block_interaction option already decides on, or a menu another plugin
     * opened and is listening to itself, and cancelling those clicks would break it.</p>
     *
     * @param view the view the click happened in
     * @return true if the click was in the player's own inventory
     */
    private static boolean isOwnInventory(InventoryView view) {
        return view.getTopInventory().getType() == InventoryType.CRAFTING;
    }

}
