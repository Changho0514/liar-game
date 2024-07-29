package com.backend.liargame.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class ChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage send(ChatMessage message) throws Exception {
        return new ChatMessage(HtmlUtils.htmlEscape(message.getName()), HtmlUtils.htmlEscape(message.getContent()));
    }
}
