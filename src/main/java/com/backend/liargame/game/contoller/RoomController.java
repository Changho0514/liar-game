package com.backend.liargame.game.contoller;

import com.backend.liargame.chat.ChatMessage;
import com.backend.liargame.common.service.WebSocketService;
import com.backend.liargame.game.dto.NicknameRequest;
import com.backend.liargame.game.dto.VoteRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Slf4j
@Controller
public class RoomController {

    private final WebSocketService webSocketService;
    public RoomController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @GetMapping("/room/create")
    public String createRoomPage(HttpSession session) {
        session.setAttribute("createdRoom", true);  // 방 생성 플래그 설정
        return "create";
    }

    @PostMapping("/room/createRoom")
    public ResponseEntity<String> createRoom(@RequestBody Map<String, String> request, HttpSession session) {
        String nickname = request.get("nickname");
        Boolean createdRoom = (Boolean) session.getAttribute("createdRoom");
        // UUID 생성
        String roomCode = UUID.randomUUID().toString().substring(0, 8);
        session.setAttribute("nickname", nickname); // 세션에 닉네임 저장
        session.setAttribute("createdRoom", createdRoom); // 방 만든 사람

        return new ResponseEntity<>(roomCode, HttpStatus.OK);
    }

    @PostMapping("/room/joinRoom")
    public ResponseEntity<Void> joinRoom(@RequestBody Map<String, String> request, HttpSession session) {
        String nickname = request.get("nickname");
        String roomCode = request.get("roomCode");
        session.setAttribute("nickname", nickname); // 세션에 닉네임 저장
        session.setAttribute("joinRoom", true); // 참여자 플래그 설정
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/room/{roomCode}")
    public ModelAndView ownRoom(@PathVariable("roomCode") String roomCode, HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        Boolean joinRoom = (Boolean) session.getAttribute("joinRoom");
        Boolean createdRoom = (Boolean) session.getAttribute("createdRoom");
        ModelAndView mav;
        log.info("nickname : " + nickname);
        log.info("joinRoom : " + joinRoom);
        log.info("createdRoom : " + createdRoom);

        boolean validUser = false;
        if(createdRoom != null && createdRoom){
            validUser = true;
            session.removeAttribute("createdRoom");
        }
        if (joinRoom == null || !joinRoom) {
            session.removeAttribute("nickname");  // 세션에서 nickname 초기화

        } else {
            session.removeAttribute("joinRoom");  // 참여자 플래그 초기화
            validUser = true;
        }

        if (validUser) {
            mav = new ModelAndView("room");
            mav.addObject("roomCode", roomCode);
            mav.addObject("nickname", nickname);
        } else {
            mav = new ModelAndView("join");
            mav.addObject("roomCode", roomCode);
        }
        return mav;
    }

    @GetMapping("/room/{roomCode}/playerCount")
    public ResponseEntity<Integer> getPlayerCount(@PathVariable String roomCode) {
        int playerCount = webSocketService.getPlayerCount(roomCode);
        log.info(roomCode + "에 연결된 사용자 -> " + playerCount);
        return ResponseEntity.ok(playerCount);
    }


    @PostMapping("/room/reset-session")
    public ResponseEntity<Void> resetSession(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("/room/checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestBody NicknameRequest nicknameRequest) {
        boolean isNicknameTaken = webSocketService.isNicknameTaken(nicknameRequest.roomCode(), nicknameRequest.nickname());
        return ResponseEntity.ok(isNicknameTaken);
    }

    @MessageMapping("/room/{roomCode}/join")
    public void joinRoom(@DestinationVariable String roomCode, @Payload Map<String, String> payload) {
        String nickname = payload.get("nickname");
        webSocketService.addPlayer(roomCode, nickname);
    }

    @MessageMapping("/room/{roomCode}/chat")
    @SendTo("/topic/room/{roomCode}/chat")
    public ChatMessage sendMessage(@DestinationVariable String roomCode, ChatMessage message) {
        return new ChatMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }

    @MessageMapping("/room/{roomCode}/leave")
    public void leaveRoom(@DestinationVariable String roomCode, Map<String, String> payload) {
        String nickname = HtmlUtils.htmlEscape(payload.get("nickname"));
        webSocketService.removePlayer(roomCode, nickname);
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String roomCode = (String) headerAccessor.getSessionAttributes().get("roomCode");
        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");

        log.info("[Room Controller] - [Disconnect event Occur]");
        log.info("roomCode : " + roomCode);
        log.info("nickname : " + nickname);
        log.info("sessionId : " + sessionId);
        if (roomCode != null && nickname != null) {
            webSocketService.removePlayer(roomCode, nickname);
        }
    }
}
