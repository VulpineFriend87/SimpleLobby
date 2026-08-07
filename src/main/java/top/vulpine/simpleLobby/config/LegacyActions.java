package top.vulpine.simpleLobby.config;

import top.vulpine.actions.action.Action;
import top.vulpine.actions.action.ActionRegistry;
import top.vulpine.actions.action.impl.SoundAction;
import top.vulpine.simpleLobby.util.SoundKeys;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Everything needed to read a config written for SimpleLobby 1.x.
 *
 * <p>The old one-liner format is understood by the action library as it stands: the
 * type is matched case insensitively, {@code player} and {@code global} are accepted
 * as targets, and the positional order of every action matches. Only sounds differ,
 * and only because they were written as Bukkit enum names.</p>
 */
public final class LegacyActions {

    /** A list entry that is a one-liner rather than a block. */
    private static final Pattern ONE_LINER = Pattern.compile("^\\s*-\\s*[\"']?\\[\\w+]", Pattern.MULTILINE);

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private LegacyActions() {
    }

    /**
     * Teaches the registry to read sounds named the old way.
     *
     * <p>1.x wrote sounds as {@code ENTITY_PLAYER_LEVELUP}. The library refuses those
     * rather than lowercasing them, because an enum name is not a key: underscores are
     * legal inside a key path, so {@code BLOCK_NOTE_BLOCK_PLING} is
     * {@code block.note_block.pling} and lowercasing would produce a valid key that no
     * sound has — silence, with nothing reported.</p>
     *
     * <p>Only the one-liner form is affected, so this stops mattering the moment a
     * config has been rewritten. A sound written as a block is left to the library,
     * which reports an enum name as the mistake it is.</p>
     *
     * @param registry the registry to override the sound entry in
     */
    public static void registerSoundNames(final ActionRegistry registry) {
        registry.register("sound", SoundAction::read, LegacyActions::parseSound);
    }

    private static Action parseSound(final String params, final String raw) {

        // target; sound; volume; pitch
        String[] parts = params.split(";", -1);

        if (parts.length > 1) {

            String key = SoundKeys.resolve(parts[1].trim());

            if (key != null) {
                parts[1] = " " + key;
            }
        }

        return SoundAction.parse(String.join(";", parts), raw);
    }

    /**
     * @param file the config file
     * @return true if it holds at least one action written the old way
     */
    public static boolean isLegacy(final Path file) {

        if (!Files.isRegularFile(file)) {
            return false;
        }

        try {
            return ONE_LINER.matcher(Files.readString(file, StandardCharsets.UTF_8)).find();

        } catch (IOException e) {
            // Unreadable here means unreadable to okaeri in a moment too, which
            // reports it properly. Nothing to migrate as far as this is concerned.
            return false;
        }
    }

    /**
     * Copies the config aside before it is rewritten.
     *
     * @param file the config file
     * @return the copy, or null if it could not be made
     */
    public static Path backup(final Path file) {

        Path copy = file.resolveSibling("config-" + LocalDateTime.now().format(STAMP) + ".yml.bak");

        try {
            Files.copy(file, copy);
            return copy;

        } catch (IOException e) {
            return null;
        }
    }
}
