package com.nexzcore.plugins.serverstatusspigot.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import io.socket.client.Socket;

public class PlayerEvents implements Listener {
    private static Socket socket;

    public PlayerEvents (Socket socket) {
        PlayerEvents.socket = socket;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ArrayList<String> players = getPlayerNames();
        String player = event.getPlayer().getDisplayName();
        if (!players.contains(player)) players.add(player);
        socket.emit("change", players);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ArrayList<String> players = getPlayerNames();
        String player = event.getPlayer().getDisplayName();
        players.remove(player);
        socket.emit("change", players);
    }

    public static void updateSocket(Socket _socket) {
        socket = _socket;
    }

    private ArrayList<String> getPlayerNames() {
        ArrayList<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) players.add(player.getDisplayName());
        return players;
    }
}
