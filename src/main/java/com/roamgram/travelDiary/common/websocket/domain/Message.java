//package com.roamgram.travelDiary.common.websocket.domain;
//
//import lombok.*;
//
//import java.io.Serializable;
//
//@Getter
//@ToString
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Message implements Serializable {
//
//    private Long chatRoomId;
//
//    private Long senderId;
//
//    private String content;
//
//    private String createdAt;
//    private int readCount;
//
//    private ChatType chatType; // 채팅 타입 필드 추가('TEXT', 'IMAGE')
//
//    private String imageName; // 이미지 파일 이름
//    private String imageUrl; // 이미지 URL
//
//    public void prepareMessageForSending(Long senderId, String createdAt, int readCount) {
//        this.senderId = senderId;
//        this.createdAt = createdAt;
//        this.readCount = readCount;
//    }
//
//    public ChatMessage convertToChatMessage() {
//        return ChatMessage.builder()
//                .chatRoomId(chatRoomId)
//                .senderId(senderId)
//                .content(content)
//                .createdAt(createdAt)
//                .readCount(readCount)
//                .chatType(chatType)
//                .imageName(imageName)
//                .imageUrl(imageUrl)
//                .build();
//    }
//}