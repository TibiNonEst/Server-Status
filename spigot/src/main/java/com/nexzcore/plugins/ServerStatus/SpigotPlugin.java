package com.nexzcore.plugins.ServerStatus;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import io.socket.client.Socket;
import io.socket.client.IO;
import com.nexzcore.plugins.ServerStatus.spigot.commands.*;
import com.nexzcore.plugins.ServerStatus.spigot.listeners.*;

public final class SpigotPlugin extends JavaPlugin {
    private FileConfiguration config;
    private Socket socket;
    private String uri;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();
        uri = config.getString("address") + ":" + config.getInt("port");
        socket = IO.socket(URI.create(uri));
        getLogger().info("Socket.io connection initialized at " + uri);
        socket.connect();
        socket.emit("online");
        socket.on("get players server", args -> socket.emit("get players server", getPlayerNames()));
        socket.on("is online", args -> socket.emit("online"));
        socket.on(Socket.EVENT_CONNECT, args -> getLogger().info("Socket connected!"));
        socket.on(Socket.EVENT_DISCONNECT, args -> getLogger().info("Socket disconnected."));
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            getLogger().info("Socket connection failed, attempting reconnection.");
            socket.connect();
        });
        getServer().getPluginManager().registerEvents(new PlayerEvents(socket, this), this);
        Objects.requireNonNull(this.getCommand("serverstatus")).setExecutor(new ReloadCommand(this, config));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        socket.emit("offline");
        socket.disconnect();
        getLogger().info("Plugin stopping.");
    }

    public ArrayList<String> getPlayerNames() {
        ArrayList<String> players = new ArrayList<>();
        for (Player player : getServer().getOnlinePlayers()) players.add(player.getDisplayName());
        return players;
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
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Server Status plugin reloaded, reconnecting socket.");
    }

    private void loadConfig() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().warning("Unable to load config file." + e.getMessage());
        }
    }
}
