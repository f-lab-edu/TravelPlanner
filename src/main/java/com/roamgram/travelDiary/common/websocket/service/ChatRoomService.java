package com.roamgram.travelDiary.common.websocket.service;

import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.domain.ResourcePermission;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.common.permissions.service.ResourcePermissionService;
import com.roamgram.travelDiary.common.permissions.service.ResourceService;
import com.roamgram.travelDiary.common.websocket.domain.ChatMessage;
import com.roamgram.travelDiary.common.websocket.domain.ChatRoom;
import com.roamgram.travelDiary.common.websocket.repository.ChatRoomRepository;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import com.roamgram.travelDiary.repository.persistence.user.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ResourceService resourceService;
    private final ResourcePermissionService resourcePermissionService;
    private final UserProfileRepository userProfileRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, ResourceService resourceService, ResourcePermissionService resourcePermissionService, UserProfileRepository userProfileRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.resourceService = resourceService;
        this.resourcePermissionService = resourcePermissionService;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        ChatRoom chatroom = chatRoomRepository.save(chatRoom);
        Resource resource = resourceService.createResource(chatRoom, "private");
        chatroom.setResource(resource);
        return chatRoomRepository.save(chatRoom);
    }




    @Transactional
    @CheckAccess(resourceType = ChatRoom.class, spelResourceId = "#roomId", permission = "PARTICIPANT")
    public boolean addUserToRoom(UUID userId, UUID roomId) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Chat room not found"));

        Resource chatRoomInternalResource = chatRoom.getResource();
        Optional<ResourcePermission> isUserInRoom = resourcePermissionService.checkIfPermissionExists(chatRoomInternalResource, user);
        if (isUserInRoom.isEmpty()) {
            resourcePermissionService.assignPermission(UserResourcePermissionTypes.PARTICIPANT, chatRoomInternalResource, user);
            chatRoomRepository.save(chatRoom);
            return true;
        } else {
            return false; // User already in the room
        }
    }

}
