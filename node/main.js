$(function () {
  const socket = io();
  $("body").tooltip({selector: "[data-toggle=tooltip]"});
  socket.emit("is online");
  socket.emit("get players");
  socket.on("get players", data => displayPlayers(data));
  socket.on("change", data => displayPlayers(data));
  socket.on("online", () => {
    $("#status").removeClass("text-danger");
    $("#status").addClass("text-success");
    socket.emit("get players");
  });
  socket.on("offline", () => {
    $("#status").removeClass("text-success");
    $("#status").addClass("text-danger");
    $("#players").text(0);
  });
  function displayPlayers(data) {
    $("#players").text(data.length);
    if (data.length > 0) {
      $("#display").empty();
      $("#display").removeClass("d-none");
      data.forEach(item => $("#display").append(`<img src="https://minotar.net/helm/${item}/56" class="mx-2 rounded" draggable="false" data-toggle="tooltip" data-placement="top" title="${item}">`));
    } else {
      $("#display").addClass("d-none");
    }
  }
});