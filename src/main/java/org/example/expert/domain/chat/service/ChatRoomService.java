package org.example.expert.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.request.ChatRoomCreateRequest;
import org.example.expert.domain.chat.dto.response.ChatRoomResponse;
import org.example.expert.domain.chat.entity.ChatRoom;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoomResponse createRoom(ChatRoomCreateRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new InvalidRequestException("채팅방 이름을 입력해주세요.");
        }

        ChatRoom chatRoom = new ChatRoom(request.getName());
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.from(savedRoom);
    }

    public List<ChatRoomResponse> getRooms() {
        return chatRoomRepository.findAll()
                .stream()
                .map(ChatRoomResponse::from)
                .toList();
    }
}
