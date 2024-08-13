var stompClient = null;
var nickname = sessionStorage.getItem('nickname');
var roomCode = window.location.pathname.split('/').pop();
var selectedPlayer = null; // 선택된 플레이어 저장
let players = []; // 게임에 참여한 플레이어 목록
let currentPlayerIndex = 0; // 현재 턴 플레이어 인덱스
let turnTimer; // 타이머 변수
let voteTimer; // 투표 타이머 변수
let declarationTime = 15; // 각 플레이어의 발언 시간 (초)
let voteStartTime = 30; // 투표 시작 대기 시간 (초)
let votes = {}; // 플레이어 투표 상태
let declarations = {}; // 플레이어 발언 상태
let voteStarted = false;  // 투표 시작 상태를 추적하는 변수
let voteTimeoutId; // 전역 변수로 타이머 ID를 저장

window.onload = function() {
    if (!nickname) {
        window.location.href = '/'; // 닉네임이 없으면 홈으로 이동
    } else {
        document.getElementById('room-link').innerText = window.location.href;
        connect(nickname); // 웹소켓 연결 함수 호출
        checkGameStatus(); // 게임 상태 확인 함수 호출
    }
}

function resetSessionAndGoHome() {
    sessionStorage.removeItem('nickname');
    location.href = '/'; // 홈으로 이동
}

function copyLink() {
    const roomLinkElement = document.getElementById('room-link');
    if (roomLinkElement) {
        const link = roomLinkElement.innerText;
        navigator.clipboard.writeText(link).then(() => {
            showAlert('클립보드에 복사되었습니다.');
        }).catch(err => {
            console.error('복사 실패:', err);
        });
    }
}

function showAlert(message) {
    const alertElement = document.getElementById('alert');
    alertElement.innerText = message;
    alertElement.style.display = 'block';
    alertElement.style.opacity = '1';
    setTimeout(() => {
        alertElement.style.opacity = '0';
        setTimeout(() => {
            alertElement.style.display = 'none';
        }, 300); // 페이드 아웃 애니메이션 시간을 기다린 후 요소를 숨김
    }, 2000); // 2초 후 메시지를 사라지게 함
}


