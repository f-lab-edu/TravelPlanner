package com.roamgram.travelDiary.common.websocket.domain;

import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.util.List;
import java.util.UUID;

@Data
@Entity
public class ChatRoom implements IdentifiableResource {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    private Resource resource;
}
