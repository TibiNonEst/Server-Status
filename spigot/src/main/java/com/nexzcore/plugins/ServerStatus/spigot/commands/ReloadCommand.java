package com.nexzcore.plugins.ServerStatus.spigot.commands;

import com.nexzcore.plugins.ServerStatus.SpigotPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ReloadCommand implements CommandExecutor {
    private final SpigotPlugin plugin;
    private final FileConfiguration config;

    public ReloadCommand (SpigotPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("serverstatus.reload")) {
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "Plugin reloaded, reconnecting socket.");
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
