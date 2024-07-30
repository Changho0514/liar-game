package com.backend.liargame.game.contoller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/room")
public class RoomController {

    @GetMapping("/{roomCode}")
    public String room(@PathVariable String roomCode, HttpSession session, Model model) {
        String nickname = (String) session.getAttribute("nickname");
        if (nickname == null || nickname.isEmpty()) {
            return "redirect:/join?roomCode=" + roomCode;
        }

        model.addAttribute("roomCode", roomCode);
        // 닉네임은 세션에서 관리하므로 모델에 추가하지 않습니다.
        return "room";
    }
}