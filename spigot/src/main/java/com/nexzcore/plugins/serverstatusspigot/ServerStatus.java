package com.nexzcore.plugins.serverstatusspigot;

import java.io.*;
import java.util.Objects;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.ChatColor;
import io.socket.client.Socket;
import io.socket.client.IO;
import com.nexzcore.plugins.serverstatusspigot.commands.*;
import com.nexzcore.plugins.serverstatusspigot.listeners.*;

public final class ServerStatus extends JavaPlugin {
    private FileConfiguration config;
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
        socket.on("get players server", args -> socket.emit("get players server", getServer().getOnlinePlayers()));
        socket.on("is online", args -> socket.emit("online"));
        socket.on("connect", args -> getLogger().info("Socket connected!"));
        socket.on("connect_failed", args -> getLogger().info("Socket connection failed."));
        socket.on("disconnect", args -> getLogger().info("Socket disconnected."));
        socket.on("error", args -> getLogger().info("Socket.io error: " + args));
        getServer().getPluginManager().registerEvents(new PlayerEvents(socket), this);
        Objects.requireNonNull(this.getCommand("reload")).setExecutor(new ReloadCommand(this, config));
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
            PlayerEvents.updateSocket(socket);
            socket.connect();
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Server Status plugin reloaded.");
        } catch (java.net.URISyntaxException e) {
            getLogger().info(e.getMessage());
        }
    }

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
