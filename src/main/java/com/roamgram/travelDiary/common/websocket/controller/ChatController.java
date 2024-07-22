package com.roamgram.travelDiary.common.websocket.controller;

import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.roamgram.travelDiary.common.permissions.service.ResourcePermissionService;
import com.roamgram.travelDiary.common.websocket.domain.ChatMessage;
import com.roamgram.travelDiary.common.websocket.service.ChatMessageService;
import com.roamgram.travelDiary.common.websocket.service.ChatRoomService;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final AuthUserService authUserService;
    private final ResourcePermissionService resourcePermissionService;
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatController(ChatMessageService chatMessageService, AuthUserServiceImpl authUserService, ResourcePermissionService resourcePermissionService, ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
        this.authUserService = authUserService;
        this.resourcePermissionService = resourcePermissionService;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(Instant.now());
//         Save message to the database
        chatMessageService.save(chatMessage);
        return chatMessage;
    }

    //TODO -> external user is using this method to join the chat room
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage joinChatRoom(@Payload ChatMessage chatMessage) {
        UserProfile user = authUserService.getCurrentUser();

        boolean added = chatRoomService.addUserToRoom(user.getId(), chatMessage.getChatRoom().getId());
        if (!added) {
            throw new IllegalStateException("User already in the chat room");
        }

        chatMessage.setSender(user.getUserProfileName());
        chatMessage.setType(ChatMessage.MessageType.ENTER);
        chatMessage.setTimestamp(Instant.now());

//         Optionally save the message in the database
         chatMessageService.save(chatMessage);

        return chatMessage;
    }

}

