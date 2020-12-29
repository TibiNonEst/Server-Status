package com.nexzcore.plugins.ServerStatus.bungee.commands;

import com.nexzcore.plugins.ServerStatus.BungeePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReloadCommand extends Command {
    private final BungeePlugin plugin;
    private final Configuration config;

    public ReloadCommand (BungeePlugin _plugin, Configuration _config) {
        super("serverstatus");
        plugin = _plugin;
        config = _config;
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (sender.hasPermission("serverstatus.reload")) {
                plugin.reload();
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "Plugin reloaded."));
            } else {
                String noPerm = config.getString("messages.no-permission");
                sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', noPerm)));
            }
        } else {
            plugin.reload();
        }
    }
}
