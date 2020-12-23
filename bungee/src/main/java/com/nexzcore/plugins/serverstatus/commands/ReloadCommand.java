package com.nexzcore.plugins.serverstatus.commands;

import com.nexzcore.plugins.serverstatus.ServerStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReloadCommand extends Command {
    private ServerStatus plugin;
    private Configuration config;

    public ReloadCommand (ServerStatus plugin, Configuration config) {
        super("serverstatus");
        this.plugin = plugin;
        this.config = config;
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !sender.hasPermission("serverstatus.reload")) {
            String noPerm = config.getString("messages.no-permission");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPerm));
        } else {
            plugin.reload();
            if (sender instanceof ProxiedPlayer) sender.sendMessage(ChatColor.GREEN + "Plugin reloaded.");
        }
    }
}
