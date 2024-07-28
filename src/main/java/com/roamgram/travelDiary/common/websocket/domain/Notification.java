package com.roamgram.travelDiary.common.websocket.domain;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {

    private Long chatRoomId;
    private Long recipientId;
    private String senderNick;
    private String senderProfileImage;
    private LocalDateTime createdDate;
    private String content;
    private NotificationType notificationType;

    public static Notification createReadCountUpdateNotification(Long chatRoomId, Long otherMemberId) {
        return new Notification(chatRoomId,
                otherMemberId,
                null,
                null,
                LocalDateTime.now(),
                null,
                NotificationType.READ_COUNT_UPDATE);
    }
}