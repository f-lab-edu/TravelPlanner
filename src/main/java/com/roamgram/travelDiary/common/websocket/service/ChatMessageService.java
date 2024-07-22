package com.roamgram.travelDiary.common.websocket.service;

import com.roamgram.travelDiary.common.websocket.domain.ChatMessage;
import com.roamgram.travelDiary.common.websocket.repository.ChatMessageRepository;
import com.roamgram.travelDiary.common.websocket.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findMessagesByRoomId(UUID roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }
}