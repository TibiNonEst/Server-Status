package com.nexzcore.plugins.ServerStatus;

import java.io.*;
import java.net.URI;

import com.google.common.io.ByteStreams;
import com.nexzcore.plugins.ServerStatus.bungee.commands.ReloadCommand;
import com.nexzcore.plugins.ServerStatus.bungee.listeners.PlayerEvents;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import io.socket.client.Socket;
import io.socket.client.IO;
import com.nexzcore.plugins.ServerStatus.bungee.commands.*;
import com.nexzcore.plugins.ServerStatus.bungee.listeners.*;

public final class BungeePlugin extends Plugin {
    private Configuration config;
    private Socket socket;
    private String uri;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.loadConfig();
        uri = config.getString("address") + ":" + config.getInt("port");
        socket = IO.socket(URI.create(uri));
        getLogger().info("Socket.io connection initialized at " + uri);
        socket.connect();
        socket.emit("online");
        socket.on("get players server", args -> socket.emit("get players server", getProxy().getPlayers()));
        socket.on("is online", args -> socket.emit("online"));
        socket.on(Socket.EVENT_CONNECT, args -> getLogger().info("Socket connected!"));
        socket.on(Socket.EVENT_DISCONNECT, args -> getLogger().info("Socket disconnected."));
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            getLogger().info("Socket connection failed, attempting reconnection.");
            socket.connect();
        });
        getProxy().getPluginManager().registerListener(this, new PlayerEvents(socket));
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this, config));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        socket.emit("offline");
        socket.disconnect();
        getLogger().info("Plugin disabled.");
    }

    public void reload() {
        loadConfig();
        String newUri = config.getString("address") + ":" + config.getInt("port");
        if (!uri.equals(newUri)) {
            socket.emit("offline");
            uri = newUri;
            socket = IO.socket(URI.create(uri));
            PlayerEvents.updateSocket(socket);
            socket.disconnect().connect();
            socket.emit("online");
        } else {
            socket.disconnect().connect();
        }
        getProxy().getConsole().sendMessage(new TextComponent(ChatColor.GREEN + "Server Status plugin reloaded, reconnecting socket."));
    }

    private void loadConfig() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdir();
            File config = new File(getDataFolder().getPath(), "config.yml");
            if (!config.exists()) {
                try {
                    config.createNewFile();
                    try (InputStream is = getResourceAsStream("config.yml");
                         OutputStream os = new FileOutputStream(config)) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create configuration file", e);
                }
            }
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
