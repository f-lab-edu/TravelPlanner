package com.roamgram.travelDiary.common.websocket.domain;

import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import io.jsonwebtoken.Identifiable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK, EXIT, MATCH, MATCH_REQUEST
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String sender;

    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id", nullable = false)
    private ChatRoom chatRoom;

    private Instant timestamp;

}
