package org.example.expert.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.request.ChatMessageRequest;
import org.example.expert.domain.chat.dto.response.ChatMessageResponse;
import org.example.expert.domain.chat.entity.ChatMessage;
import org.example.expert.domain.chat.repository.ChatMessageRepository;
import org.example.expert.domain.chat.repository.ChatRoomRepository;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatMessageResponse saveMessage(Long roomId, ChatMessageRequest request) {
        chatRoomRepository.findById(roomId).orElseThrow(
                () -> new InvalidRequestException("채팅방이 존재하지 않습니다.")
        );

        String sender = StringUtils.hasText(request.getSender()) ? request.getSender() : "익명";

        if (!StringUtils.hasText(request.getMessage())) {
            throw new InvalidRequestException("메시지를 입력해주세요.");
        }

        ChatMessage chatMessage = new ChatMessage(
                roomId,
                sender,
                request.getMessage()
        );

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.from(savedMessage);

    }

    public Page<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new InvalidRequestException("채팅방이 존재하지 않습니다."));

        Pageable pageable = PageRequest.of(page - 1, size);

        return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .map(ChatMessageResponse::from);
    }
}
