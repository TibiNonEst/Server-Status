package com.nexzcore.plugins.serverstatus.listeners;

import java.util.Collection;
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

    public PlayerEvents (Socket socket) {
        this.socket = socket;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ArrayList players = getPlayerNames(ProxyServer.getInstance().getPlayers());
        String player = event.getPlayer().getDisplayName();
        if (!players.contains(player)) {
            players.add(player);
        }
        socket.emit("change", players);
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        ArrayList players = getPlayerNames(ProxyServer.getInstance().getPlayers());
        String player  = event.getPlayer().getDisplayName();
        if (players.contains(player)) {
            players.remove(player);
        }
        socket.emit("change", players);
    }

    public static void updateSocket(Socket _socket) {
        socket = _socket;
    }

    private ArrayList getPlayerNames(Collection<ProxiedPlayer> input) {
        ArrayList players = new ArrayList();
        for (ProxiedPlayer player : input) {
            players.add(player.getDisplayName());
        }
        return players;
    }
}
