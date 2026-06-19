package org.example.expert.domain.chat.controller;

import org.example.expert.domain.chat.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @MessageMapping("/chat.send")
    @SendTo("/sub/chatroom")
    public ChatMessageDto sendMessage(ChatMessageDto dto) {
        return dto;
    }
}
