package com.backend.liargame.game.contoller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Controller
public class RoomController {

    @GetMapping("/create")
    public String createRoomPage() {
        return "create";
    }

    @GetMapping("/create-room")
    public String createRoom(@RequestParam("nickname") String nickname, HttpSession session, Model model) {
        String roomId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        session.setAttribute("roomId", roomId);
        session.setAttribute("nickname", nickname);
        model.addAttribute("roomId", roomId);
        model.addAttribute("nickname", nickname);
        return "room";
    }

    @GetMapping("/room")
    public String room(HttpSession session, Model model) {
        String roomId = (String) session.getAttribute("roomId");
        String nickname = (String) session.getAttribute("nickname");

        if (roomId == null || nickname == null) {
            return "redirect:/";
        }

        model.addAttribute("roomId", roomId);
        model.addAttribute("nickname", nickname);
        return "room";
    }
}
