# Server Status - Plugin, Website, Discord bot
**A real time server status plugin**

*Note: this plugin is still a WIP and very subject to change*

## Web server/site
### Backend
The basis of this project is on a web server. This web server runs socket.io, a wrapper around and implementation of websockets. The webserver communicates to the plugin, and clients on the site, to distribute real time server information. When the web server recieves a packet from the minecraft server it immideatly routes the information to clients on the web on discord.

### Frontend
The frontend of this site dynamically displays the information it recives from the webserver. It has a small indicator, either red or green, to display the current status of the minecraft server. Additionally it showes the player count of people online and lists their avatars *(you can view their names on hover)*. There is also a simple text-only version of the site that dynamically updates at `/simple` and an api which returns the current status statically at `/api`.

### Installation
To install this webserver it's required that you have node.js and npm installed on your system. Unzip the `ServerStatus-Node-version.zip` file in the most recent release and add it to the directory you want to run the webserver in. To install all required dependencies run `npm install` in the node folder, and to start it run `npm start`!

### Configuration
Don't want your web server running on the default port? No problem! The configuration file located at `config.yml` lets you customize which port your server runs at in the `port` field. It also allows you to add a custom name for your site visible in the title of the webpage as well as in the header in the middle with the `name` field. As this plugin doesn't currenrly query your server for its max player count, set the visible max players of your server in `max_players`. Finally, if you don't want the background of your site to be play, set a background image in the `background_img` field! To get your changes to take effect, restart the webserver with `npm start`.

#### Default configuration:
```
# Port for the web server to run at
port: 80

# Name of the site
name: 'Minecraft Server'

# Server's max players
max_players: 20

# Background image for the site (optional)
# To remove set to ''
background_img: ''
```

## Bungeecord / Spigot plugin
The bungeecord and spigot plugin both use a java port of the socket.io client. This means they can connect directly to the web server and send real time information when the status updates. When the plugin starts it tells the web server which in turn tells the web clients that it has come online. When a player joins or leaves the server it sends the current playerlist to all the clients so that they can display the current information. All of this is done in realtime, instead of being queried every few seconds.

### Commands and Permission
The plugin currently only has one command: `/reload`. The permission for this is `serverstatus.reload` and it is reccomended you use a permissions plugin such as luckperms. Both the bungeecord and spigot plugins use the same command, but this shouldn't be an issue as it's unlikely you will need to run the plugin in both your bungeecord and spigot servers.

**Note: The `/reload` command does not currently work as expected, this is being investigated, but if you need to reload your socket.io connected and `/reload` doesn't work restart the server**

### Installation
The bungeecord and spigot plugins are installed like any other plugin, just add them to your plugin folder and you're good to go! You can download the plugin in the releases tab.

### Configuration
All of the configuration is contained within the `config.yml` file. In here you should set the `address` field to you're web server address and the `port` field to your web server port. The `no-permission` field is what will be show when someone tries to run the `/reload` command without proper perms.

#### Default configuration:
```
# Socket.io server address
address: "http://localhost"
# Socket.io server port
port: 80

# Plugin messages
messages:
  # Message to send to players if they do not have the required permission
  no-permission: "&cYou do not have permission to execute this command."
```

## Discord bot
The discord runs on a discord server changing the names and descriptions of channels as people leave and join the server. The bot can change the titles of 2 voice channels, it's reccomended that these are restricted for best use, to display the current status of the server and player count. The bot also dynamically changes the descriptions of any provided text channels to display the status and player count, in addition to listing players online.

### Installation
The install for the discord bot is similar to the webserver. Download the `ServerStatus-Bot-version.zip` file from the most recent release, unzip it where you want it to run, run `npm install` in the folder to install the dependencies, and `npm start` to start it! To get it onto your server you can use [this guide](https://www.howtogeek.com/364225/how-to-make-your-own-discord-bot/) from How To Geek.

### Configuration
The `web_socket` section of this configuration is similar to the plugin. Enter in the address and port of your webserver to get socket.io to connect. The rest of the bot's setup is found in the `discord` section. In `channels` you can add the ids of the chnnels you want the bot to modify (*make sure you keep the id's in quotes*). If you don't know how to get channel id's you can follow [this guide](https://www.youtube.com/watch?v=NLWtSHWKbAI) by Gauging Gadgets. Next, like the website, set the max players of your server in `max_players` as the plugin doesn't query that information. Here you can also set the status you want your bot to have and the type. The available types are `PLAYING` (Playing), `WATCHING` (Watching), and `LISTENING` (Listening to). Finally, enter the token you got from the discord developer dashboard into the `token` area, save, and restart the bot using `npm start`.

#### Default configuration:
```
# Configuration for the websocket used to contact the server
web_socket:
  # Web server address
  address: 'http://localhost'
  # Web server port
  port: 80

# Discord bot settings
discord:
  
  # Channel id setup
  # Make sure to use quotes!
  channels:
    # Online/Offline status voice channel
    voice_status: ''
    # Player count voice channel
    voice_players: ''
    # Player count text channels
    text:
      - ''
  
  # Minecraft Server max players
  max_players: 20
  
  # Discord status
  status:
    # Status type
    # Options: PLAYING, WATCHING, LISTENING
    type: 'WATCHING'
    # Status message
    message: 'My Minecraft Server'
  
  # Discord bot token
  token: 'TOKEN'
```
