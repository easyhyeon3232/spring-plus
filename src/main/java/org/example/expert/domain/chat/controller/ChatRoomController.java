package org.example.expert.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.chat.dto.request.ChatRoomCreateRequest;
import org.example.expert.domain.chat.dto.response.ChatRoomResponse;
import org.example.expert.domain.chat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/rooms")
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody ChatRoomCreateRequest request) {
        return ResponseEntity.ok(chatRoomService.createRoom(request));
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getRooms() {
        return ResponseEntity.ok(chatRoomService.getRooms());
    }
}
