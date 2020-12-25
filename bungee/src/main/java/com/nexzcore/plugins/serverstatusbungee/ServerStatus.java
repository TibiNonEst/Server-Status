package com.nexzcore.plugins.serverstatusbungee;

import java.io.*;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;
import io.socket.client.Socket;
import io.socket.client.IO;
import com.nexzcore.plugins.serverstatusbungee.commands.*;
import com.nexzcore.plugins.serverstatusbungee.listeners.*;

public final class ServerStatus extends Plugin {
    private Configuration config;
    private Socket socket;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.loadConfig();
        try {
            String uri = config.getString("address") + ":" + config.getInt("port");
            socket = IO.socket(uri);
            socket.connect();
            getLogger().info("Socket.io connection initialized at " + uri);
            socket.emit("online");
        } catch (java.net.URISyntaxException e) {
            getLogger().info(e.getMessage());
        }
        socket.on("get players server", args -> socket.emit("get players server", getProxy().getPlayers()));
        socket.on("is online", args -> socket.emit("online"));
        socket.on("connect", args -> getLogger().info("Socket connected!"));
        socket.on("connect_failed", args -> getLogger().info("Socket connection failed."));
        socket.on("disconnect", args -> getLogger().info("Socket disconnected."));
        socket.on("error", args -> getLogger().info("Socket.io error: " + args));
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
        this.loadConfig();
        try {
            socket.disconnect();
            String uri = config.getString("address") + ":" + config.getInt("port");
            socket = IO.socket(uri);
            PlayerEvents.updateSocket(socket);
            socket.connect();
            getProxy().getConsole().sendMessage(ChatColor.GREEN + "Server Status plugin reloaded.");
        } catch (java.net.URISyntaxException e) {
            getLogger().info(e.getMessage());
        }
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
