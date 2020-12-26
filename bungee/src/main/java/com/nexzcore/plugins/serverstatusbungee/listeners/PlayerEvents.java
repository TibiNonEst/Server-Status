package com.nexzcore.plugins.serverstatusbungee.listeners;

import java.util.ArrayList;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import io.socket.client.Socket;

public class PlayerEvents implements Listener {
    private static Socket socket;

    public PlayerEvents (Socket _socket) {
        socket = _socket;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ArrayList<String> players = getPlayerNames();
        String player = event.getPlayer().getDisplayName();
        if (!players.contains(player)) players.add(player);
        socket.emit("change", players);
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
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
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) players.add(player.getDisplayName());
        return players;
    }
}
