package top.vulpine.simpleLobby;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import top.vulpine.simpleLobby.utils.Colorize;
import top.vulpine.simpleLobby.utils.PermissionChecker;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class UpdateNotifier implements Listener {

    private final SimpleLobby plugin;
    private final String projectSlug;
    private final String message;
    private volatile String cachedLatestVersion;

    public UpdateNotifier(SimpleLobby plugin, String projectSlug, String message) {
        this.plugin = plugin;
        this.projectSlug = projectSlug;
        this.message = message;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getScheduler().runAsync(this::updateCache);
        plugin.getScheduler().runAsyncRepeating(this::updateCache, 30, 30, TimeUnit.MINUTES);
    }

    private void updateCache() {
        try {
            URL url = new URL("https://api.modrinth.com/v2/project/" + projectSlug + "/version");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("User-Agent", "SimpleLobby/" + plugin.getDescription().getVersion());
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            if (con.getResponseCode() != 200) return;

            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(new InputStreamReader(con.getInputStream())).getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject versionObj = jsonArray.get(i).getAsJsonObject();
                String type = versionObj.get("version_type").getAsString(); // release, beta, alpha

                if (type.equalsIgnoreCase("release")) {
                    cachedLatestVersion = versionObj.get("version_number").getAsString();
                    break;
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Modrinth update check failed: " + e.getMessage());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String latest = cachedLatestVersion;
        if (latest == null) return;

        Player player = event.getPlayer();
        if (!PermissionChecker.hasPermission(player, "notify")) return;

        String current = plugin.getDescription().getVersion();

        if (current.equalsIgnoreCase(latest)) return;

        player.sendMessage(Colorize.color(
                message.replace("%current%", current).replace("%new%", latest)
        ));
    }
}