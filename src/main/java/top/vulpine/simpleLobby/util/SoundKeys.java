package top.vulpine.simpleLobby.util;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import top.vulpine.simpleLobby.util.logger.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Resolves a configured sound to the namespaced key the client expects.
 *
 * <p>Configs written against older versions hold Bukkit {@code Sound} enum names
 * ({@code ENTITY_PLAYER_LEVELUP}). Those cannot be resolved with
 * {@code Sound.valueOf} any more: in 1.21.3 {@code org.bukkit.Sound} changed from a
 * class to an interface, and a jar compiled while it was a class carries a method
 * reference the JVM refuses to link against an interface — an
 * {@link IncompatibleClassChangeError}, which is an {@link Error} and so escapes the
 * {@code catch (Exception)} around action execution, taking the rest of the action
 * list with it.</p>
 *
 * <h2>Why the mapping is built at runtime</h2>
 * <p>Name to key is ambiguous — {@code BLOCK_NOTE_BLOCK_PLING} could be
 * {@code block.note.block.pling} or {@code block.note_block.pling}, and 860 of the
 * 1611 vanilla sounds take the second form. But the <em>reverse</em> is exact: an
 * enum name is only ever the key uppercased with {@code .} replaced by {@code _}.
 * So the index is built by walking the server's own sound registry and deriving the
 * old name from each key, rather than shipping a copy of the table.</p>
 *
 * <p>That keeps the jar free of a sound list that would go stale every release, and
 * automatically covers sounds added after this version.</p>
 *
 * <h2>Why this links on every version</h2>
 * <p>Nothing here mentions {@code org.bukkit.Sound}. {@link Registry} and
 * {@link Keyed} are interfaces on both 1.18 and current, and {@code Registry.SOUNDS}
 * is a static field, so the call sites compile to {@code invokeinterface} against
 * types whose shape has not changed.</p>
 */
public final class SoundKeys {

    private static final String VANILLA = NamespacedKey.MINECRAFT;

    private static Map<String, String> keysByName;

    private SoundKeys() {
    }

    /**
     * Resolves a configured sound name to a namespaced key.
     *
     * <p>Values already written as keys ({@code entity.player.levelup} or
     * {@code minecraft:entity.player.levelup}) pass through untouched, so resource
     * pack sounds work too.</p>
     *
     * @param configured the value from config
     * @return the key, or null if the name is not recognised
     */
    public static String resolve(final String configured) {

        if (configured == null || configured.isBlank()) {
            return null;
        }

        String value = configured.trim();

        if (value.indexOf('.') >= 0 || value.indexOf(':') >= 0) {
            return value.toLowerCase(Locale.ROOT);
        }

        return index().get(value.toUpperCase(Locale.ROOT));
    }

    private static synchronized Map<String, String> index() {

        if (keysByName != null) {
            return keysByName;
        }

        Map<String, String> map = new HashMap<>(2048);

        try {

            // Typed as Keyed, not Sound, so the loop never references the type that
            // changed shape in 1.21.3.
            for (Keyed keyed : Registry.SOUNDS) {

                NamespacedKey key = keyed.getKey();

                // Only vanilla sounds ever had enum names, and restricting to them
                // stops a datapack sound from shadowing one.
                if (!VANILLA.equals(key.getNamespace())) {
                    continue;
                }

                map.put(key.getKey().toUpperCase(Locale.ROOT).replace('.', '_'), key.toString());
            }

        } catch (Throwable t) {
            // Throwable rather than Exception on purpose: the bug this class exists
            // to fix was an Error, and a repeat should degrade to "no sound" instead
            // of killing the rest of the action list.
            Logger.warn("Could not read the sound registry (" + t + "); "
                    + "sounds configured by name will not play. Use keys like "
                    + "'entity.player.levelup' instead.");
        }

        keysByName = map;

        Logger.debug("Indexed " + map.size() + " sounds from the server registry.");

        return keysByName;
    }
}
