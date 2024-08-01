package com.backend.liargame.game.contoller;

import com.backend.liargame.chat.ChatMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Controller
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, CopyOnWriteArrayList<String>> roomPlayers = new ConcurrentHashMap<>();

    public RoomController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/room/create")
    public String createRoomPage() {
        return "create";
    }

    @PostMapping("/room/enterRoom")
    public ResponseEntity<String> createRoom(@RequestBody Map<String, String> request, HttpSession session) {
        String nickname = request.get("nickname");
        // UUID 생성
        String roomCode = UUID.randomUUID().toString().substring(0, 8);
        session.setAttribute("nickname", nickname); // 세션에 닉네임 저장
        // 닉네임과 방 코드를 이용해 필요한 로직 처리 (예: 방 생성, 사용자 추가 등)

        // 성공적으로 처리된 경우
        return new ResponseEntity<>(roomCode, HttpStatus.OK);

        // 오류가 발생한 경우 (예: 방 생성 실패)
        // return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/room/{roomCode}")
    public ModelAndView ownRoom(@PathVariable("roomCode") String roomCode, HttpSession session) {
        ModelAndView mav = new ModelAndView("room");
        mav.addObject("roomCode", roomCode);
        String nickname = (String) session.getAttribute("nickname");
        mav.addObject("nickname", nickname);
        return mav;
    }

    @PostMapping("/room/reset-session")
    public ResponseEntity<Void> resetSession(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @MessageMapping("/room/{roomCode}/join")
    public void joinRoom(@DestinationVariable String roomCode, @Payload Map<String, String> payload) {
        String nickname = payload.get("nickname");
        roomPlayers.putIfAbsent(roomCode, new CopyOnWriteArrayList<>());
        roomPlayers.get(roomCode).add(nickname);

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", roomPlayers.get(roomCode));
    }

    @MessageMapping("/room/{roomCode}/chat")
    @SendTo("/topic/room/{roomCode}/chat")
    public ChatMessage sendMessage(@DestinationVariable String roomCode, ChatMessage message) {
        return new ChatMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }

    @MessageMapping("/room/{roomCode}/leave")
    public void leaveRoom(@DestinationVariable String roomCode, Map<String, String> payload) {
        String nickname = HtmlUtils.htmlEscape(payload.get("nickname"));
        CopyOnWriteArrayList<String> players = roomPlayers.get(roomCode);
        if (players != null) {
            players.remove(nickname);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", players);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String roomCode = (String) headerAccessor.getSessionAttributes().get("roomCode");
        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");

        if (roomCode != null && nickname != null) {
            CopyOnWriteArrayList<String> players = roomPlayers.get(roomCode);
            if (players != null) {
                players.remove(nickname);
                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", players);
            }
        }
    }
}
