package com.backend.liargame.game.contoller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.UUID;


@Controller
@RequestMapping("/room")
public class RoomController {

    @GetMapping("/create")
    public String createRoomPage() {
        return "create";
    }

    @PostMapping("/enterRoom")
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

    @GetMapping("/{roomCode}")
    public ModelAndView ownRoom(@PathVariable("roomCode") String roomCode, HttpSession session) {
        ModelAndView mav = new ModelAndView("room");
        mav.addObject("roomCode", roomCode);
        String nickname = (String) session.getAttribute("nickname");
        mav.addObject("nickname", nickname);
        return mav;
    }

    @PostMapping("/reset-session")
    public ResponseEntity<Void> resetSession(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return new ResponseEntity<>(HttpStatus.OK);

    }
}