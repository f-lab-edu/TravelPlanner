package com.roamgram.travelDiary.common.websocket.domain;

import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom implements IdentifiableResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chat_room_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserProfile sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserProfile recipient;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    private Resource resource;

    public static ChatRoom createNewChatRoom(UserProfile sender, UserProfile recipient) {
        return ChatRoom.builder()
                .sender(sender)
                .recipient(recipient)
                .build();
    }

}