async function showPlayerCount() {
    const roomCode = window.location.pathname.split('/').pop();
    try {
        const response = await fetch(`/room/${roomCode}/playerCount`);
        if (response.ok) {
            const count = await response.json();
            document.getElementById('modal-text').innerHTML = `모든 플레이어가 입장했는지<br> 확인해주세요.<br>${count}명의 플레이어로 <br>게임을 시작하시겠습니까?`;
            document.getElementById('modal').style.display = 'block';

        } else {
            alert('플레이어 수를 가져오는 데 실패했습니다.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('오류가 발생했습니다. 다시 시도해 주세요.');
    }
}

function closeModal(modal) {
    if (modal) {
        modal.style.display = 'none';
    }
}


function closeVoteModal() {
    const voteModal = document.getElementById('vote-modal');
    if (voteModal) {
        voteModal.style.display = 'none'; // 모달을 숨김
    }
}
function confirmStartGame() {
    document.getElementById('modal').style.display = 'none';
    startGame();
}

async function startGame() {
    const roomCode = window.location.pathname.split('/').pop();
    try {
        const response = await fetch(`/api/game/start/${roomCode}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (response.ok) {
            const data = await response.json();
            console.log("게임 시작 요청이 성공했습니다.");

            // 클래스 리스트를 배열로 변환하여 로그로 출력
            // console.log(Array.from(gameSection.classList));
        } else {
            alert('게임을 시작하는 데 실패했습니다.');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('오류가 발생했습니다. 다시 시도해 주세요.');
    }
}

// function updatePlayerBoxes(players) {
//     if (!players) {
//         console.error('players 배열이 정의되지 않았습니다.');
//         return;
//     }
//
//     const playerBoxes = document.getElementById('player-boxes');
//     playerBoxes.innerHTML = ''; // 기존 박스 초기화
//
//     let index = 0;
//     players.forEach(player => {
//         console.log(`Creating box for player: ${player}`); // 로그 추가
//         const playerBox = document.createElement('div');
//         playerBox.className = 'player-box';
//         playerBox.id = 'player-box-' + player;
//         playerBox.innerText = player;
//         playerBox.style.left = (index % 2 === 0 ? '100px' : '400px');
//         playerBox.style.top = (50 + Math.floor(index / 2) * 100) + 'px';
//         playerBox.style.position = 'absolute'; // position 추가
//         playerBoxes.appendChild(playerBox);
//         index++;
//     });
// }


function sendMessage(event) {
    event.preventDefault();
    const content = document.getElementById('chatField').value;
    stompClient.send("/app/room/" + roomCode + "/chat", {}, JSON.stringify({'name': nickname, 'content': content}));
    document.getElementById('chatField').value = ''; // 메시지 전송 후 입력 칸 비우기
}

function connect(nickname) {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/room/' + roomCode + '/players', function (message) {
            updatePlayerList(JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/room/' + roomCode + '/chat', function (message) {
            showMessage(JSON.parse(message.body));
        });

        // 게임 시작 토픽 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/gameStart/' + nickname, function (message) {
            const gameMessage = JSON.parse(message.body).ownSentence;
            console.log('Received game message:', gameMessage); // 로그 추가

            console.log('Received game start message:', message.body); // 로그 추가
            const players = JSON.parse(message.body).players;
            // updatePlayerBoxes(players); // 박스 그리기
            document.getElementById('game-content').innerHTML = gameMessage;
            document.getElementById('room-section').style.display = 'none';

            // 'hidden' 클래스를 제거하고 'active' 클래스를 추가
            const gameSection = document.getElementById('game-section');
            gameSection.classList.remove('hidden');
            gameSection.classList.add('active');

            document.getElementById('clock-section').style.display = 'block';
        });

        // 턴 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/turnUpdate', (message) => {
            document.getElementById('game-controls').style.display = 'none';
            document.getElementById('in-game-controls').style.display = 'block';
            console.log("turnUpdate -> ", message.body);
            const currentTurnPlayer = message.body;
            console.log(currentTurnPlayer + '턴입니다.')
            updateTurnUI(currentTurnPlayer); // 현재 턴인 플레이어에 따라 UI 업데이트
        });


        // 선언 업데이트 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/declarationUpdate', function (message) {
            const declarations = JSON.parse(message.body);
            updateDeclarationList(declarations);
        });

        // 라이어투표 진행여부 업데이트 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/voteUpdate', function (message) {
            voteForLiarVoteResults(message.body);
        });

        // 라이어 투표 결과
        stompClient.subscribe('/topic/room/' + roomCode + '/voteResult', function (message) {
            liarVoteResults(message.body);
        });

        // 타이머 상태 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/timer', function (message) {
            // console.log('Received timer message:', message.body); // 추가된 로그
            try {
                const timerMessage = JSON.parse(message.body);
                // console.log('timerMessage.timeLeft: ' + timerMessage.timeLeft);
                // console.log('timerMessage.currentPlayer: ' + timerMessage.currentPlayer);
                updateTimer(timerMessage.timeLeft, timerMessage.currentPlayer);
            } catch (e) {
                console.error('Error parsing timer message:', e, message.body);
            }
        });

        stompClient.subscribe('/topic/room/' + roomCode + '/startVote', function (message) {
            console.log('Received startVote message:', message.body);
            showVoteModal();
        });

        // 투표 프롬프트 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/votePrompt', function (message) {
            console.log('Received votePrompt message:', message.body);
            showVotePrompt();
        });

        // 라이어의 추측 시작 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/liarGuess', function (message) {
            console.log('Received liarGuess message:', message.body);
            showLiarGuessPrompt();
        });

        // 라이어의 선택 결과
        stompClient.subscribe('/topic/room/' + roomCode + '/liar-result', function (message) {
            LiarLastGuess(message);
        });

        // 게임 종료 토픽 구독
        stompClient.subscribe('/topic/room/' + roomCode + '/gameEnd', function (message){
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            console.log('게임종료!!!');
            handleGameEnd(message);
        });

        stompClient.send("/app/room/" + roomCode + "/join", {}, JSON.stringify({ 'nickname': nickname }));
    });
}

window.onbeforeunload = function() {
    const roomCode = window.location.pathname.split('/').pop();
    if (stompClient) {
        stompClient.send("/app/room/" + roomCode + "/leave", {}, JSON.stringify({ 'nickname': nickname }));
    }
};

function updatePlayerList(players) {
    var playerList = document.getElementById('player-list');
    playerList.innerHTML = '';
    players.forEach(function(player) {
        var playerElement = document.createElement('div');
        playerElement.className = 'player-box'; //클래스 추가
        playerElement.appendChild(document.createTextNode(player));
        playerElement.onclick = function() { openVoteModal(player); };
        playerList.appendChild(playerElement);
    });
}

function showMessage(message) {
    const messages = document.getElementById('messages');
    const messageElement = document.createElement('div');
    messageElement.appendChild(document.createTextNode(message.name + ": " + message.content));
    messages.appendChild(messageElement);
}

function checkGameStatus() {
    const roomCode = window.location.pathname.split('/').pop();
    fetch(`/api/game/status/${roomCode}`)
        .then(response => response.json())
        .then(data => {
            if (data.status === 'IN_PROGRESS') {
                document.getElementById('start-game-btn').style.display = 'none'; // 게임 시작 버튼 숨기기
                document.getElementById('spectator-mode').style.display = 'block'; // 관전 모드 표시
            }
        });
}


function openVoteModal(player) {
    selectedPlayer = player;
    document.getElementById('vote-modal-text').innerText = `"${player}" 플레이어에게 투표하겠습니까?`;
    document.getElementById('vote-modal').style.display = 'block';
}

function voteForLiarVoteResults(voteCount) {
    const voteResults = document.getElementById('vote-for-liar-vote-result');
    voteResults.textContent = `${voteCount} 명 동의`;
}

function updateVoteResults(votes) {
    const voteResults = document.getElementById('vote-results');
    voteResults.innerHTML = ''; // 기존 결과 초기화

    // 화살표 초기화
    const arrows = document.querySelectorAll('.arrow');
    arrows.forEach(arrow => arrow.remove());

    Object.entries(votes).forEach(([voter, votee]) => {
        console.log('voter : ', voter);
        console.log('votee : ', votee);

        const fromBox = document.getElementById('player-box-' + voter);
        const toBox = document.getElementById('player-box-' + votee);

        if (fromBox && toBox) {
            console.log(`Drawing arrow from ${voter} to ${votee}`); // 로그 추가
            const arrow = document.createElement('div');
            arrow.className = 'arrow';
            const fromRect = fromBox.getBoundingClientRect();
            const toRect = toBox.getBoundingClientRect();
            const angle = Math.atan2(toRect.top - fromRect.top, toRect.left - fromRect.left);
            const length = Math.sqrt(Math.pow(toRect.left - fromRect.left, 2) + Math.pow(toRect.top - fromRect.top, 2));
            console.log(`FromRect: ${JSON.stringify(fromRect)}, ToRect: ${JSON.stringify(toRect)}`); // 로그 추가
            console.log(`Angle: ${angle}, Length: ${length}`); // 로그 추가
            arrow.style.position = 'absolute';
            arrow.style.transformOrigin = '0 0';
            arrow.style.transform = `rotate(${angle}rad)`;
            arrow.style.width = `${length}px`;
            arrow.style.height = '2px';
            arrow.style.backgroundColor = 'black';
            arrow.style.left = `${fromRect.left + fromRect.width / 2}px`;
            arrow.style.top = `${fromRect.top + fromRect.height / 2}px`;
            document.body.appendChild(arrow); // player-boxes 대신 body에 추가하여 위치가 정확히 적용되도록 함
            console.log(`Arrow appended to body`); // 로그 추가

            // 투표 결과 텍스트 추가
            const resultElement = document.createElement('div');
            resultElement.innerText = `${voter} -> ${votee}: 1표`;
            voteResults.appendChild(resultElement);
        } else {
            console.error(`Could not find player boxes for ${voter} or ${votee}`); // 로그 추가
        }
    });
}



// function startVote() {
//     const voteSection = document.getElementById('vote-section');
//     const votePlayers = document.getElementById('vote-players');
//     const voteResultsSection = document.getElementById('vote-results-section');
//     voteSection.style.display = 'block';
//     voteResultsSection.style.display = 'block';
//
//     fetch(`/api/game/players/${roomCode}`)
//         .then(response => response.json())
//         .then(players => {
//             votePlayers.innerHTML = '';
//
//             players.forEach((player, index) => {
//                 const voteButton = document.createElement('button');
//                 voteButton.innerText = player;
//                 voteButton.onclick = function() { openVoteModal(player); };
//                 votePlayers.appendChild(voteButton);
//             });
//
//             console.log(`Starting vote with players: ${players}`); // 로그 추가
//         });
// }

var currentUser = sessionStorage.getItem('nickname');

function updateTurnUI(currentTurnPlayer) {

    const turnMessage = document.getElementById('current-turn-message');
    turnMessage.innerText = `${currentTurnPlayer} 님이 발언할 차례입니다`;

    const declarationField = document.getElementById('declarationField');
    const submitButton = document.getElementById('submit-button'); //

    if (currentTurnPlayer.toString() === nickname) {
        // 현재 턴이 자신의 턴일 때 제출 입력 필드 활성화 및 제출 버튼 활성화
        declarationField.disabled = false;
        submitButton.disabled = false;
        // console.log('declaration-section을 보여줍니다.');
    } else {
        // 현재 턴이 자신의 턴이 아닐 때 제출 버튼만 비활성화
        declarationField.disabled = true;
        submitButton.disabled = true;
        // console.log('declaration-section을 숨깁니다.');
    }
}

function updateTimer(timeLeft, currentPlayer) {
    const timerElement = document.getElementById('turn-timer');
    timerElement.innerText = `남은 시간: ${timeLeft}초`;
    updateTurnUI(currentPlayer);
}

function showVoteModal() {
    if (!voteStarted) {
        fetchPlayersAndCreateVoteButtons(roomCode);
        document.getElementById('vote-modal').style.display = 'block';
        voteStarted = true;  // 투표가 시작되었음을 표시
    }
}

function fetchPlayersAndCreateVoteButtons(roomCode) {
    fetch(`/api/game/players/${roomCode}`)
        .then(response => response.json())
        .then(players => {
            const voteButtonsDiv = document.getElementById('vote-buttons');
            voteButtonsDiv.innerHTML = ''; // 기존 버튼 초기화

            players.forEach(player => {
                const button = document.createElement('button');
                button.className = 'modal-button';
                button.innerText = player;
                button.onclick = () => showVoteConfirmationModal(player);
                voteButtonsDiv.appendChild(button);
            });
        })
        .catch(error => {
            console.error('Error fetching players:', error);
        });
}

function showVoteConfirmationModal(player) {
    console.log(player + '선택');

    // 기존 모달을 찾습니다.
    let modal = document.querySelector('.vote-confirmation-modal');
    if (!modal) {
        // 모달이 없으면 새로 생성합니다.
        modal = document.createElement('div');
        modal.className = 'vote-confirmation-modal modal'; // 고유한 클래스 이름 추가
        modal.innerHTML = `
            <div class="modal-content">
                <p id="vote-confirm-modal-text"></p>
                <div class="modal-buttons">
                    <button class="modal-button" id="yes-button">예</button>
                    <button class="modal-button" id="no-button">아니오</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    }

    // 모달 내용 업데이트
    document.getElementById('vote-confirm-modal-text').innerText = `'${player}' 플레이어에게 투표하시겠습니까?`;

    // 이벤트 리스너 설정 전에 버튼 요소를 명확하게 가져옵니다.
    const yesButton = modal.querySelector('#yes-button');
    const noButton = modal.querySelector('#no-button');

    if (yesButton && noButton) {
        // 기존의 이벤트 리스너를 제거한 후 새로운 이벤트 리스너를 추가합니다.
        yesButton.onclick = null;
        noButton.onclick = null;

        yesButton.onclick = () => {
            const voteModal = document.getElementById('vote-modal');
            submitLiarVote(nickname, player);
            closeModal(modal);
            closeModal(voteModal);
        };

        noButton.onclick = () => {
            closeModal(modal);
        };

        modal.style.display = 'block'; // 모달을 표시합니다.
    } else {
        console.error('버튼 요소를 찾을 수 없습니다.');
    }
}



function submitLiarVote(player, vote) {
    fetch(`/api/game/vote/${roomCode}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name: player, vote: vote })
    })
        .then(response => {
            if (response.ok) {
                console.log('Vote submitted successfully');
            } else {
                console.error('Error submitting vote');
            }
        })
        .catch(error => {
            console.error('Error submitting vote:', error);
        });
}

function submitVote(choice) {
    const vote = {
        name: nickname, // 투표하는 플레이어 닉네임
        vote: choice // 'yes' or 'no'
    }
    stompClient.send("/app/room/" + roomCode + "/vote-for-liar-vote", {}, JSON.stringify(vote));
}

// 라이어 투표 여부 메시지 표시 함수
function showVotePrompt() {
    const votePromptMessage = "투표를 시행하겠습니까? <br>60초 뒤에는 자동으로 투표에 진입합니다.";
    document.getElementById('vote-prompt-message').innerHTML = votePromptMessage;
    document.getElementById('vote-prompt').style.display = 'block';

    // 이전에 설정된 타이머가 있다면 취소
    if (voteTimeoutId) {
        clearTimeout(voteTimeoutId);
    }

    // 새로운 타이머 설정하고 60초 후 자동으로 투표 모달을 표시
    voteTimeoutId = setTimeout(() => {
        document.getElementById('vote-prompt').style.display = 'none';
        if (!voteStarted) {  // 투표가 아직 시작되지 않았다면
            showVoteModal(); // 투표 모달을 자동으로 표시
        }
    }, 60000);


}

// 라이어가 걸린 후 추측 시작 메시지 표시 함수
function showLiarGuessPrompt() {
    const liarGuessMessage = "라이어가 발각되었습니다. 제시어를 맞출 기회가 주어집니다.";
    document.getElementById('liar-guess-message').innerText = liarGuessMessage;
    document.getElementById('liar-guess-prompt').style.display = 'block';

    // 라이어가 제시어를 맞추는 시간을 15초로 설정합니다.
    setTimeout(() => {
        document.getElementById('liar-guess-prompt').style.display = 'none';
    }, 15000);
}


function updateDeclarationList(declarations) {
    const declarationList = document.getElementById('declaration-list');
    declarationList.innerHTML = ''; // 기존 선언 초기화

    for (const [player, declaration] of Object.entries(declarations)) {
        const listItem = document.createElement('li');
        listItem.classList.add('fancy-declaration'); // 클래스 추가
        listItem.innerHTML = `
            <span class="player-name">${player}</span>
            <p class="declaration-text">${declaration}</p>
        `;
        declarationList.appendChild(listItem);
    }
}

function submitDeclaration(event) {
    event.preventDefault();
    const declaration = document.getElementById('declarationField').value;
    stompClient.send("/app/room/" + roomCode + "/declaration", {}, JSON.stringify({'name': currentUser, 'declaration': declaration}));
    document.getElementById('declarationField').value = ''; // 선언 제출 후 입력 칸 비우기
}

function liarVoteResults(message){
    const data = JSON.parse(message);

    const votes = data.voteResult;
    const mostVotedPlayers = data.mostVotedPlayers;
    const liar = data.liar;
    const liarWon = data.liarWon;

    // 결과 표시 섹션을 표시
    const voteResultSection = document.getElementById("vote-results-section");
    voteResultSection.style.display = "block";

    // 투표 결과를 표시할 요소를 가져옴
    const voteResult = document.getElementById("vote-results");
    voteResult.innerHTML = ""; // 기존 내용을 초기화

    // 각 플레이어의 투표 결과를 표시
    Object.keys(votes).forEach(player => {
        const resultItem = document.createElement("p");
        resultItem.innerHTML = `<strong>${player}</strong>: <span style="color: red;">${votes[player]} 표</span>`;
        voteResult.appendChild(resultItem);
    });

    // 최다 득표자 표시
    const mostVotedMessage = document.createElement("p");
    if (mostVotedPlayers.length > 1) {
        mostVotedMessage.innerHTML = `최다 득표자는 <strong>${mostVotedPlayers.join(", ")}</strong> 입니다.`;
    } else {
        mostVotedMessage.innerHTML = `최다 득표자는 <strong>${mostVotedPlayers[0]}</strong> 입니다.`;
    }
    voteResult.appendChild(mostVotedMessage);

    // 라이어 승리 여부에 따른 메시지 표시
    const liarResultMessage = document.createElement("p");
    if (liarWon) {
        liarResultMessage.innerHTML = `<span style="color: red;">라이어가 승리했습니다!</span> <br> 라이어는 <strong>${liar}</strong> 였습니다!`;
        voteResult.appendChild(liarResultMessage);
    } else {
        liarResultMessage.innerHTML = `<strong>라이어 검거 완료!</strong>`;
        voteResult.appendChild(liarResultMessage);

        const secondChanceMessage = document.createElement("p");
        secondChanceMessage.innerHTML = `<span style="color: red;">라이어에게 한번 더 기회가 주어집니다.</span>`;
        voteResult.appendChild(secondChanceMessage);


        showLiarOptions();
    }

}

async function showLiarOptions() {
    const roomCode = window.location.pathname.split('/').pop();
    try {
        const response = await fetch(`/api/game/liarOptions/${roomCode}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (response.ok) {
            // JSON 응답을 파싱합니다.
            const data = await response.json();

            const liar = data.liar; // 라이어의 닉네임 또는 ID
            const options = data.options; // 제시어 선택지 목록
            const correctAnswer = data.correctAnswer; // 정답

            const liarModal = document.getElementById("liar-modal");
            const modalOptionContainer = document.getElementById("modal-option-container");

            // 라이어라면 모달을 띄워 제시어 선택지 표시
            if (nickname === liar) {
                modalOptionContainer.innerHTML = ""; // 이전에 있던 선택지를 초기화

                options.forEach(option => {
                    const optionButton = document.createElement("button");
                    optionButton.textContent = option;
                    optionButton.onclick = () => {
                        submitLiarGuess(option, correctAnswer);
                        closeLiarModal(); // 선택 후 모달 닫기
                    };
                    modalOptionContainer.appendChild(optionButton);
                });

                // 모달을 화면에 표시
                liarModal.style.display = "block";
            } else {
            }
        } else {
            throw new Error('라이어 옵션을 가져오는데 에러가 걸렸어요!');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// 모달을 닫는 함수
function closeLiarModal() {
    const liarModal = document.getElementById("liar-modal");
    liarModal.style.display = "none";
}

// 라이어의 선택을 서버로 전송하고 결과를 처리하는 함수
async function submitLiarGuess(selectedOption, correctAnswer) {
    // 선택 결과를 서버로 전송
    const roomCode = window.location.pathname.split('/').pop();
    await fetch(`/api/game/liarGuess/${roomCode}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ selectedOption, correctAnswer })
    });

    // 결과가 서버에 전송된 후 모달을 닫음
    closeLiarModal();
}

// 라이어의 마지막 기회에 관한 핸들러
function LiarLastGuess(message) {
    const resultData = JSON.parse(message.body);
    const resultMessage = document.createElement("p");

    if (resultData.correct) {
        resultMessage.innerHTML = `<span style="color: red;">라이어가 제시어를 맞췄습니다. </span> <br> <strong>라이어가 승리했습니다.</strong>`;
    } else {
        resultMessage.innerHTML = `<span style="color: red;">라이어가 제시어를 맞추지 못했습니다!</span> <br> 라이어가 선택한 정답은 <strong>${resultData.selectedOption}</strong>입니다. <br> 정답은 <strong>${resultData.correctAnswer}</strong> 였습니다.`;
    }

    const voteResult = document.getElementById("vote-results");
    voteResult.appendChild(resultMessage);

}

function handleGameEnd(message) {
    // 1. UI 요소들 초기화
    document.getElementById('game-section').classList.add('hidden');
    document.getElementById('game-section').classList.remove('active');

    document.getElementById('vote-results-section').style.display = 'none'; // 투표 결과 섹션 숨김
    document.getElementById('vote-modal').style.display = 'none'; // 투표 모달 숨김
    document.getElementById('liar-modal').style.display = 'none'; // 라이어 모달 숨김
    document.getElementById('vote-prompt').style.display = 'none'; // 투표 프롬프트 숨김
    document.getElementById('liar-guess-prompt').style.display = 'none'; // 라이어 추측 모달 숨김
    document.getElementById('liar-result').style.display = 'none'; // 라이어 결과 메시지 숨김

    // 2. UI 상태 초기화
    document.getElementById('vote-results').innerHTML = ''; // 투표 결과 초기화
    document.getElementById('vote-for-liar-vote-result').innerHTML = '0명 동의'; // 투표 결과 초기화

    // document.getElementById('player-boxes').innerHTML = ''; // 플레이어 박스 초기화
    document.getElementById('declaration-list').innerHTML = ''; // 선언 리스트 초기화
    document.getElementById('turn-timer').innerText = ''; // 타이머 초기화
    document.getElementById('game-content').innerText = ''; // 게임 콘텐츠 초기화

    // 3. 모든 게임 상태 변수를 초기화
    players = []; // 플레이어 목록 초기화
    currentPlayerIndex = 0; // 현재 턴 플레이어 인덱스 초기화
    votes = {}; // 투표 상태 초기화
    declarations = {}; // 발언 상태 초기화
    voteStarted = false;  // 투표 시작 상태 초기화
    // 이전 게임의 타이머를 취소하여 영향을 없앰
    if (voteTimeoutId) {
        clearTimeout(voteTimeoutId);
        voteTimeoutId = null; // 타이머 ID 초기화
    }
    clearInterval(turnTimer); // 기존의 턴 타이머 초기화
    clearInterval(voteTimer); // 기존의 투표 타이머 초기화

    // // 4. 다시 하기 버튼을 표시하거나 홈으로 돌아가는 버튼 표시
    // document.getElementById('game-controls').innerHTML = `
    //     <button onclick="resetSessionAndGoHome()">홈으로</button>
    //     <button onclick="restartGame()">다시하기</button>
    // `;
}

function restartGame() {
    const roomCode = window.location.pathname.split('/').pop(); // 현재 URL에서 roomCode 추출

    // 서버에 게임 종료 요청
    fetch(`/api/game/end/${roomCode}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(response => {
            if (response.ok) {
                console.log("게임이 종료되었습니다. 새로운 게임을 시작합니다.");
                // 서버에서 게임이 종료된 후 새로운 게임을 시작
                confirmStartGame();
            } else {
                console.error("게임 종료 요청에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error("게임 종료 요청 중 오류가 발생했습니다:", error);
        });
}
