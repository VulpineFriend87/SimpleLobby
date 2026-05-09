package top.vulpine.simpleLobby;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.vulpine.simpleLobby.utils.Colorize;
import top.vulpine.simpleLobby.utils.PermissionChecker;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateNotifier implements Listener {

    private final JavaPlugin plugin;
    private final String repo;
    private final String message;
    private volatile String cachedLatestVersion;

    public UpdateNotifier(JavaPlugin plugin, String repo, String message) {
        this.plugin = plugin;
        this.repo = repo;
        this.message = message;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::updateCache);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateCache, 20L * 60L * 30L, 20L * 60L * 30L);
    }

    private void updateCache() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.github.com/repos/" + repo + "/releases/latest").openConnection();
            con.setRequestProperty("Accept", "application/vnd.github.v3+json");
            con.setRequestProperty("User-Agent", "SimpleLobby-UpdateChecker");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            if (con.getResponseCode() != 200) return;

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(new InputStreamReader(con.getInputStream())).getAsJsonObject();
            cachedLatestVersion = json.get("tag_name").getAsString();
        } catch (Exception e) {
            plugin.getLogger().warning("GitHub update check failed: " + e.getMessage());
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