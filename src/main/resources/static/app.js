var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected); // USAR PARA INICIO DE PARTIDA
    $("#disconnect").prop("disabled", !connected); // USAR PARA FIN DE PARTIDA
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/finTurnos', function (greeting) {
            showGreeting(JSON.parse(greeting.body).statusCode);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

var a = new URLSearchParams(window.location.search).get("gp");

function sendName() {
    stompClient.send("/api/turno/" + a, {}, JSON.stringify(""));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});