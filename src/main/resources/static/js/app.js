var socket = new SockJS('/search-engine-websocket');
var stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    stompClient.subscribe('/topic/results', function (searchResult) {
        showUrl(JSON.parse(searchResult.body));
    })
});
var paused = false;

function sendSearchingInfo() {
    reset();
    stompClient.send("/app/search", {}, JSON.stringify(
        {
            'url' : $("#url").val(),
            'threadsCount' : $("#threadsCount").val(),
            'textToSearch' : $("#textToSearch").val(),
            'scannedUrlsCount' : $("#scannedUrlsCount").val()
        }));
}

function reset() {
    $("#results").empty();
    var progressBarElem = $("#progress");
    progressBarElem.attr("max", $("#scannedUrlsCount").val());
    progressBarElem.val(0);
}

function showUrl(searchResult) {
    var newElement = "<a href=\"" + searchResult.url + "\">" + searchResult.url + "</a><br/>";
    var color = "red";
    if (searchResult.inProgress === true) {
        newElement += "Обработка...";
    } else if (searchResult.hasError === true) {
        newElement += searchResult.errorInfo;
    } else if (searchResult.wordFound === true) {
        newElement += "Текст найден";
        color = "green";
    } else {
        newElement += "Текст не найден";
    }

    var selected = $("li[id='" + searchResult.id + "']");
    if (selected.length) {
        selected.css("color", color);
        selected.html(newElement);
        moveProgress();
    } else {
        $("#results").append("<li id=\"" + searchResult.id + "\">" + newElement + "</li>");
    }
}

function moveProgress() {
    $('#progress').val( function(i, oldValue) {
        return ++oldValue;
    });
}

function start() {
    if (paused) {
        paused = false;
        stompClient.send("/app/resume");
    } else {
        if ($("#url").val().length > 0 &&
            $("#threadsCount").val().length > 0 &&
            $("#textToSearch").val().length > 0 &&
            $("#scannedUrlsCount").val().length > 0) {
            sendSearchingInfo();
        }
    }
}

function stop() {
    stompClient.send("/app/stop", {});
}

function pause() {
    paused = true;
    stompClient.send("/app/pause", {});
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#start").click(function() { start(); });
    $("#stop").click(function () { stop(); });
    $("#pause").click(function () { pause(); });
});