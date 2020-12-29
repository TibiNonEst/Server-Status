package com.nexzcore.plugins.ServerStatus.velocity.listeners;

import com.nexzcore.plugins.ServerStatus.VelocityPlugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import io.socket.client.Socket;

import java.util.ArrayList;

public class PlayerEvents {
    private final VelocityPlugin plugin;
    private static Socket socket;

    public PlayerEvents (Socket socket, VelocityPlugin plugin) {
        PlayerEvents.socket = socket;
        this.plugin = plugin;
    }

    @Subscribe
    public void onJoin(ServerConnectedEvent event) {
        ArrayList<String> players = plugin.getPlayerNames();
        String player = event.getPlayer().getUsername();
        if (!players.contains(player)) players.add(player);
        socket.emit("change", players);
    }

    @Subscribe
    public void onLeave(KickedFromServerEvent event) {
        ArrayList<String> players = plugin.getPlayerNames();
        String player = event.getPlayer().getUsername();
        players.remove(player);
        socket.emit("change", players);
    }

    public static void updateSocket(Socket socket) {
        PlayerEvents.socket = socket;
    }
}
