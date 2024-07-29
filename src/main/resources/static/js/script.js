document.getElementById('create-room-form').addEventListener('submit', function(event) {
    event.preventDefault();
    const roomName = document.getElementById('room-name').value;
    const playerCount = document.getElementById('player-count').value;
    alert(`Room "${roomName}" with ${playerCount} players created!`);
});
