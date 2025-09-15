package com.minekarta.advancedcorehub.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerInfoManager implements PluginMessageListener {

    private final AdvancedCoreHub plugin;
    private final Map<String, Integer> serverPlayerCounts = new ConcurrentHashMap<>();
    private List<String> serversToQuery;
    private BukkitTask updateTask;

    public ServerInfoManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        loadConfig();
        startUpdateTask();
    }

    public void loadConfig() {
        this.serversToQuery = plugin.getConfig().getStringList("server-selector.servers");
    }

    public void startUpdateTask() {
        // Cancel previous task if reloading
        if (updateTask != null) {
            updateTask.cancel();
        }
        // Run task every 5 seconds
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::requestPlayerCounts, 0L, 100L);
    }

    public void requestPlayerCounts() {
        if (serversToQuery == null || serversToQuery.isEmpty() || Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }
        // Get the first online player to send the message
        Player player = Bukkit.getOnlinePlayers().iterator().next();

        for (String serverName : serversToQuery) {
            com.google.common.io.ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerCount");
            out.writeUTF(serverName);
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals("PlayerCount")) {
            String serverName = in.readUTF();
            int playerCount = in.readInt();
            serverPlayerCounts.put(serverName, playerCount);
        }
    }

    public int getPlayerCount(String serverName) {
        return serverPlayerCounts.getOrDefault(serverName, -1);
    }

    public List<String> getServersToQuery() {
        return Collections.unmodifiableList(serversToQuery);
    }
}
