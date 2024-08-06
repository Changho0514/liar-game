var stompClient = null;

function connect(nickname) {
    var socket = new SockJS('/ws'); // 올바른 SockJS 엔드포인트 설정
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        const roomCode = window.location.pathname.split('/').pop(); // 방 코드를 URL에서 가져옴

        // 방에 연결된 사용자 목록 수신
        stompClient.subscribe('/topic/room/' + roomCode + '/players', function (message) {
            updatePlayerList(JSON.parse(message.body));
        });

        // 채팅 메시지 수신
        stompClient.subscribe('/topic/room/' + roomCode + '/chat', function (message) {
            showMessage(JSON.parse(message.body));
        });

        // 방에 입장 메시지 전송
        stompClient.send("/app/room/" + roomCode + "/join", {}, JSON.stringify({ 'nickname': nickname }));
    });
}

function sendMessage(nickname) {
    const content = document.getElementById('content').value;
    const roomCode = window.location.pathname.split('/').pop();
    stompClient.send("/app/room/" + roomCode + "/chat", {}, JSON.stringify({'name': nickname, 'content': content}));
    document.getElementById('content').value = ''; // 메시지 전송 후 입력 칸 비우기
}

function showMessage(message) {
    const messages = document.getElementById('messages');
    const messageElement = document.createElement('div');
    messageElement.appendChild(document.createTextNode(message.name + ": " + message.content));
    messages.appendChild(messageElement);
}

function updatePlayerList(players) {
    var playerList = document.getElementById('player-list'); // 플레이어 목록을 표시할 div
    if (!playerList) {
        console.error('player-list element not found');
        return;
    }
    playerList.innerHTML = '';
    players.forEach(function(player) {
        var playerElement = document.createElement('div');
        playerElement.appendChild(document.createTextNode(player));
        playerList.appendChild(playerElement);
    });
}
