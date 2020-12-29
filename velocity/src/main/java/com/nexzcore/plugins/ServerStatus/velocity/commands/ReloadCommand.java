package com.nexzcore.plugins.ServerStatus.velocity.commands;

import com.nexzcore.plugins.ServerStatus.VelocityPlugin;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;

public class ReloadCommand implements SimpleCommand {
    private final VelocityPlugin plugin;
    private final ConfigurationNode config;

    public ReloadCommand (VelocityPlugin plugin, ConfigurationNode config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        if (source instanceof Player) {
            if (source.hasPermission("serverstatus.reload")) {
                plugin.reload();
                source.sendMessage(Component.text("Plugin reloaded.").color(NamedTextColor.GREEN));
            } else {
                String noPerm = config.getNode("messages.no-permission").getString();
                assert noPerm != null;
                source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(noPerm));
            }
        } else {
            plugin.reload();
        }
    }
}
