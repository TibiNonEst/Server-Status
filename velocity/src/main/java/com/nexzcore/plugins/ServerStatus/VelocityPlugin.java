package com.nexzcore.plugins.ServerStatus;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;
import io.socket.client.Socket;
import io.socket.client.IO;
import com.nexzcore.plugins.ServerStatus.velocity.Info;
import com.nexzcore.plugins.ServerStatus.velocity.commands.*;
import com.nexzcore.plugins.ServerStatus.velocity.listeners.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

@Plugin(
        id = "serverstatus",
        name = "ServerStatus",
        description = Info.DESCRIPTION,
        version = Info.VERSION,
        authors = {"TibiNonEst"}
)
public class VelocityPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private ConfigurationNode config;
    private Socket socket;
    private String uri;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        // Plugin startup logic
        loadConfig();
        uri = config.getNode("address").getString() + ":" + config.getNode("port").getInt();
        socket = IO.socket(URI.create(uri));
        logger.info("Socket.io connection initialized at " + uri);
        socket.connect();
        socket.emit("online");
        socket.on("get players server", args -> socket.emit("get players server", getPlayerNames()));
        socket.on("is online", args -> socket.emit("online"));
        socket.on(Socket.EVENT_CONNECT, args -> logger.info("Socket connected!"));
        socket.on(Socket.EVENT_DISCONNECT, args -> logger.info("Socket disconnected."));
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            logger.info("Socket connection failed, attempting reconnection.");
            socket.connect();
        });
        server.getEventManager().register(this, new PlayerEvents(socket, this));
        CommandMeta reloadMeta = server.getCommandManager().metaBuilder("serverstatus")
                .aliases("ssr", "ssreload", "srvstatus", "vserverstatus", "vssr", "vssreload", "vsrvstatus")
                .build();
        server.getCommandManager().register(reloadMeta, new ReloadCommand(this, config));
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        socket.emit("offline");
        socket.disconnect();
        logger.info("Plugin stopping.");
    }

    public ArrayList<String> getPlayerNames() {
        ArrayList<String> players = new ArrayList<>();
        for (Player player : server.getAllPlayers()) players.add(player.getUsername());
        return players;
    }

    public void reload() {
        loadConfig();
        String newUri = config.getNode("address").getString() + ":" + config.getNode("port").getInt();
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
        server.getConsoleCommandSource().sendMessage(Component.text("Server Status plugin reloaded, reconnecting socket.").color(NamedTextColor.GREEN));
    }

    private void loadConfig() {
        File file = new File(dataDirectory.toFile(), "config.yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml")), file.toPath());
            } catch (IOException e) {
                logger.warn("Unable to load default config file. " + e.getMessage());
            }
        }
        try {
            config = YAMLConfigurationLoader.builder().setFile(file).build().load();
        } catch (IOException e) {
            logger.warn("Unable to load config file. " + e.getMessage());
        }
    }
}
