package org.example.expert.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.chat.entity.ChatRoom;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private String name;
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCreatedAt()
        );
    }
}