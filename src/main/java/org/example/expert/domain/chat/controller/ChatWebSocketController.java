package org.example.expert.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.request.ChatMessageRequest;
import org.example.expert.domain.chat.dto.response.ChatMessageResponse;
import org.example.expert.domain.chat.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request
    ) {
        ChatMessageResponse response = chatMessageService.saveMessage(roomId, request);

        messagingTemplate.convertAndSend(
                "/sub/chat/rooms/" + roomId,
                response
        );
    }
}
