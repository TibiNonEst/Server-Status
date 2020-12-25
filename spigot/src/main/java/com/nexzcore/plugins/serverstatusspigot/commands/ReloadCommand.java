package com.nexzcore.plugins.serverstatusspigot.commands;

import com.nexzcore.plugins.serverstatusspigot.ServerStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ReloadCommand implements CommandExecutor {
    private ServerStatus plugin;
    private FileConfiguration config;

    public ReloadCommand (ServerStatus plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("serverstatus.reload")) {
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "Plugin reloaded.");
            } else {
                String noPerm = config.getString("messages.no-permission");
                assert noPerm != null;
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPerm));
            }
        } else {
            plugin.reload();
        }
        return true;
    }
}
