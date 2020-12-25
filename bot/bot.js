const fs = require("fs");
const io = require("socket.io-client");
const Discord = require("discord.js");
const YAML = require("yaml");
const client = new Discord.Client();

const configFile = fs.readFileSync("config.yml", "utf8");
config = YAML.parse(configFile);
const socket = io(config.web_socket.address + ":" +config.web_socket.port);

socket.on("get players", data => updateCount(data));
socket.on("change", data => updateCount(data));
socket.on("online", () => {
  changeName(config.discord.channels.voice_status, "Status: Online")
  socket.emit("get players");
});
socket.on("offline", () => changeName(config.discord.channels.voice_status, "Status: Offline");

client.on("ready", () => {
  console.log("Bot started!");
  client.user.setActivity(config.discord.status.message, {type: config.discord.status.type});
  socket.emit("is online");
  socket.emit("get players");
});

function updateCount(data) {
  let playerCount = data.length;
  let players = data.join(", ");
  let textChannels = config.discord.channels.text;
  changeName(config.discord.channels.voice_players, `Players: ${playerCount}/${config.discord.max_players}`);
  textChannels.forEach(id => changeDesc(id, `Players: ${playerCount}/${config.discord.max_players} ${players}`));
}

function changeName (id, name) {
  client.channels.fetch(id)
  .then(channel => channel.setName(name))
  .catch(console.error);
}

function changeDesc (id, desc) {
  client.channels.fetch(id)
  .then(channel => channel.setTopic(desc))
  .catch(console.error);
}

client.login(config.discord.token);
