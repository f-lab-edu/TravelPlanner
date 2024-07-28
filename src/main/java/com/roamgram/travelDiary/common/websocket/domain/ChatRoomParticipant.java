package com.roamgram.travelDiary.common.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "chatRoomParticipant")
public class ChatRoomParticipant {

    @Id
    private String id;

    @Indexed
    private Long memberId;

    @Indexed
    private Long chatRoomId;

    private LocalDateTime joinedAt;

    public ChatRoomParticipant(Long memberId, Long chatRoomId) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
        this.joinedAt = LocalDateTime.now();
    }
}