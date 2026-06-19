package org.example.expert.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.chat.entity.ChatMessage;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {

    private Long messageId;
    private Long roomId;
    private String sender;
    private String message;
    private LocalDateTime sentAt;

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                chatMessage.getSender(),
                chatMessage.getMessage(),
                chatMessage.getCreatedAt()
        );
    }
}
