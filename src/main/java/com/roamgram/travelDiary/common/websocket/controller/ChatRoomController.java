//package com.roamgram.travelDiary.common.websocket.controller;
//
//import com.roamgram.travelDiary.common.permissions.aop.CheckAccess;
//import com.roamgram.travelDiary.common.permissions.aop.InjectAuthorisedResourceIds;
//import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
//import com.roamgram.travelDiary.common.permissions.service.ResourceService;
//import com.roamgram.travelDiary.common.websocket.domain.ChatMessage;
//import com.roamgram.travelDiary.common.websocket.domain.ChatRoom;
//import com.roamgram.travelDiary.common.websocket.repository.ChatRoomRepository;
//import com.roamgram.travelDiary.common.websocket.service.ChatMessageService;
//import com.roamgram.travelDiary.common.websocket.service.ChatRoomService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/chat")
//public class ChatRoomController {
//    private final ChatRoomRepository chatRoomRepository;
//    private final ChatMessageService chatMessageService;
//    private final ChatRoomService chatRoomService;
//
//    @Autowired
//    public ChatRoomController(ChatRoomRepository chatRoomRepository, ChatMessageService chatMessageService, ResourceService resourceService, ChatRoomService chatRoomService) {
//        this.chatRoomRepository = chatRoomRepository;
//        this.chatMessageService = chatMessageService;
//        this.chatRoomService = chatRoomService;
//    }
//
//    @PostMapping("/rooms")
//    public ChatRoom createRoom(@RequestBody ChatRoom chatRoom) {
//        ChatRoom persistedChatRoom = chatRoomService.createChatRoom(chatRoom);
//        return persistedChatRoom;
//    }
//
//    @GetMapping("/rooms")
//    @InjectAuthorisedResourceIds(parameterName = "resourceIds", resourceType = "ChatRoom", permissionType = UserResourcePermissionTypes.EDITOR)
//    public List<ChatRoom> getAllAuthorisedRooms(List<UUID> resourceIds) {
//        return chatRoomRepository.findAll();
//    }
//
//    @GetMapping("/rooms/{roomId}/messages")
//    @CheckAccess(resourceType = ChatRoom.class, spelResourceId = "#roomId", permission = "EDITOR")
//    public List<ChatMessage> getMessages(@PathVariable UUID roomId) {
//        return chatMessageService.findMessagesByRoomId(roomId);
//    }
//}
