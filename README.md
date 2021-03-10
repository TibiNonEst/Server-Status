# Server Status - Plugin, Website, Discord Bot
**A real time server status plugin**

*Note: this plugin is still a WIP and very subject to change*

The Bungeecord, Spigot, and Velocity plugins all use a java port of the Socket.io client. This means they can connect directly to the webserver and send real time information when the status updates. When the plugin starts it tells the web server which in turn tells the web clients that it has come online. When a player joins or leaves the server it sends the current playerlist to all the clients so that they can display the current information. All of this is done in realtime, instead of being queried every few seconds.

## Commands and Permission
The plugin currently only has one command: `/reload`. The permission for this is `serverstatus.reload` and it is recommended you use a permissions plugin such as luckperms. All implementations of the plugin use the same command, but this shouldn't be an issue as it's unlikely you will need to run the plugin in both your proxy and backend servers.

**Note: The `/reload` command does not currently always work as expected. While the it does disconnect and reconnect from websockets, and reload the config, there's no guarantee that the websockets will connect as expected until you restart the server.**

## Installation
The Bungeecord Spigot, and Velocity plugins are installed like any other plugin, just add them to your `plugins` folder and you're good to go! You can download the plugin in the releases tab.

## Configuration
All of the configuration is contained within the `config.yml` file. In here you should set the `address` field to you're web server address and the `port` field to your web server port. The `no-permission` field is what will be show when someone tries to run the `/reload` command without proper perms.

### Default configuration:
```
# Socket.io server address
address: 'http://localhost'
# Socket.io server port
port: 80

# Plugin messages
messages:
  # Message to send to players if they do not have the required permission
  no-permission: '&cYou do not have permission to execute this command.'
```
