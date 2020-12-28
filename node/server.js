const express = require("express");
const fs = require("fs");
const YAML = require("yaml");
const app = express();
const http = require("http").Server(app);
const io = require("socket.io")(http);

const configFile = fs.readFileSync("config.yml", "utf8");
const config = YAML.parse(configFile);
let current = {online: false, players: {max: config.max_players, online: 0, list: []}};

app.use((req, res, next) => {
  res.header("X-Frame-Options", "DENY");
  res.header("X-Powered-By", "Nexzcore");
  res.header("Referrer-Policy", "strict-origin-when-cross-origin");
  next();
});

app.set("view engine", "pug");
app.set("views", "./");

io.on("connection", socket => {
  socket.on("is online", () => {
    current.online = false;
    io.emit("is online");
  });
  socket.on("online", () => {
    current.online = true;
    io.emit("online");
  });
  socket.on("offline", () => {
    current.online = false;
    io.emit("offline");
  });
  socket.on("change", data => {
    io.emit("change", data);
    current.players.online = data.length;
    current.players.list = data;
  });
  socket.on("get players", () => {
    if (current.online) io.emit("get players server");
    else io.emit("offline");
  });
  socket.on("get players server", data => {
    io.emit("get players", data);
    current.players.online = data.length;
    current.players.list = data;
  });
});

setInterval(() => {
  if (current.online) io.emit("get players server");
}, 5000);

app.get("/", (req, res) => res.render("index", {config: config}));

app.get("/simple", (req, res) => res.render("simple", {config: config}));

app.get("/api", (req, res) => {
  res.header("Content-Type", "application/json");
  res.send(JSON.stringify(current, null, 2));
});

app.get("/main.js", (req, res) => res.sendFile(`${__dirname}/main.js`));

app.use((req, res) => res.redirect("/"));

const server = http.listen(process.env.PORT || config.port, () => {
  const host = server.address().address;
  const port = server.address().port;
  console.log("Server running at http://%s:%s", host, port);
});
