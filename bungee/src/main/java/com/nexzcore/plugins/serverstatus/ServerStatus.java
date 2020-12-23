package com.nexzcore.plugins.serverstatus;

import java.io.*;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;
import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;
import com.nexzcore.plugins.serverstatus.commands.*;
import com.nexzcore.plugins.serverstatus.listeners.*;

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
        socket.on("get players server", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("get players server", ProxyServer.getInstance().getPlayers());
            }
        });
        socket.on("is online", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("online");
            }
        });
        getProxy().getPluginManager().registerListener(this, new PlayerEvents(socket));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReloadCommand(this, config));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        socket.emit("offline");
        socket.disconnect();
        getLogger().info("Socket.io disconnected");
    }

    public void reload() {
        this.loadConfig();
        try {
            socket.disconnect();
            String uri = config.getString("address") + ":" + config.getInt("port");
            socket = IO.socket(uri);
            socket.connect();
            PlayerEvents.updateSocket(socket);
            ProxyServer.getInstance().getConsole().sendMessage(ChatColor.GREEN + "Server Status plugin reloaded.");
        } catch (java.net.URISyntaxException e) {
            getLogger().info(e.getMessage());
        }
    }

    private void loadConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
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
