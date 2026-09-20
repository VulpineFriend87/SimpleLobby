package top.vulpine.simpleLobby.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import top.vulpine.simpleLobby.SimpleLobby;
import top.vulpine.simpleLobby.config.Config;
import top.vulpine.commons.log.LogAction;
import top.vulpine.commons.log.Logger;

/**
 * Keeps players off the entities a lobby is decorated with.
 *
 * <p>The block options do not reach any of this: mobs, armor stands, item frames and
 * paintings are entities, so hitting one is not a block break and right-clicking one is
 * not a block interaction. Damage to players themselves stays with the disable_damage
 * option in {@link WorldListener}.</p>
 */
public class EntityListener implements Listener {

    private final SimpleLobby plugin;

    private enum Action implements LogAction {
        DAMAGE, INTERACTION
    }

    public EntityListener(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    /** @return the world options as they stand, which a reload replaces wholesale */
    private Config.Options options() {
        return plugin.getConfiguration().options;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        // A player hurting a player is the disable_damage option's business, not this one's.
        if (event.getEntity() instanceof Player) {
            return;
        }

        if (isNpc(event.getEntity())) {
            return;
        }

        Player attacker = attacker(event.getDamager());
        if (attacker == null) {
            return;
        }

        if (!options().disableEntityInteraction.appliesTo(attacker)) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.DAMAGE, "Damage to " + event.getEntityType() + " prevented for player: "
                + attacker.getName());
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {

        if (isNpc(event.getRightClicked())) {
            return;
        }

        if (!options().disableEntityInteraction.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.INTERACTION, "Interaction with " + event.getRightClicked().getType()
                + " prevented for player: " + event.getPlayer().getName());
    }

    /**
     * Stops armor stands being dressed and undressed.
     *
     * <p>Handled separately although the event extends {@link PlayerInteractEntityEvent},
     * because it declares a handler list of its own and so never reaches
     * {@link #onEntityInteract(PlayerInteractEntityEvent)}.</p>
     */
    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {

        if (!options().disableEntityInteraction.appliesTo(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.INTERACTION, "Armor stand manipulation prevented for player: "
                + event.getPlayer().getName());
    }

    /** Stops paintings and item frames being knocked off the wall. */
    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {

        Player remover = attacker(event.getRemover());
        if (remover == null) {
            return;
        }

        if (!options().disableEntityInteraction.appliesTo(remover)) {
            return;
        }

        event.setCancelled(true);
        Logger.debug(Action.DAMAGE, "Break of " + event.getEntity().getType() + " prevented for player: "
                + remover.getName());
    }

    /**
     * Whether an entity belongs to an NPC plugin, which marks its own with this metadata.
     *
     * <p>Server selectors are usually NPCs standing in the lobby, and the plugin behind
     * them reads the same click events this class cancels. Protecting them from a punch
     * is not worth stopping them answering one.</p>
     *
     * @param entity the entity that was clicked or hit
     * @return true if it should be left to whoever placed it
     */
    private static boolean isNpc(Entity entity) {
        return entity.hasMetadata("NPC");
    }

    /**
     * @param damager whatever dealt the blow
     * @return the player behind it, whether they swung it or threw it, or null if there is none
     */
    private static Player attacker(Entity damager) {

        if (damager instanceof Player player) {
            return player;
        }

        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            return shooter;
        }

        return null;
    }

}
