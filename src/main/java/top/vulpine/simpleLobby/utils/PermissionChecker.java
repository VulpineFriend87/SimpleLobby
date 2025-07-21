package top.vulpine.simpleLobby.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * A utility class that checks if a CommandSender has the required permissions
 * for a specific base permission and optional sub-permissions.
 */
public class PermissionChecker {

    private static final String root = "simplelobby";

    /**
     * Checks if the given CommandSender has the specified permission.
     *
     * @param sender The CommandSender to check permissions for.
     * @param base   The base permission to check.
     * @param parts  Optional sub-permissions to check.
     * @return true if the sender has the required permission, false otherwise.
     */
    public static boolean hasPermission(CommandSender sender, String base, String... parts) {

        if (sender.hasPermission(root + ".admin") || sender.hasPermission(root + ".*")) {

            return true;

        }

        String basePermission = root + "." + base.toLowerCase();

        if (parts == null || parts.length == 0) {

            if (sender.hasPermission(basePermission) || sender.hasPermission(basePermission + ".*")) {
                return true;
            }

            String permissionPrefix = basePermission + ".";
            for (PermissionAttachmentInfo pai : sender.getEffectivePermissions()) {

                if (pai.getValue() && pai.getPermission().startsWith(permissionPrefix)) {
                    return true;
                }

            }

        } else {

            if (sender.hasPermission(basePermission) || sender.hasPermission(basePermission + ".*")) {
                return true;
            }

            for (String part : parts) {

                String subPermission = basePermission + "." + part.toLowerCase();

                if (sender.hasPermission(subPermission) || sender.hasPermission(subPermission + ".*")) {
                    return true;
                }

            }

        }

        return false;

    }

}
