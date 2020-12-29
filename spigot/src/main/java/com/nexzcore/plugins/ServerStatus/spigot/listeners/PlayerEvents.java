package com.nexzcore.plugins.ServerStatus.spigot.listeners;

import java.util.ArrayList;

import com.nexzcore.plugins.ServerStatus.SpigotPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import io.socket.client.Socket;

public class PlayerEvents implements Listener {
    private final SpigotPlugin plugin;
    private static Socket socket;

    public PlayerEvents (Socket socket, SpigotPlugin plugin) {
        PlayerEvents.socket = socket;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ArrayList<String> players = plugin.getPlayerNames();
        String player = event.getPlayer().getDisplayName();
        if (!players.contains(player)) players.add(player);
        socket.emit("change", players);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ArrayList<String> players = plugin.getPlayerNames();
        String player = event.getPlayer().getDisplayName();
        players.remove(player);
        socket.emit("change", players);
    }

    public static void updateSocket(Socket socket) {
        PlayerEvents.socket = socket;
    }
}
