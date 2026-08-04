package top.vulpine.simpleLobby.command.exception;

import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import top.vulpine.commons.text.Colorize;
import top.vulpine.simpleLobby.SimpleLobby;

/**
 * Replaces the messages Lamp sends by default with the ones defined in the
 * plugin configuration. Anything not overridden here falls back to Lamp's
 * default Bukkit messages.
 */
public class ExceptionHandler extends BukkitExceptionHandler {

    private final SimpleLobby plugin;

    public ExceptionHandler(SimpleLobby plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSenderNotPlayer(SenderNotPlayerException e, BukkitCommandActor actor) {
        send(actor, plugin.getConfiguration().messages.onlyPlayers);
    }

    private void send(BukkitCommandActor actor, String message) {
        actor.sender().sendMessage(Colorize.color(message));
    }

}
